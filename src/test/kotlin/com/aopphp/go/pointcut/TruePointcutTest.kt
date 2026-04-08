package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class TruePointcutTest {

    @Test
    fun `matches always returns true`() {
        val pointcut = TruePointcut()
        val element = mock<PhpNamedElement>()
        assertTrue(pointcut.matches(element))
    }

    @Test
    fun `getKind returns all kinds by default`() {
        val pointcut = TruePointcut()
        assertEquals(KindFilter.entries.toSet(), pointcut.getKind())
    }

    @Test
    fun `getKind returns custom kind set when specified`() {
        val kinds = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_PROPERTY)
        val pointcut = TruePointcut(kinds)
        assertEquals(kinds, pointcut.getKind())
    }

    @Test
    fun `getClassFilter returns TruePointFilter by default`() {
        val pointcut = TruePointcut()
        assertSame(TruePointFilter, pointcut.getClassFilter())
    }

    @Test
    fun `getClassFilter returns custom filter when provided`() {
        val customFilter = mock<PointFilter>()
        val pointcut = TruePointcut(_classFilter = customFilter)
        assertSame(customFilter, pointcut.getClassFilter())
    }

    @Test
    fun `two TruePointcuts with same kind and filter are equal`() {
        val kinds = setOf(KindFilter.KIND_METHOD)
        val a = TruePointcut(kinds)
        val b = TruePointcut(kinds)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two TruePointcuts with different kinds are not equal`() {
        val a = TruePointcut(setOf(KindFilter.KIND_METHOD))
        val b = TruePointcut(setOf(KindFilter.KIND_CLASS))
        assertNotEquals(a, b)
    }

    @Test
    fun `TruePointcut is not equal to null or other type`() {
        val pointcut = TruePointcut()
        assertNotEquals(pointcut, null)
        assertNotEquals(pointcut, "string")
    }

    @Test
    fun `classFilter can be set via JvmName setter`() {
        val pointcut = TruePointcut()
        val newFilter = mock<PointFilter>()
        pointcut._classFilter = newFilter
        assertSame(newFilter, pointcut.getClassFilter())
    }
}
