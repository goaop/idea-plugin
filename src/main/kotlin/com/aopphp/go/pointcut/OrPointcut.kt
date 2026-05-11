package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class OrPointcut(first: Pointcut, second: Pointcut) : AndPointcut(first, second) {
    init {
        _classFilter = OrPointFilter(first.getClassFilter(), second.getClassFilter())
    }

    // Override kind to be the union rather than intersection
    private val _orKind: Set<KindFilter> = first.getKind() + second.getKind()
    override fun getKind() = _orKind

    override fun matches(element: PhpNamedElement) =
        isMatchesPointcut(element, first) || isMatchesPointcut(element, second)
}
