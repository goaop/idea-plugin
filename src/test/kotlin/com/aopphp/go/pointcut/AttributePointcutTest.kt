package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpAttribute
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AttributePointcutTest {

    // ---- canMatchElement ----

    @Test
    fun `matches returns false for unknown element type`() {
        val element = mock<PhpNamedElement>()
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\My\\Attribute")
        assertFalse(pointcut.matches(element))
    }

    @Test
    fun `matches returns false for Method when KIND_METHOD is not in filter`() {
        val method = mock<Method>()
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_PROPERTY), "\\My\\Attribute")
        assertFalse(pointcut.matches(method))
    }

    @Test
    fun `matches returns false for Field when KIND_PROPERTY is not in filter`() {
        val field = mock<Field>()
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\My\\Attribute")
        assertFalse(pointcut.matches(field))
    }

    @Test
    fun `matches returns false for PhpClass when KIND_CLASS is not in filter`() {
        val cls = mock<PhpClass>()
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\My\\Attribute")
        assertFalse(pointcut.matches(cls))
    }

    // ---- getAttributes check ----

    @Test
    fun `matches returns false for Field without the expected attribute`() {
        val field = mock<Field>()
        whenever(field.getAttributes("\\My\\Attribute")).thenReturn(emptyList())
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_PROPERTY), "\\My\\Attribute")
        assertFalse(pointcut.matches(field))
    }

    @Test
    fun `matches returns true for Field with the expected attribute`() {
        val field = mock<Field>()
        val attr = mock<PhpAttribute>()
        whenever(field.getAttributes("\\My\\Attribute")).thenReturn(listOf(attr))
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_PROPERTY), "\\My\\Attribute")
        assertTrue(pointcut.matches(field))
    }

    @Test
    fun `matches returns true for PhpClass with the expected attribute`() {
        val cls = mock<PhpClass>()
        val attr = mock<PhpAttribute>()
        whenever(cls.getAttributes("\\My\\Attribute")).thenReturn(listOf(attr))
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_CLASS), "\\My\\Attribute")
        assertTrue(pointcut.matches(cls))
    }

    @Test
    fun `matches returns false for PhpClass without the expected attribute`() {
        val cls = mock<PhpClass>()
        whenever(cls.getAttributes("\\My\\Attribute")).thenReturn(emptyList())
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_CLASS), "\\My\\Attribute")
        assertFalse(pointcut.matches(cls))
    }

    @Test
    fun `matches Method with expected attribute when KIND_METHOD in filter`() {
        val method = mock<Method>()
        val attr = mock<PhpAttribute>()
        whenever(method.getAttributes("\\Route")).thenReturn(listOf(attr))
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\Route")
        assertTrue(pointcut.matches(method))
    }

    // ---- getClassFilter ----

    @Test
    fun `getClassFilter matches returns false for non-PhpClass element`() {
        val pointcut = AttributePointcut(setOf(KindFilter.KIND_CLASS), "\\My\\Attribute")
        val classFilter = pointcut.getClassFilter()
        val element = mock<PhpNamedElement>()
        assertFalse(classFilter.matches(element))
    }

    // ---- getKind ----

    @Test
    fun `getKind returns the provided kind set`() {
        val kinds = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_CLASS)
        val pointcut = AttributePointcut(kinds, "\\My\\Attribute")
        assertEquals(kinds, pointcut.getKind())
    }

    // ---- equals / hashCode ----

    @Test
    fun `two pointcuts with same kind and class are equal`() {
        val a = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\My\\Attribute")
        val b = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\My\\Attribute")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two pointcuts with different expected class are not equal`() {
        val a = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\My\\Attribute")
        val b = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\Other\\Attribute")
        assertNotEquals(a, b)
    }

    @Test
    fun `two pointcuts with different kinds are not equal`() {
        val a = AttributePointcut(setOf(KindFilter.KIND_METHOD), "\\My\\Attribute")
        val b = AttributePointcut(setOf(KindFilter.KIND_CLASS), "\\My\\Attribute")
        assertNotEquals(a, b)
    }
}
