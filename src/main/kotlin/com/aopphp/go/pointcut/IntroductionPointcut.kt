package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Pointcut of a `#[DeclareParents]` introduction: it selects whole classes, which receive the declared
 * interface and trait. The expression is any class filter, e.g. `within(App\Service\**)`.
 */
class IntroductionPointcut(private val pointcut: Pointcut) : Pointcut {
    private val _kind = setOf(KindFilter.KIND_CLASS, KindFilter.KIND_INTRODUCTION)

    override fun getKind() = _kind
    override fun getClassFilter() = pointcut.getClassFilter()

    override fun matches(element: PhpNamedElement): Boolean =
        element is PhpClass && pointcut.getKind().supports(element) && pointcut.matches(element)

    override fun equals(other: Any?) = other is IntroductionPointcut && pointcut == other.pointcut
    override fun hashCode() = 31 * pointcut.hashCode() + 7
}
