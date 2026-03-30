package com.aopphp.go.pointcut

import com.jetbrains.php.PhpClassHierarchyUtils
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

class InheritanceClassFilter(private val parentClassName: String) : PointFilter {
    private val _kind = setOf(KindFilter.KIND_CLASS)

    override fun getKind() = _kind

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is PhpClass) return false
        val parents = PhpIndex.getInstance(element.project).getAnyByFQN(parentClassName)
        val parent = parents.firstOrNull() ?: return false
        return PhpClassHierarchyUtils.getAllSubclasses(parent).contains(element)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InheritanceClassFilter) return false
        return parentClassName == other.parentClassName
    }

    override fun hashCode() = parentClassName.hashCode()
}
