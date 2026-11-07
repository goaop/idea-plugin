package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * `matchReturningByReference()`: methods and functions declared `function &name()`.
 *
 * Go! AOP 4.0 can not weave them, so pointcuts that select such methods add `&& !matchReturningByReference()`.
 */
class MatchReturningByReferencePointcut : Pointcut {
    private val _kind = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_FUNCTION)

    override fun getKind() = _kind
    override fun getClassFilter(): PointFilter = TruePointFilter
    override fun matches(element: PhpNamedElement) = element is Function && element.isReturningByReference

    override fun equals(other: Any?) = other is MatchReturningByReferencePointcut
    override fun hashCode() = 17
}
