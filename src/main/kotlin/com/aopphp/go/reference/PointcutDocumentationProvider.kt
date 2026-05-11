package com.aopphp.go.reference

import com.aopphp.go.pattern.CodePattern
import com.aopphp.go.psi.NamespaceName
import com.aopphp.go.psi.PointcutTypes
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.php.PhpIndex

/**
 * Provides hover documentation for class names inside Go! AOP pointcut expressions.
 *
 * By returning the resolved PhpClass as the "custom documentation element",
 * IntelliJ automatically delegates to PHP's own documentation provider — so the
 * user sees the same rich PHP class doc as when hovering over a PHP type hint.
 */
class PointcutDocumentationProvider : AbstractDocumentationProvider() {

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        contextElement ?: return null
        if (contextElement.node?.elementType != PointcutTypes.T_NAME_PART) return null
        if (!CodePattern.insideAnnotationPointcut().accepts(contextElement)) return null
        val namespaceName = contextElement.parent as? NamespaceName ?: return null
        val fqn = namespaceName.getFQN()
        return PhpIndex.getInstance(contextElement.project).getClassesByFQN(fqn).firstOrNull()
    }
}
