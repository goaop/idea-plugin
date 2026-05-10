package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

open class AndPointcut(
    protected val first: Pointcut,
    protected val second: Pointcut
) : Pointcut {

    protected val _kind: Set<KindFilter> = first.getKind().intersect(second.getKind())
    protected var _classFilter: PointFilter = AndPointFilter(first.getClassFilter(), second.getClassFilter())

    override fun getKind() = _kind
    override fun getClassFilter() = _classFilter

    override fun matches(element: PhpNamedElement) =
        isMatchesPointcut(element, first) && isMatchesPointcut(element, second)

    protected fun isMatchesPointcut(point: PhpNamedElement, pointcut: Pointcut): Boolean {
        if (point !is PhpClassMember) return false
        val containingClass = point.containingClass ?: return false
        return pointcut.matches(point) && pointcut.getClassFilter().matches(containingClass)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AndPointcut) return false
        return getKind() == other.getKind() && _classFilter == other._classFilter
            && first == other.first && second == other.second
    }

    override fun hashCode() = 31 * (31 * (31 * getKind().hashCode() + _classFilter.hashCode()) + first.hashCode()) + second.hashCode()
}
