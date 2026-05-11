package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MemberStateMatcherFilterTest {

    @Test
    fun `returns false for non-PhpElementWithModifier element`() {
        val element = mock<PhpNamedElement>()
        val filter = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)
        assertFalse(filter.matches(element))
    }

    @Test
    fun `returns true when element state matches allowed state (DYNAMIC)`() {
        val element = mockMemberWithModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
        val filter = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)
        assertTrue(filter.matches(element))
    }

    @Test
    fun `returns false when element state does not match allowed state`() {
        val element = mockMemberWithModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC)
        val filter = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)
        assertFalse(filter.matches(element))
    }

    @Test
    fun `returns true when element state is STATIC and filter allows STATIC`() {
        val element = mockMemberWithModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC)
        val filter = MemberStateMatcherFilter(PhpModifier.State.STATIC)
        assertTrue(filter.matches(element))
    }

    @Test
    fun `returns false when element is DYNAMIC but filter requires STATIC`() {
        val element = mockMemberWithModifier(PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC)
        val filter = MemberStateMatcherFilter(PhpModifier.State.STATIC)
        assertFalse(filter.matches(element))
    }

    @Test
    fun `getKind returns all kind filters`() {
        val filter = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)
        assertEquals(KindFilter.entries.toSet(), filter.getKind())
    }

    @Test
    fun `two filters with same state are equal`() {
        val a = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)
        val b = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two filters with different states are not equal`() {
        val a = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)
        val b = MemberStateMatcherFilter(PhpModifier.State.STATIC)
        assertNotEquals(a, b)
    }

    @Test
    fun `not equal to non-MemberStateMatcherFilter`() {
        val filter = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)
        assertNotEquals(filter, null)
        assertNotEquals(filter, PhpModifier.State.DYNAMIC)
    }

    private fun mockMemberWithModifier(modifier: PhpModifier): PhpClassMember {
        val element = mock<PhpClassMember>()
        whenever(element.modifier).thenReturn(modifier)
        return element
    }
}
