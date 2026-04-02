package com.aopphp.go.pointcut

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class InheritanceClassFilter(private val parentClassName: String) : PointFilter {
    private val _kind = setOf(KindFilter.KIND_CLASS)

    override fun getKind() = _kind

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is PhpClass) return false
        val normalizedFqn = if (parentClassName.startsWith("\\")) parentClassName else "\\$parentClassName"
        val elementFqn = element.fqn ?: return false
        // X+ matches X itself
        if (elementFqn == normalizedFqn) return true
        // getAllSubclasses(String) is a fast index lookup that covers classes, interfaces, and traits
        return PhpIndex.getInstance(element.project).getAllSubclasses(normalizedFqn).any { it.fqn == elementFqn }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InheritanceClassFilter) return false
        return parentClassName == other.parentClassName
    }

    override fun hashCode() = parentClassName.hashCode()
}
