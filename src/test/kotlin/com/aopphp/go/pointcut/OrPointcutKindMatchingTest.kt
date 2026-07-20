package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpModifier
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Regression tests for issue #9: a combination with `||` must not let a sub-pointcut
 * of one join point kind match elements of another kind.
 *
 * Models the pointcut `execution(public **->data*(*)) || access(public **->$id*)`
 * with real [SignaturePointcut]s: before the fix, a public field named `dataMap`
 * was matched by the execution clause purely by name pattern (and a method named
 * `idGenerator` by the access clause).
 */
class OrPointcutKindMatchingTest {

    private val executionClause = SignaturePointcut(
        setOf(KindFilter.KIND_METHOD),
        "data*",
        MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
    )

    private val accessClause = SignaturePointcut(
        setOf(KindFilter.KIND_PROPERTY),
        "id*",
        MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
    )

    private val orPointcut = OrPointcut(executionClause, accessClause)

    @Test
    fun `field matching execution name pattern is not matched`() {
        // The issue #9 false positive: field "dataMap" matches "data*" by name,
        // but the execution clause is KIND_METHOD and must not apply to fields
        val field = mockField("dataMap")
        assertFalse(orPointcut.matches(field))
    }

    @Test
    fun `method matching access name pattern is not matched`() {
        // Symmetric case: method "idGenerator" matches "id*" of the access clause
        val method = mockMethod("idGenerator")
        assertFalse(orPointcut.matches(method))
    }

    @Test
    fun `method matching execution clause still matches`() {
        val method = mockMethod("dataLoader")
        assertTrue(orPointcut.matches(method))
    }

    @Test
    fun `field matching access clause still matches`() {
        val field = mockField("idNumber")
        assertTrue(orPointcut.matches(field))
    }

    @Test
    fun `class-kind true pointcut in or does not leak members`() {
        // Models `execution(public **->data*(*)) || initialization(**)`: the
        // initialization clause compiles to TruePointcut(KIND_CLASS) whose matches()
        // is always true — it must not make every method match through the OR branch
        val orWithInit = OrPointcut(executionClause, TruePointcut(setOf(KindFilter.KIND_CLASS)))
        assertFalse(orWithInit.matches(mockMethod("unrelated")))
        assertFalse(orWithInit.matches(mockField("unrelated")))
        assertTrue(orWithInit.matches(mockMethod("dataLoader")))
    }

    @Test
    fun `and of cross-kind pointcuts matches nothing`() {
        // No single element can be both a method and a property
        val andPointcut = AndPointcut(executionClause, accessClause)
        assertFalse(andPointcut.matches(mockMethod("dataLoader")))
        assertFalse(andPointcut.matches(mockField("idNumber")))
    }

    @Test
    fun `or kind is still the union of both clause kinds`() {
        // PointcutAdvisor.getMatchedElements relies on the union to collect members
        assertEquals(setOf(KindFilter.KIND_METHOD, KindFilter.KIND_PROPERTY), orPointcut.getKind())
    }

    // ---- Helpers ----

    private fun mockMethod(name: String): Method {
        val method = mock<Method>()
        whenever(method.name).thenReturn(name)
        whenever(method.containingClass).thenReturn(mock<PhpClass>())
        whenever(method.modifier).thenReturn(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
        return method
    }

    private fun mockField(name: String): Field {
        val field = mock<Field>()
        whenever(field.name).thenReturn(name)
        whenever(field.containingClass).thenReturn(mock<PhpClass>())
        whenever(field.modifier).thenReturn(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
        return field
    }
}
