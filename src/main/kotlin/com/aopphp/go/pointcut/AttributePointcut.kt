package com.aopphp.go.pointcut

import com.aopphp.go.index.AttributePhpNamedElementIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpAttributesOwner
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Matches PHP named elements that have a specific PHP 8 Attribute applied.
 */
class AttributePointcut(
    private val filterKind: Set<KindFilter>,
    private val expectedClass: String
) : Pointcut {

    private val _classFilter: PointFilter = AttributeClassFilter(expectedClass)

    override fun getClassFilter() = _classFilter

    override fun matches(element: PhpNamedElement): Boolean {
        if (!canMatchElement(element)) return false
        if (element !is PhpAttributesOwner) return false
        return element.getAttributes(expectedClass).isNotEmpty()
    }

    private fun canMatchElement(element: PhpNamedElement) = when (element) {
        is Method    -> filterKind.contains(KindFilter.KIND_METHOD)
        is Field     -> filterKind.contains(KindFilter.KIND_PROPERTY)
        is PhpClass  -> filterKind.contains(KindFilter.KIND_CLASS)
        else         -> false
    }

    override fun getKind() = filterKind

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttributePointcut) return false
        return _classFilter == other._classFilter && filterKind == other.filterKind
            && expectedClass == other.expectedClass
    }

    override fun hashCode() = 31 * (31 * _classFilter.hashCode() + filterKind.hashCode()) + expectedClass.hashCode()

    private class AttributeClassFilter(private val annotationName: String) : PointFilter {
        private val _kind = setOf(KindFilter.KIND_CLASS)

        override fun getKind() = _kind

        override fun matches(element: PhpNamedElement): Boolean {
            if (element !is PhpClass) return false
            val elementFQN = element.fqn
            val scope = PhpIndex.getInstance(element.project).searchScope
            val values = FileBasedIndex.getInstance().getValues(AttributePhpNamedElementIndex.KEY, annotationName, scope)
            return values.any { set -> set.any { it.startsWith(elementFQN) } }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AttributeClassFilter) return false
            return annotationName == other.annotationName
        }

        override fun hashCode() = annotationName.hashCode()
    }
}
