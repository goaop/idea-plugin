package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class NotPointFilter(private val first: PointFilter) : PointFilter {
    private val _kind = first.getKind()

    override fun getKind() = _kind
    override fun matches(element: PhpNamedElement) = !first.matches(element)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NotPointFilter) return false
        return _kind == other._kind && first == other.first
    }

    override fun hashCode() = 31 * _kind.hashCode() + first.hashCode()
}
