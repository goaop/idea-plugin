package com.aopphp.go.pointcut

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpAttribute
import com.jetbrains.php.lang.psi.elements.PhpAttributesOwner
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Matches PHP named elements that carry a PHP 8 attribute, mirroring the framework's `AttributePointcut`:
 * attributes that extend [expectedClass] match as well.
 */
class AttributePointcut(
    private val filterKind: Set<KindFilter>,
    private val expectedClass: String
) : Pointcut {

    private val _classFilter: PointFilter = AttributeClassFilter(expectedClass)

    override fun getClassFilter() = _classFilter

    override fun matches(element: PhpNamedElement): Boolean {
        if (!filterKind.supports(element)) return false
        return hasAttribute(element, expectedClass)
    }

    override fun getKind() = filterKind

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AttributePointcut) return false
        return _classFilter == other._classFilter && filterKind == other.filterKind
            && expectedClass == other.expectedClass
    }

    override fun hashCode() = 31 * (31 * _classFilter.hashCode() + filterKind.hashCode()) + expectedClass.hashCode()

    /**
     * Pre-filter for classes: only a class with a member (or the class itself) carrying the attribute
     * can contain a matching joinpoint.
     */
    private class AttributeClassFilter(private val annotationName: String) : PointFilter {
        private val _kind = setOf(KindFilter.KIND_CLASS)

        override fun getKind() = _kind

        override fun matches(element: PhpNamedElement): Boolean {
            if (element !is PhpClass) return false
            if (hasAttribute(element, annotationName)) return true
            val members: List<PhpNamedElement> = (element.ownMethods?.toList() ?: emptyList()) +
                (element.ownFields?.toList() ?: emptyList())
            return members.any { hasAttribute(it, annotationName) }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AttributeClassFilter) return false
            return annotationName == other.annotationName
        }

        override fun hashCode() = annotationName.hashCode()
    }

    companion object {

        /** True when [element] carries the attribute [attributeFqn] or an attribute extending it */
        @JvmStatic
        fun hasAttribute(element: PhpNamedElement, attributeFqn: String): Boolean {
            if (element !is PhpAttributesOwner) return false
            if (element.getAttributes(attributeFqn).isNotEmpty()) return true
            return element.attributes.any { isAttributeSubclass(it, attributeFqn) }
        }

        private fun isAttributeSubclass(attribute: PhpAttribute, expectedFqn: String): Boolean {
            val fqn: String = attribute.fqn ?: return false
            if (fqn == expectedFqn) return true
            val classes = PhpIndex.getInstance(attribute.project).getClassesByFQN(fqn)
            return classes.any { phpClass ->
                var parent: PhpClass? = phpClass.superClass
                val visited = mutableSetOf<String>()
                while (parent != null && visited.add(parent.fqn)) {
                    if (parent.fqn == expectedFqn) return@any true
                    parent = parent.superClass
                }
                false
            }
        }
    }
}
