package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NotPointcutTest {

    @Test
    fun `matches returns false for non-PhpClassMember element`() {
        val element = mock<PhpNamedElement>()
        val inner = mockTruePointcut()
        assertFalse(NotPointcut(inner).matches(element))
    }

    @Test
    fun `matches returns false when containingClass is null`() {
        val member = mock<PhpClassMember>()
        whenever(member.containingClass).thenReturn(null)
        val inner = mockTruePointcut()
        assertFalse(NotPointcut(inner).matches(member))
    }

    @Test
    fun `matches returns false when inner pointcut matches and class filter matches`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val inner = mockPointcutMatching(member, cls, true)
        assertFalse(NotPointcut(inner).matches(member))
    }

    @Test
    fun `matches returns true when inner pointcut does not match`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val inner = mockPointcutMatching(member, cls, false)
        assertTrue(NotPointcut(inner).matches(member))
    }

    @Test
    fun `matches returns true when class filter does not match (element outside pointcut scope)`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val classFilter = mock<PointFilter>()
        whenever(classFilter.matches(cls)).thenReturn(false)
        whenever(classFilter.getKind()).thenReturn(KindFilter.entries.toSet())
        val inner = mock<Pointcut>()
        whenever(inner.getClassFilter()).thenReturn(classFilter)
        whenever(inner.getKind()).thenReturn(KindFilter.entries.toSet())
        // Class filter says "not in scope" → NotPointcut returns true
        assertTrue(NotPointcut(inner).matches(member))
    }

    @Test
    fun `getKind returns kind from wrapped pointcut`() {
        val kinds = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_PROPERTY)
        val inner = mockPointcutWithKind(kinds)
        assertEquals(kinds, NotPointcut(inner).getKind())
    }

    @Test
    fun `getClassFilter returns TruePointFilter`() {
        val inner = mockTruePointcut()
        assertSame(TruePointFilter, NotPointcut(inner).getClassFilter())
    }

    @Test
    fun `two NotPointcuts wrapping same pointcut are equal`() {
        val inner = mockTruePointcut()
        val a = NotPointcut(inner)
        val b = NotPointcut(inner)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `not equal when wrapping different pointcuts`() {
        val inner1 = mockPointcutWithKind(setOf(KindFilter.KIND_METHOD))
        val inner2 = mockPointcutWithKind(setOf(KindFilter.KIND_CLASS))
        assertNotEquals(NotPointcut(inner1), NotPointcut(inner2))
    }

    @Test
    fun `not equal to non-NotPointcut`() {
        val inner = mockTruePointcut()
        assertNotEquals(NotPointcut(inner), TruePointFilter)
    }

    // ---- Helpers ----

    private fun mockMemberInClass(cls: PhpClass): PhpClassMember {
        val member = mock<PhpClassMember>()
        whenever(member.containingClass).thenReturn(cls)
        return member
    }

    private fun mockPointcutMatching(member: PhpClassMember, cls: PhpClass, matches: Boolean): Pointcut {
        val classFilter = mock<PointFilter>()
        whenever(classFilter.matches(cls)).thenReturn(true)
        whenever(classFilter.getKind()).thenReturn(KindFilter.entries.toSet())
        val pointcut = mock<Pointcut>()
        whenever(pointcut.matches(member)).thenReturn(matches)
        whenever(pointcut.getClassFilter()).thenReturn(classFilter)
        whenever(pointcut.getKind()).thenReturn(KindFilter.entries.toSet())
        return pointcut
    }

    private fun mockTruePointcut(): Pointcut {
        val pointcut = mock<Pointcut>()
        whenever(pointcut.getClassFilter()).thenReturn(TruePointFilter)
        whenever(pointcut.getKind()).thenReturn(KindFilter.entries.toSet())
        return pointcut
    }

    private fun mockPointcutWithKind(kinds: Set<KindFilter>): Pointcut {
        val pointcut = mock<Pointcut>()
        whenever(pointcut.getClassFilter()).thenReturn(TruePointFilter)
        whenever(pointcut.getKind()).thenReturn(kinds)
        return pointcut
    }
}
