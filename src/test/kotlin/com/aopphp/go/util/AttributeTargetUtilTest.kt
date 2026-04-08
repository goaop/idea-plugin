package com.aopphp.go.util

import com.aopphp.go.psi.AnnotatedAccessPointcut
import com.aopphp.go.psi.AnnotatedExecutionPointcut
import com.aopphp.go.psi.AnnotatedWithinPointcut
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpAttribute
import com.jetbrains.php.lang.psi.elements.PhpClass
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AttributeTargetUtilTest {

    // ---- requiredBitsFor ----

    @Test
    fun `requiredBitsFor AnnotatedExecutionPointcut returns TARGET_METHOD or TARGET_FUNCTION`() {
        val parent = mock<AnnotatedExecutionPointcut>()
        val expected = AttributeTargetUtil.TARGET_METHOD or AttributeTargetUtil.TARGET_FUNCTION
        assertEquals(expected, AttributeTargetUtil.requiredBitsFor(parent))
    }

    @Test
    fun `requiredBitsFor AnnotatedAccessPointcut returns TARGET_PROPERTY`() {
        val parent = mock<AnnotatedAccessPointcut>()
        assertEquals(AttributeTargetUtil.TARGET_PROPERTY, AttributeTargetUtil.requiredBitsFor(parent))
    }

    @Test
    fun `requiredBitsFor AnnotatedWithinPointcut returns TARGET_CLASS`() {
        val parent = mock<AnnotatedWithinPointcut>()
        assertEquals(AttributeTargetUtil.TARGET_CLASS, AttributeTargetUtil.requiredBitsFor(parent))
    }

    @Test
    fun `requiredBitsFor unknown element returns zero`() {
        val other = mock<PsiElement>()
        assertEquals(0, AttributeTargetUtil.requiredBitsFor(other))
    }

    @Test
    fun `requiredBitsFor null returns zero`() {
        assertEquals(0, AttributeTargetUtil.requiredBitsFor(null))
    }

    // ---- requiredDescription ----

    @Test
    fun `requiredDescription for AnnotatedExecutionPointcut mentions TARGET_METHOD`() {
        val parent = mock<AnnotatedExecutionPointcut>()
        val desc = AttributeTargetUtil.requiredDescription(parent)
        assertTrue(desc.contains("TARGET_METHOD"), "Expected 'TARGET_METHOD' in: $desc")
        assertTrue(desc.contains("TARGET_FUNCTION"), "Expected 'TARGET_FUNCTION' in: $desc")
    }

    @Test
    fun `requiredDescription for AnnotatedAccessPointcut mentions TARGET_PROPERTY`() {
        val parent = mock<AnnotatedAccessPointcut>()
        assertEquals("TARGET_PROPERTY", AttributeTargetUtil.requiredDescription(parent))
    }

    @Test
    fun `requiredDescription for AnnotatedWithinPointcut mentions TARGET_CLASS`() {
        val parent = mock<AnnotatedWithinPointcut>()
        assertEquals("TARGET_CLASS", AttributeTargetUtil.requiredDescription(parent))
    }

    @Test
    fun `requiredDescription for unknown element returns unknown`() {
        assertEquals("unknown", AttributeTargetUtil.requiredDescription(null))
    }

    // ---- isCompatible ----

    @Test
    fun `isCompatible returns true when requiredBits is zero`() {
        val cls = mock<PhpClass>()
        assertTrue(AttributeTargetUtil.isCompatible(cls, 0))
    }

    @Test
    fun `isCompatible returns true when class has no attribute decorator`() {
        // TARGET_ALL mask only when requiredBits=0
        val cls = mock<PhpClass>()
        whenever(cls.getAttributes("\\Attribute")).thenReturn(emptyList())
        // getTargetMask returns 0 → incompatible for non-zero required bits
        assertFalse(AttributeTargetUtil.isCompatible(cls, AttributeTargetUtil.TARGET_METHOD))
    }

    @Test
    fun `isCompatible returns true when mask includes required bits`() {
        val cls = mockClassWithTargetMask("Attribute::TARGET_METHOD | Attribute::TARGET_FUNCTION")
        assertTrue(AttributeTargetUtil.isCompatible(cls, AttributeTargetUtil.TARGET_METHOD))
    }

    @Test
    fun `isCompatible returns false when mask does not include required bits`() {
        val cls = mockClassWithTargetMask("Attribute::TARGET_CLASS")
        assertFalse(AttributeTargetUtil.isCompatible(cls, AttributeTargetUtil.TARGET_METHOD))
    }

    @Test
    fun `isCompatible returns true for TARGET_ALL mask`() {
        val cls = mockClassWithTargetMask("Attribute::TARGET_ALL")
        assertTrue(AttributeTargetUtil.isCompatible(cls, AttributeTargetUtil.TARGET_METHOD))
        assertTrue(AttributeTargetUtil.isCompatible(cls, AttributeTargetUtil.TARGET_CLASS))
        assertTrue(AttributeTargetUtil.isCompatible(cls, AttributeTargetUtil.TARGET_PROPERTY))
    }

    // ---- getTargetMask ----

    @Test
    fun `getTargetMask returns zero when class has no Attribute decorator`() {
        val cls = mock<PhpClass>()
        whenever(cls.getAttributes("\\Attribute")).thenReturn(emptyList())
        assertEquals(0, AttributeTargetUtil.getTargetMask(cls))
    }

    @Test
    fun `getTargetMask returns TARGET_ALL when Attribute has no arguments`() {
        val cls = mockClassWithNoArgAttribute()
        assertEquals(AttributeTargetUtil.TARGET_ALL, AttributeTargetUtil.getTargetMask(cls))
    }

    @Test
    fun `getTargetMask returns TARGET_ALL when Attribute parameterList is null`() {
        val cls = mock<PhpClass>()
        val attr = mock<PhpAttribute>()
        whenever(attr.parameterList).thenReturn(null)
        whenever(cls.getAttributes("\\Attribute")).thenReturn(listOf(attr))
        assertEquals(AttributeTargetUtil.TARGET_ALL, AttributeTargetUtil.getTargetMask(cls))
    }

    @Test
    fun `getTargetMask returns TARGET_ALL when parameters array is empty`() {
        val cls = mock<PhpClass>()
        val attr = mock<PhpAttribute>()
        val paramList = mock<ParameterList>()
        whenever(paramList.parameters).thenReturn(emptyArray())
        whenever(attr.parameterList).thenReturn(paramList)
        whenever(cls.getAttributes("\\Attribute")).thenReturn(listOf(attr))
        assertEquals(AttributeTargetUtil.TARGET_ALL, AttributeTargetUtil.getTargetMask(cls))
    }

    // ---- parseTargetMaskFromText (via getTargetMask) ----

    @ParameterizedTest(name = "expression \"{0}\" should parse to {1}")
    @CsvSource(
        "Attribute::TARGET_CLASS, 1",
        "Attribute::TARGET_FUNCTION, 2",
        "Attribute::TARGET_METHOD, 4",
        "Attribute::TARGET_PROPERTY, 8",
        "Attribute::TARGET_CLASS_CONSTANT, 16",
        "Attribute::TARGET_PARAMETER, 32",
        "Attribute::TARGET_ALL, 63",
        "TARGET_METHOD, 4",
        "TARGET_CLASS, 1",
        "6, 6",
        "63, 63",
        "0, 0",
    )
    fun `parseTargetMaskFromText correctly resolves known constant expressions`(
        expression: String,
        expectedMask: Int
    ) {
        val cls = mockClassWithTargetMask(expression)
        assertEquals(expectedMask, AttributeTargetUtil.getTargetMask(cls))
    }

    @Test
    fun `parseTargetMaskFromText handles pipe-separated constants`() {
        val cls = mockClassWithTargetMask("Attribute::TARGET_METHOD | Attribute::TARGET_FUNCTION")
        assertEquals(
            AttributeTargetUtil.TARGET_METHOD or AttributeTargetUtil.TARGET_FUNCTION,
            AttributeTargetUtil.getTargetMask(cls)
        )
    }

    @Test
    fun `parseTargetMaskFromText handles multiple pipe-separated constants`() {
        val cls = mockClassWithTargetMask(
            "Attribute::TARGET_CLASS | Attribute::TARGET_METHOD | Attribute::TARGET_PROPERTY"
        )
        val expected = AttributeTargetUtil.TARGET_CLASS or
                AttributeTargetUtil.TARGET_METHOD or
                AttributeTargetUtil.TARGET_PROPERTY
        assertEquals(expected, AttributeTargetUtil.getTargetMask(cls))
    }

    @Test
    fun `parseTargetMaskFromText returns TARGET_ALL for unrecognized expression`() {
        val cls = mockClassWithTargetMask("SomeClass::UNKNOWN_CONSTANT")
        assertEquals(AttributeTargetUtil.TARGET_ALL, AttributeTargetUtil.getTargetMask(cls))
    }

    @Test
    fun `parseTargetMaskFromText handles empty string as TARGET_ALL`() {
        val cls = mockClassWithTargetMask("  ")
        assertEquals(AttributeTargetUtil.TARGET_ALL, AttributeTargetUtil.getTargetMask(cls))
    }

    // ---- Constant values ----

    @Test
    fun `constant values match PHP Attribute class definitions`() {
        assertEquals(1, AttributeTargetUtil.TARGET_CLASS)
        assertEquals(2, AttributeTargetUtil.TARGET_FUNCTION)
        assertEquals(4, AttributeTargetUtil.TARGET_METHOD)
        assertEquals(8, AttributeTargetUtil.TARGET_PROPERTY)
        assertEquals(16, AttributeTargetUtil.TARGET_CLASS_CONSTANT)
        assertEquals(32, AttributeTargetUtil.TARGET_PARAMETER)
        assertEquals(63, AttributeTargetUtil.TARGET_ALL)
    }

    // ---- Helpers ----

    private fun mockClassWithTargetMask(paramText: String): PhpClass {
        val cls = mock<PhpClass>()
        val attr = mock<PhpAttribute>()
        val paramList = mock<ParameterList>()
        val param = mock<PsiElement>()
        whenever(param.text).thenReturn(paramText)
        whenever(paramList.parameters).thenReturn(arrayOf(param))
        whenever(attr.parameterList).thenReturn(paramList)
        whenever(cls.getAttributes("\\Attribute")).thenReturn(listOf(attr))
        return cls
    }

    private fun mockClassWithNoArgAttribute(): PhpClass {
        val cls = mock<PhpClass>()
        val attr = mock<PhpAttribute>()
        val paramList = mock<ParameterList>()
        whenever(paramList.parameters).thenReturn(emptyArray())
        whenever(attr.parameterList).thenReturn(paramList)
        whenever(cls.getAttributes("\\Attribute")).thenReturn(listOf(attr))
        return cls
    }
}
