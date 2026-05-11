package com.aopphp.go.reference

import com.aopphp.go.index.AttributePointcutExpressionIndex
import com.intellij.openapi.application.QueryExecutorBase
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
 * Custom reference searcher that finds usages of PHP classes, methods, and properties
 * inside Go! AOP pointcut expressions.
 *
 * PHP's standard Find Usages relies on a word-index scan that does not find identifiers
 * buried inside string literals (where pointcut expressions live). This searcher
 * explicitly walks aspect files found via [AttributePointcutExpressionIndex] and checks
 * each pointcut-hosting [StringLiteralExpression] for references to the target element.
 *
 * References are provided by [PointcutPhpReferenceContributor] which attaches
 * [PsiReference] objects directly on the PHP string literal.
 */
class PointcutReferencesSearcher :
    QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>(true) {

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

        val fileIndex = FileBasedIndex.getInstance()
        val aspectFiles = mutableSetOf<com.intellij.openapi.vfs.VirtualFile>()
        fileIndex.processAllKeys(AttributePointcutExpressionIndex.KEY, { key ->
            aspectFiles.addAll(
                fileIndex.getContainingFiles(AttributePointcutExpressionIndex.KEY, key, scope)
            )
            true
        }, scope, null)

        val psiManager = PsiManager.getInstance(project)
        for (vFile in aspectFiles) {
            val psiFile = psiManager.findFile(vFile) ?: continue

            val hosts = PsiTreeUtil.collectElementsOfType(psiFile, StringLiteralExpression::class.java)
            for (host in hosts) {
                if (!host.contents.contains(shortName)) continue

                val refs = PointcutPhpReferenceContributor.getReferencesForHost(host)
                for (ref in refs) {
                    if (ref.isReferenceTo(target)) {
                        if (!consumer.process(ref)) return
                    }
                }
            }
        }
    }
}
