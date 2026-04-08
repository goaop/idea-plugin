package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class TruePointFilterTest {

    @Test
    fun `matches always returns true for any element`() {
        val element = mock<PhpNamedElement>()
        assertTrue(TruePointFilter.matches(element))
    }

    @Test
    fun `getKind returns all KindFilter values`() {
        val kinds = TruePointFilter.getKind()
        assertEquals(KindFilter.entries.toSet(), kinds)
    }

    @Test
    fun `is a singleton object`() {
        assertSame(TruePointFilter, TruePointFilter.getInstance())
    }

    @Test
    fun `equals another TruePointFilter instance`() {
        assertEquals(TruePointFilter, TruePointFilter)
    }

    @Test
    fun `hashCode is stable and equals zero`() {
        assertEquals(0, TruePointFilter.hashCode())
        assertEquals(TruePointFilter.hashCode(), TruePointFilter.hashCode())
    }

    @Test
    fun `does not equal non-TruePointFilter objects`() {
        val other = mock<PointFilter>()
        assertNotEquals(TruePointFilter, other)
    }
}
