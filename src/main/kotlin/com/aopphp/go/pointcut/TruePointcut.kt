package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

open class TruePointcut(
    private val kind: Set<KindFilter> = KindFilter.entries.toSet(),
    protected var classFilter: PointFilter = TruePointFilter
) : Pointcut {
    override fun getClassFilter() = classFilter
    override fun matches(element: PhpNamedElement) = true
    override fun getKind() = kind

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TruePointcut) return false
        return classFilter == other.classFilter && kind == other.kind
    }

    override fun hashCode() = 31 * classFilter.hashCode() + kind.hashCode()
}
