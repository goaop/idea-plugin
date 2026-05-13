package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Verifies that the pointcut matching layer correctly handles trait elements.
 * Traits are represented as [PhpClass] instances with [PhpClass.isTrait] returning true.
 */
class PointcutTraitMatchingTest {

    // ---- SignaturePointcut (class filter) matches trait by FQN ----

    @Test
    fun `class filter SignaturePointcut matches trait PhpClass by FQN`() {
        val classFilter = SignaturePointcut(setOf(KindFilter.KIND_CLASS), "App\\MyTrait", TruePointFilter)
        val trait = mockTrait("\\App\\MyTrait")
        assertTrue(classFilter.matches(trait))
    }

    @Test
    fun `class filter SignaturePointcut does not match trait with different FQN`() {
        val classFilter = SignaturePointcut(setOf(KindFilter.KIND_CLASS), "App\\MyTrait", TruePointFilter)
        val trait = mockTrait("\\App\\OtherTrait")
        assertFalse(classFilter.matches(trait))
    }

    // ---- SignaturePointcut (name filter) matches trait method ----

    @Test
    fun `method pointcut matches trait method`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "doSomething", TruePointFilter)
        val method = mockTraitMethod("doSomething", "\\App\\MyTrait")
        assertTrue(pointcut.matches(method))
    }

    // ---- SignaturePointcut (name filter) matches trait field ----

    @Test
    fun `property pointcut matches trait field`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_PROPERTY), "myProperty", TruePointFilter)
        val field = mockTraitField("myProperty", "\\App\\MyTrait")
        assertTrue(pointcut.matches(field))
    }

    // ---- InheritanceClassFilter matches trait ----

    @Test
    fun `InheritanceClassFilter matches trait by exact FQN`() {
        val filter = InheritanceClassFilter("App\\MyTrait")
        val trait = mockTrait("\\App\\MyTrait")
        assertTrue(filter.matches(trait))
    }

    // ---- Kind filter classes cover trait via KIND_CLASS (traits are PhpClass instances) ----

    @Test
    fun `getKind on class filter returns KIND_CLASS which covers traits`() {
        val classFilter = SignaturePointcut(setOf(KindFilter.KIND_CLASS), "App\\MyTrait", TruePointFilter)
        assertTrue(KindFilter.KIND_CLASS in classFilter.getKind())
    }

    // ---- Helpers ----

    /** Creates a mock PhpClass that behaves like a trait. */
    private fun mockTrait(fqn: String): PhpClass {
        val trait = mock<PhpClass>()
        whenever(trait.fqn).thenReturn(fqn)
        whenever(trait.isTrait).thenReturn(true)
        whenever(trait.isInterface).thenReturn(false)
        whenever(trait.isEnum).thenReturn(false)
        return trait
    }

    /** Creates a mock Method that belongs to a trait. */
    private fun mockTraitMethod(name: String, traitFqn: String): Method {
        val trait = mockTrait(traitFqn)
        val method = mock<Method>()
        whenever(method.name).thenReturn(name)
        whenever(method.containingClass).thenReturn(trait)
        return method
    }

    /** Creates a mock Field that belongs to a trait. */
    private fun mockTraitField(name: String, traitFqn: String): Field {
        val trait = mockTrait(traitFqn)
        val field = mock<Field>()
        whenever(field.name).thenReturn(name)
        whenever(field.containingClass).thenReturn(trait)
        return field
    }
}
