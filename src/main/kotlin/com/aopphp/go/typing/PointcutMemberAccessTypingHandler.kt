package com.aopphp.go.typing

import com.aopphp.go.PointcutQueryLanguage
import com.aopphp.go.psi.MemberReference
import com.aopphp.go.psi.PointcutTypes
import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil

/**
 * Schedules auto-completion popups at contextually appropriate moments inside
 * Go! AOP pointcut expressions injected into PHP strings.
 *
 * Trigger events:
 *
 *  `->` / `::` typed  → member-name completion popup (methods for execution, properties for access).
 *
 *  `(` typed after an execution/access/within/staticinitialization/initialization keyword
 *              → class-name completion popup immediately, so the user does not need to
 *                type the first character of the class name manually.
 *
 *  SPACE typed after a modifier (public/protected/private/final/readonly/private(set)/protected(set))
 *              → class-name completion popup (the modifier has been chosen, time to pick a class).
 */
class PointcutMemberAccessTypingHandler : TypedHandlerDelegate() {

    private companion object {
        /** Keywords whose `(` opens a class pattern */
        val CLASS_PATTERN_KEYWORDS = setOf(
            PointcutTypes.EXECUTION, PointcutTypes.ACCESS, PointcutTypes.WITHIN,
            PointcutTypes.STATICINITIALIZATION, PointcutTypes.INITIALIZATION,
        )

        /** Modifiers followed by the class pattern */
        val MEMBER_MODIFIERS = setOf(
            PointcutTypes.PUBLIC, PointcutTypes.PROTECTED, PointcutTypes.PRIVATE, PointcutTypes.FINAL,
            PointcutTypes.READONLY, PointcutTypes.PRIVATE_SET, PointcutTypes.PROTECTED_SET,
        )
    }

    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        val offset = editor.caretModel.offset
        val text = editor.document.charsSequence
        val showPopup = when (c) {
            // -> or :: inside a member reference (execution/access), not within() etc.
            '>', ':' -> {
                val isMemberAccess = offset >= 2 && text[offset - 2] == (if (c == '>') '-' else ':')
                val injectedElement = if (isMemberAccess) injectedPointcutElementAt(project, file, offset - 2) else null
                injectedElement != null && PsiTreeUtil.getParentOfType(injectedElement, MemberReference::class.java) != null
            }
            // ( after a keyword that takes a class pattern
            '(' -> previousTokenType(project, file, offset) in CLASS_PATTERN_KEYWORDS
            // space after a modifier
            ' ' -> previousTokenType(project, file, offset) in MEMBER_MODIFIERS
            else -> false
        }
        if (showPopup) {
            AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
        }

        return Result.CONTINUE
    }

    /** Returns the pointcut PSI element injected at [offset] of the host [file], if any */
    private fun injectedPointcutElementAt(project: Project, file: PsiFile, offset: Int): PsiElement? {
        val injectedElement = InjectedLanguageManager.getInstance(project).findInjectedElementAt(file, offset)
        return injectedElement?.takeIf { it.containingFile?.language == PointcutQueryLanguage }
    }

    /** Returns the type of the pointcut token before the character just typed at [caretOffset] - 1 */
    private fun previousTokenType(project: Project, file: PsiFile, caretOffset: Int): IElementType? {
        if (caretOffset < 1) return null
        val typedElement = injectedPointcutElementAt(project, file, caretOffset - 1) ?: return null
        return PsiTreeUtil.prevLeaf(typedElement)?.node?.elementType
    }
}
