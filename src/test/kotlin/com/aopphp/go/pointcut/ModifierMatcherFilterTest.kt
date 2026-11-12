package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ModifierMatcherFilterTest {

    private fun method(modifier: PhpModifier): Method = mock<Method>().also {
        whenever(it.modifier).thenReturn(modifier)
    }

    private fun field(access: PhpModifier.Access, setAccess: PhpModifier.Access? = null, readonly: Boolean = false): Field =
        mock<Field>().also {
            whenever(it.modifier).thenReturn(
                PhpModifier.instance(access, setAccess, PhpModifier.Abstractness.IMPLEMENTED, PhpModifier.State.DYNAMIC)
            )
            whenever(it.isReadonly).thenReturn(readonly)
        }

    /** `final public|protected` */
    private val finalPublicOrProtected = ModifierMatcherFilter()
        .andMatch(ModifierMatcherFilter.FINAL)
        .orMatch(ModifierMatcherFilter.PUBLIC or ModifierMatcherFilter.PROTECTED)

    @Test
    fun `space separated modifiers must all be present`() {
        assertTrue(finalPublicOrProtected.matches(method(PhpModifier.PUBLIC_FINAL_DYNAMIC)))
        assertTrue(finalPublicOrProtected.matches(method(PhpModifier.PROTECTED_FINAL_DYNAMIC)))
        // Go! AOP 3.x also matched every non-final public method here
        assertFalse(finalPublicOrProtected.matches(method(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)))
        assertFalse(finalPublicOrProtected.matches(method(PhpModifier.PRIVATE_FINAL_DYNAMIC)))
    }

    @Test
    fun `alternatives accept any of their modifiers`() {
        val filter = ModifierMatcherFilter(orMask = ModifierMatcherFilter.PUBLIC or ModifierMatcherFilter.PROTECTED)
        assertTrue(filter.matches(method(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)))
        assertTrue(filter.matches(method(PhpModifier.PROTECTED_IMPLEMENTED_STATIC)))
        assertFalse(filter.matches(method(PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC)))
    }

    @Test
    fun `asymmetric visibility modifiers match the set visibility of a property`() {
        val privateSet = ModifierMatcherFilter(andMask = ModifierMatcherFilter.PRIVATE_SET)
        val protectedSet = ModifierMatcherFilter(andMask = ModifierMatcherFilter.PROTECTED_SET)
        assertTrue(privateSet.matches(field(PhpModifier.Access.PUBLIC, PhpModifier.Access.PRIVATE)))
        assertFalse(privateSet.matches(field(PhpModifier.Access.PUBLIC)))
        assertTrue(protectedSet.matches(field(PhpModifier.Access.PUBLIC, PhpModifier.Access.PROTECTED)))
        assertFalse(protectedSet.matches(field(PhpModifier.Access.PUBLIC, PhpModifier.Access.PRIVATE)))
    }

    @Test
    fun `readonly modifier matches readonly properties`() {
        val readonly = ModifierMatcherFilter(andMask = ModifierMatcherFilter.READONLY)
        assertTrue(readonly.matches(field(PhpModifier.Access.PUBLIC, readonly = true)))
        assertFalse(readonly.matches(field(PhpModifier.Access.PUBLIC)))
    }

    @Test
    fun `empty filter matches any member and nothing else`() {
        assertTrue(ModifierMatcherFilter().matches(method(PhpModifier.PRIVATE_IMPLEMENTED_STATIC)))
        assertFalse(ModifierMatcherFilter().matches(mock<PhpNamedElement>()))
    }

    @Test
    fun `bitOf maps every pointcut modifier`() {
        listOf("public", "protected", "private", "final", "readonly", "private(set)", "protected(set)").forEach {
            assertNotEquals(0, ModifierMatcherFilter.bitOf(it), it)
        }
        assertEquals(0, ModifierMatcherFilter.bitOf("static"))
    }

    @Test
    fun `filters with same masks are equal`() {
        assertEquals(ModifierMatcherFilter(1, 2), ModifierMatcherFilter(1, 2))
        assertEquals(ModifierMatcherFilter(1, 2).hashCode(), ModifierMatcherFilter(1, 2).hashCode())
        assertNotEquals(ModifierMatcherFilter(1, 2), ModifierMatcherFilter(2, 1))
    }
}
