package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class NotPointcut(private val pointcut: Pointcut) : Pointcut {
    private val kind = pointcut.getKind()
    private val classFilter: PointFilter = TruePointFilter

    override fun getKind() = kind
    override fun getClassFilter() = classFilter

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is PhpClassMember) return false
        val containing = element.containingClass ?: return false
        if (!pointcut.getClassFilter().matches(containing)) return true
        return !pointcut.matches(element)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NotPointcut) return false
        return kind == other.kind && classFilter == other.classFilter && pointcut == other.pointcut
    }

    override fun hashCode() = 31 * (31 * kind.hashCode() + classFilter.hashCode()) + pointcut.hashCode()
}
