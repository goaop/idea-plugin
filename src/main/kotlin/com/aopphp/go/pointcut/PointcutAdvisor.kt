package com.aopphp.go.pointcut

import com.aopphp.go.index.AnnotationPointcutExpressionIndex
import com.aopphp.go.util.PluginUtil
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.stubs.indexes.PhpClassIndex

object PointcutAdvisor {

    private val fileBasedIndex = FileBasedIndex.getInstance()
    private val index = AnnotationPointcutExpressionIndex.KEY

    @JvmStatic
    fun getMatchedAdvices(element: PhpNamedElement): List<PhpNamedElement> {
        val result = mutableListOf<PhpNamedElement>()
        val project = element.project
        val phpIndex = PhpIndex.getInstance(project)
        val scope = GlobalSearchScope.allScope(project)

        for (signature in fileBasedIndex.getAllKeys(index, project)) {
            val pointcut = fileBasedIndex.getValues(index, signature, scope).firstOrNull() ?: continue
            if (!isPointcutMatches(element, pointcut)) continue

            val dot = signature.lastIndexOf('.')
            val className = signature.substring(0, dot)
            val elementName = signature.substring(dot + 1)
            val isField = elementName.startsWith("$")

            val adviceClass = phpIndex.getClassesByFQN(className).firstOrNull() ?: continue
            val adviceElement: PhpNamedElement? = if (isField) {
                adviceClass.findOwnFieldByName(elementName.removePrefix("$"), false)
            } else {
                adviceClass.findMethodByName(elementName)
            }
            adviceElement?.let { result.add(it) }
        }
        return result
    }

    @JvmStatic
    fun getMatchedElements(aspectMember: PhpNamedElement): List<PhpNamedElement>? {
        val project = aspectMember.project
        val scope = GlobalSearchScope.projectScope(project)
        val pointcutFQN = aspectMember.fqn ?: return null
        val pointcut = fileBasedIndex.getValues(index, pointcutFQN, scope).firstOrNull() ?: return null

        val result = mutableListOf<PhpNamedElement>()
        val classList = mutableSetOf<PhpClass>()

        StubIndex.getInstance().getAllKeys(PhpClassIndex.KEY, project).forEach { classKey ->
            StubIndex.getInstance().processElements(
                PhpClassIndex.KEY, classKey, project, scope, PhpClass::class.java
            ) { instance ->
                if (pointcut.getClassFilter().matches(instance)) classList.add(instance)
                false
            }
        }

        if (KindFilter.KIND_METHOD in pointcut.getKind()) {
            classList.forEach { phpClass ->
                ReadAction.run<RuntimeException> {
                    phpClass.ownMethods.filter { isPointcutMatches(it, pointcut) }.forEach { result.add(it) }
                }
            }
        }

        if (KindFilter.KIND_PROPERTY in pointcut.getKind()) {
            classList.forEach { phpClass ->
                ReadAction.run<RuntimeException> {
                    phpClass.ownFields.filter { isPointcutMatches(it, pointcut) }.forEach { result.add(it) }
                }
            }
        }

        return result
    }

    private fun isPointcutMatches(element: PhpNamedElement, pointcut: Pointcut): Boolean {
        if (!canMatchElement(element, pointcut.getKind())) return false

        val parentElement: PhpNamedElement? = when (element) {
            is PhpClassMember -> element.containingClass
            is PhpClass -> element
            else -> null
        }

        val classFilter = pointcut.getClassFilter()
        if (parentElement == null || !canMatchElement(parentElement, classFilter.getKind())) return false
        if (!classFilter.matches(parentElement)) return false

        return pointcut.matches(element)
    }

    private fun canMatchElement(element: PhpNamedElement?, filterKind: Set<KindFilter>): Boolean {
        return when (element) {
            is Method   -> KindFilter.KIND_METHOD in filterKind
            is Field    -> KindFilter.KIND_PROPERTY in filterKind
            is PhpClass -> !element.isInterface && !PluginUtil.isAspect(element)
                           && KindFilter.KIND_CLASS in filterKind
            else        -> false
        }
    }
}
