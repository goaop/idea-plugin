package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class OrPointFilterTest {

    private val element = mock<PhpNamedElement>()

    @Test
    fun `matches returns true when both filters match`() {
        val first = mockFilterReturning(true)
        val second = mockFilterReturning(true)
        assertTrue(OrPointFilter(first, second).matches(element))
    }

    @Test
    fun `matches returns true when only first filter matches`() {
        val first = mockFilterReturning(true)
        val second = mockFilterReturning(false)
        assertTrue(OrPointFilter(first, second).matches(element))
    }

    @Test
    fun `matches returns true when only second filter matches`() {
        val first = mockFilterReturning(false)
        val second = mockFilterReturning(true)
        assertTrue(OrPointFilter(first, second).matches(element))
    }

    @Test
    fun `matches returns false when neither filter matches`() {
        val first = mockFilterReturning(false)
        val second = mockFilterReturning(false)
        assertFalse(OrPointFilter(first, second).matches(element))
    }

    @Test
    fun `getKind returns union of both filter kinds`() {
        val first = mockFilterWithKind(setOf(KindFilter.KIND_METHOD))
        val second = mockFilterWithKind(setOf(KindFilter.KIND_CLASS))
        val filter = OrPointFilter(first, second)
        assertEquals(setOf(KindFilter.KIND_METHOD, KindFilter.KIND_CLASS), filter.getKind())
    }

    @Test
    fun `getKind with overlapping kinds has no duplicates`() {
        val both = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_CLASS)
        val first = mockFilterWithKind(both)
        val second = mockFilterWithKind(both)
        assertEquals(both, OrPointFilter(first, second).getKind())
    }

    @Test
    fun `two equal OrPointFilters are equal with same hashCode`() {
        val first = mockFilterWithKind(setOf(KindFilter.KIND_METHOD))
        val second = mockFilterWithKind(setOf(KindFilter.KIND_CLASS))
        val a = OrPointFilter(first, second)
        val b = OrPointFilter(first, second)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `not equal to AndPointFilter with same delegates`() {
        val first = mockFilterReturning(true)
        val second = mockFilterReturning(true)
        val or = OrPointFilter(first, second)
        val and = AndPointFilter(first, second)
        assertNotEquals(or, and)
    }

    private fun mockFilterReturning(result: Boolean): PointFilter {
        val filter = mock<PointFilter>()
        whenever(filter.matches(element)).thenReturn(result)
        whenever(filter.getKind()).thenReturn(KindFilter.entries.toSet())
        return filter
    }

    private fun mockFilterWithKind(kinds: Set<KindFilter>): PointFilter {
        val filter = mock<PointFilter>()
        whenever(filter.getKind()).thenReturn(kinds)
        whenever(filter.matches(element)).thenReturn(true)
        return filter
    }
}
