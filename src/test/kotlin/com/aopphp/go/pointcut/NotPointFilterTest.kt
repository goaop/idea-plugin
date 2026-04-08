package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NotPointFilterTest {

    private val element = mock<PhpNamedElement>()

    @Test
    fun `matches returns false when wrapped filter matches`() {
        val inner = mockFilterReturning(true)
        assertFalse(NotPointFilter(inner).matches(element))
    }

    @Test
    fun `matches returns true when wrapped filter does not match`() {
        val inner = mockFilterReturning(false)
        assertTrue(NotPointFilter(inner).matches(element))
    }

    @Test
    fun `getKind delegates to wrapped filter`() {
        val kinds = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_PROPERTY)
        val inner = mock<PointFilter>()
        whenever(inner.getKind()).thenReturn(kinds)
        assertEquals(kinds, NotPointFilter(inner).getKind())
    }

    @Test
    fun `two NotPointFilters wrapping same filter are equal`() {
        val inner = mock<PointFilter>()
        whenever(inner.getKind()).thenReturn(setOf(KindFilter.KIND_METHOD))
        val a = NotPointFilter(inner)
        val b = NotPointFilter(inner)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `not equal when wrapping different filters`() {
        val f1 = mockFilterReturning(true)
        val f2 = mockFilterReturning(false)
        assertNotEquals(NotPointFilter(f1), NotPointFilter(f2))
    }

    @Test
    fun `not equal to non-NotPointFilter`() {
        val inner = mockFilterReturning(true)
        assertNotEquals(NotPointFilter(inner), inner)
    }

    private fun mockFilterReturning(result: Boolean): PointFilter {
        val filter = mock<PointFilter>()
        whenever(filter.matches(element)).thenReturn(result)
        whenever(filter.getKind()).thenReturn(KindFilter.entries.toSet())
        return filter
    }
}
