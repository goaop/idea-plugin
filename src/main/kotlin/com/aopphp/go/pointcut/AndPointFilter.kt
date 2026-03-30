package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class AndPointFilter(private val first: PointFilter, private val second: PointFilter) : PointFilter {
    private val kind = first.getKind().intersect(second.getKind())

    override fun getKind() = kind
    override fun matches(element: PhpNamedElement) = first.matches(element) && second.matches(element)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AndPointFilter) return false
        return kind == other.kind && first == other.first && second == other.second
    }

    override fun hashCode() = 31 * (31 * kind.hashCode() + first.hashCode()) + second.hashCode()
}
