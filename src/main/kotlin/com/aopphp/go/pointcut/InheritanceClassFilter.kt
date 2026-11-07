package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Class filter of the `Foo+` pattern, mirroring the framework's `ClassInheritancePointcut`: matches the type
 * `Foo` itself, its subclasses and implementers, and classes that use the trait `Foo` directly or through
 * a parent class.
 */
class InheritanceClassFilter(private val parentClassName: String) : PointFilter {
    private val _kind = setOf(KindFilter.KIND_CLASS)
    private val normalizedFqn = if (parentClassName.startsWith("\\")) parentClassName else "\\$parentClassName"

    override fun getKind() = _kind

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is PhpClass) return false
        val elementFqn: String = element.fqn ?: return false
        if (elementFqn == normalizedFqn) return true

        val visited = mutableSetOf<String>()
        var current: PhpClass? = element
        while (current != null) {
            val currentFqn: String = current.fqn ?: break
            if (!visited.add(currentFqn)) break
            if (current !== element && currentFqn == normalizedFqn) return true
            if (usesTrait(current) || implementsInterface(current, mutableSetOf())) return true
            current = current.superClass
        }
        return false
    }

    private fun usesTrait(phpClass: PhpClass): Boolean {
        val traitNames: Array<String>? = phpClass.traitNames
        return traitNames?.any { it == normalizedFqn } == true
    }

    private fun implementsInterface(phpClass: PhpClass, visited: MutableSet<String>): Boolean {
        val interfaces: Array<PhpClass>? = phpClass.implementedInterfaces
        for (iface in interfaces ?: return false) {
            val ifaceFqn: String = iface.fqn ?: continue
            if (ifaceFqn == normalizedFqn) return true
            if (visited.add(ifaceFqn) && implementsInterface(iface, visited)) return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InheritanceClassFilter) return false
        return parentClassName == other.parentClassName
    }

    override fun hashCode() = parentClassName.hashCode()
}
