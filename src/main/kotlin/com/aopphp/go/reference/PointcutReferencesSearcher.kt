package com.aopphp.go.reference

import com.aopphp.go.PointcutQueryLanguage
import com.aopphp.go.index.AttributePointcutExpressionIndex
import com.aopphp.go.psi.PointcutTypes
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * Extends "Find Usages" for PHP classes and class members to include references inside
 * Go! AOP pointcut expressions injected into PHP string literals.
 *
 * Strategy: use the plugin's own [AttributePointcutExpressionIndex] to locate the (small) set
 * of PHP aspect files, then explicitly walk their injection hosts and the injected pointcut PSI.
 * This bypasses IntelliJ's word index entirely — word-index-based approaches are unreliable for
 * content inside injected language fragments because the host language's word scanner may not
 * tokenize string-literal content the way the injected language expects.
 */
class PointcutReferencesSearcher : QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>(true) {

    override fun processQuery(
        params: ReferencesSearch.SearchParameters,
        consumer: Processor<in PsiReference>
    ) {
        val target = params.elementToSearch
        if (target !is PhpNamedElement) return
        val shortName = target.name?.takeIf { it.isNotEmpty() } ?: return

        val project = target.project
        val scope = params.effectiveSearchScope as? GlobalSearchScope
            ?: GlobalSearchScope.projectScope(project)

        // Step 1: collect all PHP files that contain AOP pointcut expressions.
        // The index keys are aspect-member FQNs; we only need the *files*.
        val fileIndex = FileBasedIndex.getInstance()
        val aspectFiles = mutableSetOf<VirtualFile>()
        for (key in fileIndex.getAllKeys(AttributePointcutExpressionIndex.KEY, project)) {
            aspectFiles.addAll(
                fileIndex.getContainingFiles(AttributePointcutExpressionIndex.KEY, key, scope)
            )
        }

        val psiManager = PsiManager.getInstance(project)
        val injectedManager = InjectedLanguageManager.getInstance(project)

        for (vFile in aspectFiles) {
            val psiFile = psiManager.findFile(vFile) ?: continue

            // Step 2: find every StringLiteralExpression in the aspect file
            // (potential injection host for a pointcut expression).
            val hosts = PsiTreeUtil.collectElementsOfType(psiFile, StringLiteralExpression::class.java)

            for (host in hosts) {
                // Step 3: enumerate injected PSI fragments hosted by this string literal.
                // enumerate() computes injections on-the-fly if they haven't been cached yet.
                injectedManager.enumerate(host) { injectedPsi, _ ->
                    if (injectedPsi.language != PointcutQueryLanguage) return@enumerate

                    // Step 4: walk every element in the injected pointcut PSI tree and check
                    // whether any T_NAME_PART token with matching text has a reference that
                    // resolves to the PHP target element.
                    PsiTreeUtil.processElements(injectedPsi) { injected ->
                        if (injected.node?.elementType == PointcutTypes.T_NAME_PART
                            && injected.text == shortName
                        ) {
                            for (ref in injected.references) {
                                if (ref.isReferenceTo(target)) {
                                    if (!consumer.process(ref)) return@processElements false
                                }
                            }
                        }
                        true
                    }
                }
            }
        }
    }
}
