package com.aopphp.go.pointcut

import com.aopphp.go.index.AttributePointcutExpressionIndex
import com.aopphp.go.util.PluginUtil
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.stubs.indexes.PhpClassIndex
import com.jetbrains.php.lang.psi.stubs.indexes.PhpTraitIndex

object PointcutAdvisor {

    private val fileBasedIndex = FileBasedIndex.getInstance()
    private val index = AttributePointcutExpressionIndex.KEY

    @JvmStatic
    fun getMatchedAdvices(element: PhpNamedElement): List<PhpNamedElement> {
        val result = mutableListOf<PhpNamedElement>()
        val project = element.project
        val phpIndex = PhpIndex.getInstance(project)
        val scope = GlobalSearchScope.allScope(project)

        for (signature in fileBasedIndex.getAllKeys(index, project)) {
            ProgressManager.checkCanceled()
            val pointcut = fileBasedIndex.getValues(index, signature, scope).firstOrNull() ?: continue
            if (!isPointcutMatches(element, pointcut)) continue

            val dot = signature.lastIndexOf('.')
            val className = signature.substring(0, dot)
            val memberName = signature.substring(dot + 1).removePrefix("$")

            val adviceClass = phpIndex.getClassesByFQN(className).firstOrNull() ?: continue
            val adviceElement: PhpNamedElement? =
                adviceClass.findMethodByName(memberName)
                    ?: adviceClass.findOwnFieldByName(memberName, false)
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

        collectClassLikeElements(PhpClassIndex.KEY, pointcut, project, scope, classList)
        collectClassLikeElements(PhpTraitIndex.KEY, pointcut, project, scope, classList)

        if (KindFilter.KIND_METHOD in pointcut.getKind()) {
            classList.forEach { phpClass ->
                ProgressManager.checkCanceled()
                runReadAction {
                    phpClass.ownMethods.filter { isPointcutMatches(it, pointcut) }.forEach { result.add(it) }
                }
            }
        }

        if (KindFilter.KIND_PROPERTY in pointcut.getKind()) {
            classList.forEach { phpClass ->
                ProgressManager.checkCanceled()
                runReadAction {
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

    private fun collectClassLikeElements(
        indexKey: StubIndexKey<String, PhpClass>,
        pointcut: Pointcut,
        project: Project,
        scope: GlobalSearchScope,
        classList: MutableSet<PhpClass>
    ) {
        val classFilter = pointcut.getClassFilter()
        StubIndex.getInstance().getAllKeys(indexKey, project).forEach { key ->
            ProgressManager.checkCanceled()
            try {
                StubIndex.getInstance().processElements(
                    indexKey, key, project, scope, PhpClass::class.java
                ) { instance ->
                    if (classFilter.matches(instance)) classList.add(instance)
                    false
                }
            } catch (e: ProcessCanceledException) {
                throw e
            } catch (_: Exception) {
                // Skip stale/outdated stub entries — the index will be updated on the next re-index pass
            }
        }
    }
}
