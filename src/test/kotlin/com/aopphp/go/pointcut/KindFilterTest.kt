package com.aopphp.go.pointcut

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

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
}
