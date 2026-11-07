package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * `matchInherited()`: members inherited from a parent class or imported from a trait.
 *
 * The plugin evaluates every member in the context of the class (or trait) that declares it, where a member
 * is never inherited: `matchInherited()` alone selects nothing, and `!matchInherited()` keeps every member.
 * This keeps `execution(public **->*(*)) && !matchInherited()` working instead of matching nothing.
 */
class MatchInheritedPointcut : Pointcut {
    private val _kind = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_PROPERTY)

    override fun getKind() = _kind
    override fun getClassFilter(): PointFilter = TruePointFilter
    override fun matches(element: PhpNamedElement) = false

    override fun equals(other: Any?) = other is MatchInheritedPointcut
    override fun hashCode() = 19
}
