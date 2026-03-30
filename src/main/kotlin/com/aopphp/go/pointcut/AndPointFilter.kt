package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class AndPointFilter(private val first: PointFilter, private val second: PointFilter) : PointFilter {
    private val _kind = first.getKind().intersect(second.getKind())

    override fun getKind() = _kind
    override fun matches(element: PhpNamedElement) = first.matches(element) && second.matches(element)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AndPointFilter) return false
        return _kind == other._kind && first == other.first && second == other.second
    }

    override fun hashCode() = 31 * (31 * _kind.hashCode() + first.hashCode()) + second.hashCode()
}
