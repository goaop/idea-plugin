package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AndPointFilterTest {

    private val element = mock<PhpNamedElement>()

    @Test
    fun `matches returns true when both filters match`() {
        val first = mockFilterReturning(true)
        val second = mockFilterReturning(true)
        assertTrue(AndPointFilter(first, second).matches(element))
    }

    @Test
    fun `matches returns false when first filter does not match`() {
        val first = mockFilterReturning(false)
        val second = mockFilterReturning(true)
        assertFalse(AndPointFilter(first, second).matches(element))
    }

    @Test
    fun `matches returns false when second filter does not match`() {
        val first = mockFilterReturning(true)
        val second = mockFilterReturning(false)
        assertFalse(AndPointFilter(first, second).matches(element))
    }

    @Test
    fun `matches returns false when neither filter matches`() {
        val first = mockFilterReturning(false)
        val second = mockFilterReturning(false)
        assertFalse(AndPointFilter(first, second).matches(element))
    }

    @Test
    fun `getKind returns intersection of both filter kinds`() {
        val first = mockFilterWithKind(setOf(KindFilter.KIND_METHOD, KindFilter.KIND_CLASS))
        val second = mockFilterWithKind(setOf(KindFilter.KIND_CLASS, KindFilter.KIND_PROPERTY))
        val filter = AndPointFilter(first, second)
        assertEquals(setOf(KindFilter.KIND_CLASS), filter.getKind())
    }

    @Test
    fun `getKind returns empty set when no kinds in common`() {
        val first = mockFilterWithKind(setOf(KindFilter.KIND_METHOD))
        val second = mockFilterWithKind(setOf(KindFilter.KIND_CLASS))
        val filter = AndPointFilter(first, second)
        assertTrue(filter.getKind().isEmpty())
    }

    @Test
    fun `two equal AndPointFilters produce same hashCode`() {
        val first = mockFilterWithKind(setOf(KindFilter.KIND_METHOD))
        val second = mockFilterWithKind(setOf(KindFilter.KIND_METHOD))
        val a = AndPointFilter(first, second)
        val b = AndPointFilter(first, second)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `not equal when wrapping different filters`() {
        val f1 = mockFilterReturning(true)
        val f2 = mockFilterReturning(false)
        val a = AndPointFilter(f1, f1)
        val b = AndPointFilter(f2, f2)
        assertNotEquals(a, b)
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
