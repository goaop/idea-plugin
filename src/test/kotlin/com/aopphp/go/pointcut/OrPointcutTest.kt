package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class OrPointcutTest {

    @Test
    fun `matches returns true when both pointcuts match`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val first = mockPointcutMatching(member, cls, true)
        val second = mockPointcutMatching(member, cls, true)
        assertTrue(OrPointcut(first, second).matches(member))
    }

    @Test
    fun `matches returns true when only first pointcut matches`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val first = mockPointcutMatching(member, cls, true)
        val second = mockPointcutMatching(member, cls, false)
        assertTrue(OrPointcut(first, second).matches(member))
    }

    @Test
    fun `matches returns true when only second pointcut matches`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val first = mockPointcutMatching(member, cls, false)
        val second = mockPointcutMatching(member, cls, true)
        assertTrue(OrPointcut(first, second).matches(member))
    }

    @Test
    fun `matches returns false when neither pointcut matches`() {
        val cls = mock<PhpClass>()
        val member = mockMemberInClass(cls)
        val first = mockPointcutMatching(member, cls, false)
        val second = mockPointcutMatching(member, cls, false)
        assertFalse(OrPointcut(first, second).matches(member))
    }

    @Test
    fun `matches returns false for non-PhpClassMember element`() {
        val element = mock<PhpNamedElement>()
        val first = mockTruePointcut()
        val second = mockTruePointcut()
        assertFalse(OrPointcut(first, second).matches(element))
    }

    @Test
    fun `matches returns false when containingClass is null`() {
        val member = mock<Method>()
        whenever(member.containingClass).thenReturn(null)
        val first = mockTruePointcut()
        val second = mockTruePointcut()
        assertFalse(OrPointcut(first, second).matches(member))
    }

    @Test
    fun `matches returns false when no child pointcut kind supports element kind`() {
        val cls = mock<PhpClass>()
        val field = mock<Field>()
        whenever(field.containingClass).thenReturn(cls)
        val first = mockPointcutMatching(field, cls, true, setOf(KindFilter.KIND_METHOD))
        val second = mockPointcutMatching(field, cls, true, setOf(KindFilter.KIND_METHOD))
        assertFalse(OrPointcut(first, second).matches(field))
        // The kind gate must short-circuit before either child pointcut is consulted
        verify(first, never()).matches(any())
        verify(second, never()).matches(any())
    }

    @Test
    fun `matches consults only the child whose kind supports the element`() {
        val cls = mock<PhpClass>()
        val field = mock<Field>()
        whenever(field.containingClass).thenReturn(cls)
        val methodOnly = mockPointcutMatching(field, cls, true, setOf(KindFilter.KIND_METHOD))
        val propertyOnly = mockPointcutMatching(field, cls, true, setOf(KindFilter.KIND_PROPERTY))
        assertTrue(OrPointcut(methodOnly, propertyOnly).matches(field))
        verify(methodOnly, never()).matches(any())
        verify(propertyOnly).matches(field)
    }

    @Test
    fun `getKind returns union of both pointcut kinds`() {
        val first = mockPointcutWithKind(setOf(KindFilter.KIND_METHOD))
        val second = mockPointcutWithKind(setOf(KindFilter.KIND_CLASS))
        val pointcut = OrPointcut(first, second)
        assertEquals(setOf(KindFilter.KIND_METHOD, KindFilter.KIND_CLASS), pointcut.getKind())
    }

    @Test
    fun `getClassFilter returns OrPointFilter combining both class filters`() {
        val first = mockTruePointcut()
        val second = mockTruePointcut()
        val classFilter = OrPointcut(first, second).getClassFilter()
        assertNotNull(classFilter)
        assertTrue(classFilter is OrPointFilter)
    }

    @Test
    fun `two OrPointcuts with same delegates are equal`() {
        val first = mockTruePointcut()
        val second = mockTruePointcut()
        val a = OrPointcut(first, second)
        val b = OrPointcut(first, second)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    // ---- Helpers ----

    private fun mockMemberInClass(cls: PhpClass): PhpClassMember {
        val member = mock<Method>()
        whenever(member.containingClass).thenReturn(cls)
        return member
    }

    private fun mockPointcutMatching(
        member: PhpClassMember,
        cls: PhpClass,
        matches: Boolean,
        kinds: Set<KindFilter> = KindFilter.entries.toSet()
    ): Pointcut {
        val classFilter = mock<PointFilter>()
        whenever(classFilter.matches(cls)).thenReturn(matches)
        whenever(classFilter.getKind()).thenReturn(kinds)
        val pointcut = mock<Pointcut>()
        whenever(pointcut.matches(member)).thenReturn(matches)
        whenever(pointcut.getClassFilter()).thenReturn(classFilter)
        whenever(pointcut.getKind()).thenReturn(kinds)
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
