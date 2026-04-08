package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AndPointcutTest {

    @Test
    fun `matches returns true when both pointcuts match and class filters pass`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val first = mockPointcutMatching(member, cls, true)
        val second = mockPointcutMatching(member, cls, true)
        assertTrue(AndPointcut(first, second).matches(member))
    }

    @Test
    fun `matches returns false when first pointcut does not match`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val first = mockPointcutMatching(member, cls, false)
        val second = mockPointcutMatching(member, cls, true)
        assertFalse(AndPointcut(first, second).matches(member))
    }

    @Test
    fun `matches returns false when second pointcut does not match`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val first = mockPointcutMatching(member, cls, true)
        val second = mockPointcutMatching(member, cls, false)
        assertFalse(AndPointcut(first, second).matches(member))
    }

    @Test
    fun `matches returns false for non-PhpClassMember element`() {
        val element = mock<PhpNamedElement>()
        val first = mockTruePointcut()
        val second = mockTruePointcut()
        assertFalse(AndPointcut(first, second).matches(element))
    }

    @Test
    fun `matches returns false when containingClass is null`() {
        val member = mock<PhpClassMember>()
        whenever(member.containingClass).thenReturn(null)
        val first = mockTruePointcut()
        val second = mockTruePointcut()
        assertFalse(AndPointcut(first, second).matches(member))
    }

    @Test
    fun `getKind returns intersection of both pointcut kinds`() {
        val first = mockPointcutWithKind(setOf(KindFilter.KIND_METHOD, KindFilter.KIND_CLASS))
        val second = mockPointcutWithKind(setOf(KindFilter.KIND_CLASS, KindFilter.KIND_PROPERTY))
        val pointcut = AndPointcut(first, second)
        assertEquals(setOf(KindFilter.KIND_CLASS), pointcut.getKind())
    }

    @Test
    fun `getClassFilter returns AndPointFilter combining both class filters`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val first = mockPointcutMatching(member, cls, true)
        val second = mockPointcutMatching(member, cls, true)
        val classFilter = AndPointcut(first, second).getClassFilter()
        assertNotNull(classFilter)
        assertTrue(classFilter is AndPointFilter)
    }

    @Test
    fun `two AndPointcuts with same wrapped pointcuts are equal`() {
        val first = mockTruePointcut()
        val second = mockTruePointcut()
        val a = AndPointcut(first, second)
        val b = AndPointcut(first, second)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `not equal to non-AndPointcut`() {
        val pointcut = AndPointcut(mockTruePointcut(), mockTruePointcut())
        assertNotEquals(pointcut, TruePointFilter)
    }

    // ---- Helpers ----

    private fun mockMemberInClass(cls: PhpClass): PhpClassMember {
        val member = mock<PhpClassMember>()
        whenever(member.containingClass).thenReturn(cls)
        return member
    }

    private fun mockPointcutMatching(member: PhpClassMember, cls: PhpClass, matches: Boolean): Pointcut {
        val classFilter = mock<PointFilter>()
        whenever(classFilter.matches(cls)).thenReturn(matches)
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
