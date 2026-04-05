package com.aopphp.go.reference

import com.aopphp.go.index.AttributePointcutExpressionIndex
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.UseScopeEnlarger
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Enlarges the use scope of PHP named elements to include files that contain
 * Go! AOP pointcut expressions.
 *
 * Without this, PHP's Find Usages may limit the search scope to files that
 * contain direct PHP references, missing aspect files where classes and members
 * are referenced inside pointcut expression strings.
 */
class PointcutUseScopeEnlarger : UseScopeEnlarger() {

    override fun getAdditionalUseScope(element: PsiElement): SearchScope? {
        if (element !is PhpNamedElement) return null

        val project = element.project
        val fileIndex = FileBasedIndex.getInstance()
        val projectScope = GlobalSearchScope.projectScope(project)

        val aspectFiles = mutableSetOf<com.intellij.openapi.vfs.VirtualFile>()
        fileIndex.processAllKeys(AttributePointcutExpressionIndex.KEY, { key ->
            aspectFiles.addAll(
                fileIndex.getContainingFiles(AttributePointcutExpressionIndex.KEY, key, projectScope)
            )
            true
        }, projectScope, null)

        if (aspectFiles.isEmpty()) return null
        return GlobalSearchScope.filesWithoutLibrariesScope(project, aspectFiles)
    }
}
