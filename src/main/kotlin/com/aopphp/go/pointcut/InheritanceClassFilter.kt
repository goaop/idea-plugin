package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class InheritanceClassFilter(private val parentClassName: String) : PointFilter {
    private val _kind = setOf(KindFilter.KIND_CLASS)
    private val normalizedFqn = if (parentClassName.startsWith("\\")) parentClassName else "\\$parentClassName"

    override fun getKind() = _kind

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is PhpClass) return false
        val elementFqn = element.fqn ?: return false
        if (elementFqn == normalizedFqn) return true
        return element.isSubclassOf(normalizedFqn)
    }

    private fun PhpClass.isSubclassOf(fqn: String): Boolean {
        var current: PhpClass? = superClass
        while (current != null) {
            if (current.fqn == fqn) return true
            current = current.superClass
        }
        return implementedInterfaces.any { it.fqn == fqn }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InheritanceClassFilter) return false
        return parentClassName == other.parentClassName
    }

    override fun hashCode() = parentClassName.hashCode()
}
