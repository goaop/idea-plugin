package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class KindFilterTest {

    @Test
    fun `all expected kind filter values exist`() {
        val values = KindFilter.entries
        assertTrue(values.contains(KindFilter.KIND_METHOD))
        assertTrue(values.contains(KindFilter.KIND_PROPERTY))
        assertTrue(values.contains(KindFilter.KIND_CLASS))
        assertTrue(values.contains(KindFilter.KIND_TRAIT))
        assertTrue(values.contains(KindFilter.KIND_FUNCTION))
        assertTrue(values.contains(KindFilter.KIND_INIT))
        assertTrue(values.contains(KindFilter.KIND_STATIC_INIT))
        assertTrue(values.contains(KindFilter.KIND_DYNAMIC))
    }

    @Test
    fun `enum has exactly eight values`() {
        assertEquals(8, KindFilter.entries.size)
    }

    @Test
    fun `valueOf returns correct enum constant`() {
        assertEquals(KindFilter.KIND_METHOD, KindFilter.valueOf("KIND_METHOD"))
        assertEquals(KindFilter.KIND_CLASS, KindFilter.valueOf("KIND_CLASS"))
        assertEquals(KindFilter.KIND_PROPERTY, KindFilter.valueOf("KIND_PROPERTY"))
    }

    @Test
    fun `all entries form a full set`() {
        val allKinds = KindFilter.entries.toSet()
        assertEquals(8, allKinds.size)
    }

    // ---- supports ----

    @Test
    fun `supports returns true for Method when set contains KIND_METHOD`() {
        assertTrue(setOf(KindFilter.KIND_METHOD).supports(mock<Method>()))
    }

    @Test
    fun `supports returns false for Method when set lacks KIND_METHOD`() {
        assertFalse(setOf(KindFilter.KIND_PROPERTY, KindFilter.KIND_CLASS).supports(mock<Method>()))
    }

    @Test
    fun `supports returns true for Field when set contains KIND_PROPERTY`() {
        assertTrue(setOf(KindFilter.KIND_PROPERTY).supports(mock<Field>()))
    }

    @Test
    fun `supports returns false for Field when set lacks KIND_PROPERTY`() {
        assertFalse(setOf(KindFilter.KIND_METHOD, KindFilter.KIND_CLASS).supports(mock<Field>()))
    }

    @Test
    fun `supports returns true for PhpClass when set contains KIND_CLASS`() {
        assertTrue(setOf(KindFilter.KIND_CLASS).supports(mock<PhpClass>()))
    }

    @Test
    fun `supports returns false for PhpClass when set lacks KIND_CLASS`() {
        assertFalse(setOf(KindFilter.KIND_METHOD, KindFilter.KIND_PROPERTY).supports(mock<PhpClass>()))
    }

    @Test
    fun `supports returns false for plain PhpNamedElement even with all kinds`() {
        assertFalse(KindFilter.entries.toSet().supports(mock<PhpNamedElement>()))
    }

    @Test
    fun `empty kind set supports nothing`() {
        assertFalse(emptySet<KindFilter>().supports(mock<Method>()))
        assertFalse(emptySet<KindFilter>().supports(mock<Field>()))
        assertFalse(emptySet<KindFilter>().supports(mock<PhpClass>()))
    }
}
