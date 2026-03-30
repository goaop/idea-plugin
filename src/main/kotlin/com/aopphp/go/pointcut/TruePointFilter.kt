package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

object TruePointFilter : PointFilter {
    private val KIND_ALL = KindFilter.entries.toSet()

    @JvmStatic fun getInstance(): TruePointFilter = this

    override fun matches(element: PhpNamedElement) = true
    override fun getKind() = KIND_ALL
    override fun hashCode() = 0
    override fun equals(other: Any?) = other is TruePointFilter
}
