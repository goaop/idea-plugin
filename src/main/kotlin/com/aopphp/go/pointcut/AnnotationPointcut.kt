package com.aopphp.go.pointcut

import com.aopphp.go.index.AnnotatedPhpNamedElementIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.ID
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpAttributesOwner
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Matches PHP named elements that have a specific PHP 8 Attribute applied.
 */
class AnnotationPointcut(
    private val filterKind: Set<KindFilter>,
    private val expectedClass: String
) : Pointcut {

    private val _classFilter: PointFilter = AnnotatedClassFilter(expectedClass)

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
        if (other !is AnnotationPointcut) return false
        return _classFilter == other._classFilter && filterKind == other.filterKind
            && expectedClass == other.expectedClass
    }

    override fun hashCode() = 31 * (31 * _classFilter.hashCode() + filterKind.hashCode()) + expectedClass.hashCode()

    private class AnnotatedClassFilter(private val annotationName: String) : PointFilter {
        private val _kind = setOf(KindFilter.KIND_CLASS)
        private val index: FileBasedIndex = FileBasedIndex.getInstance()
        private val key: ID<String, Set<String>> = AnnotatedPhpNamedElementIndex.KEY

        override fun getKind() = _kind

        override fun matches(element: PhpNamedElement): Boolean {
            if (element !is PhpClass) return false
            val elementFQN = element.fqn ?: return false
            val scope = PhpIndex.getInstance(element.project).searchScope
            val values = index.getValues(key, annotationName, scope)
            return values.firstOrNull()?.any { it.startsWith(elementFQN) } == true
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AnnotatedClassFilter) return false
            return annotationName == other.annotationName
        }

        override fun hashCode() = annotationName.hashCode()
    }
}
