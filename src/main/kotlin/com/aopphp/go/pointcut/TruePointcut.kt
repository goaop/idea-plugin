package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

open class TruePointcut @JvmOverloads constructor(
    private val _kind: Set<KindFilter> = KindFilter.entries.toSet(),
    @set:JvmName("setClassFilter")
    var _classFilter: PointFilter = TruePointFilter
) : Pointcut {
    override fun getClassFilter() = _classFilter
    override fun matches(element: PhpNamedElement) = true
    override fun getKind() = _kind

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TruePointcut) return false
        return _classFilter == other._classFilter && _kind == other._kind
    }

    override fun hashCode() = 31 * _classFilter.hashCode() + _kind.hashCode()
}
