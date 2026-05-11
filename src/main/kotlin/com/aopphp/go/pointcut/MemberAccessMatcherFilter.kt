package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpElementWithModifier
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class MemberAccessMatcherFilter(private val allowedAccess: Set<PhpModifier.Access>) : PointFilter {
    private val _kind = KindFilter.entries.toSet()

    override fun getKind() = _kind

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is PhpElementWithModifier) return false
        return element.modifier.access in allowedAccess
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberAccessMatcherFilter) return false
        return allowedAccess == other.allowedAccess
    }

    override fun hashCode() = allowedAccess.hashCode()
}
