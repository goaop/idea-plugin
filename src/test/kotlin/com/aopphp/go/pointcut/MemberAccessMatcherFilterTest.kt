package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MemberAccessMatcherFilterTest {

    @Test
    fun `returns false for non-PhpElementWithModifier element`() {
        val element = mock<PhpNamedElement>()
        val filter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        assertFalse(filter.matches(element))
    }

    @Test
    fun `returns true when element access is in allowed set`() {
        val element = mockMemberWithModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
        val filter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        assertTrue(filter.matches(element))
    }

    @Test
    fun `returns false when element access is not in allowed set`() {
        val element = mockMemberWithModifier(PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC)
        val filter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        assertFalse(filter.matches(element))
    }

    @Test
    fun `returns true when protected access matches allowed set`() {
        val element = mockMemberWithModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC)
        val filter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PROTECTED))
        assertTrue(filter.matches(element))
    }

    @Test
    fun `returns true when access is one of multiple allowed`() {
        val element = mockMemberWithModifier(PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC)
        val filter = MemberAccessMatcherFilter(
            setOf(PhpModifier.Access.PUBLIC, PhpModifier.Access.PRIVATE)
        )
        assertTrue(filter.matches(element))
    }

    @Test
    fun `getKind returns all kind filters`() {
        val filter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        assertEquals(KindFilter.entries.toSet(), filter.getKind())
    }

    @Test
    fun `two filters with same allowed access are equal`() {
        val a = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        val b = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two filters with different allowed access are not equal`() {
        val a = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        val b = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PRIVATE))
        assertNotEquals(a, b)
    }

    @Test
    fun `not equal to null or different type`() {
        val filter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        assertNotEquals(filter, null)
        assertNotEquals(filter, "string")
    }

    private fun mockMemberWithModifier(modifier: PhpModifier): PhpClassMember {
        val element = mock<PhpClassMember>()
        whenever(element.modifier).thenReturn(modifier)
        return element
    }
}
