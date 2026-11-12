package com.aopphp.go.pointcut

import com.aopphp.go.index.AttributePointcutExpressionIndex
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * A reference to a named pointcut of an aspect: `Aspect\Class->pointcutName`, or `$this->pointcutName`
 * that is not declared in the aspect itself (e.g. inherited from an abstract parent aspect).
 *
 * The referenced pointcut lives in another index entry, which can not be read while indexing,
 * so it is resolved lazily when an element is matched. The kind is unknown until then, so it is
 * reported as every kind and checked against the resolved pointcut while matching.
 */
class PointcutReferencePointcut(
    private val aspectClassFqn: String,
    private val pointcutName: String,
) : Pointcut {

    private val _classFilter: PointFilter = ReferencedClassFilter(this)

    @Transient
    private var cachedPointcut: Pointcut? = null

    @Transient
    private var cachedStamp: Long? = null

    override fun getKind() = KindFilter.entries.toSet()

    override fun getClassFilter() = _classFilter

    override fun matches(element: PhpNamedElement): Boolean {
        val pointcut = resolve(element.project) ?: return false
        return pointcut.getKind().supports(element) && pointcut.matches(element)
    }

    /** Returns the referenced pointcut, or null when there is no such named pointcut */
    fun resolve(project: Project?): Pointcut? {
        project ?: return null
        val stamp = PsiModificationTracker.getInstance(project).modificationCount
        if (cachedStamp == stamp) return cachedPointcut

        val visiting = RESOLVING.get()
        val key = "$aspectClassFqn.$pointcutName"
        if (!visiting.add(key)) return null
        try {
            cachedPointcut = lookup(project, key)
            cachedStamp = stamp
            return cachedPointcut
        } finally {
            visiting.remove(key)
        }
    }

    private fun lookup(project: Project, key: String): Pointcut? {
        val index = FileBasedIndex.getInstance()
        val scope = GlobalSearchScope.allScope(project)
        index.getValues(AttributePointcutExpressionIndex.KEY, key, scope).firstOrNull()?.let { return it }

        // The named pointcut can be declared in a parent aspect
        val aspectClass = PhpIndex.getInstance(project).getClassesByFQN(aspectClassFqn).firstOrNull() ?: return null
        val method = aspectClass.findMethodByName(pointcutName) ?: return null
        val declaringKey = method.fqn
        if (declaringKey == key) return null
        return index.getValues(AttributePointcutExpressionIndex.KEY, declaringKey, scope).firstOrNull()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PointcutReferencePointcut) return false
        return aspectClassFqn == other.aspectClassFqn && pointcutName == other.pointcutName
    }

    override fun hashCode() = 31 * aspectClassFqn.hashCode() + pointcutName.hashCode()

    override fun toString() = "PointcutReferencePointcut($aspectClassFqn->$pointcutName)"

    /** Class filter of the referenced pointcut, resolved with the project of the matched class */
    private class ReferencedClassFilter(private val reference: PointcutReferencePointcut) : PointFilter {
        override fun getKind() = KindFilter.entries.toSet()

        override fun matches(element: PhpNamedElement): Boolean {
            val pointcut = reference.resolve(element.project) ?: return false
            return pointcut.getClassFilter().matches(element)
        }

        override fun equals(other: Any?) =
            other is ReferencedClassFilter && reference.aspectClassFqn == other.reference.aspectClassFqn &&
                reference.pointcutName == other.reference.pointcutName

        override fun hashCode() = reference.hashCode()
    }

    companion object {
        private val RESOLVING: ThreadLocal<MutableSet<String>> = ThreadLocal.withInitial { mutableSetOf() }
    }
}
