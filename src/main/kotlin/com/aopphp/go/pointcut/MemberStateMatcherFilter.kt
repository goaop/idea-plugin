package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpElementWithModifier
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class MemberStateMatcherFilter(private val allowedState: PhpModifier.State) : PointFilter {
    private val _kind = KindFilter.entries.toSet()

    override fun getKind() = _kind

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is PhpElementWithModifier) return false
        return element.modifier.state == allowedState
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberStateMatcherFilter) return false
        return allowedState == other.allowedState
    }

    override fun hashCode() = allowedState.hashCode()
}
