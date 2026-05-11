package com.aopphp.go.typing

import com.aopphp.go.PointcutQueryLanguage
import com.aopphp.go.psi.MemberReference
import com.aopphp.go.psi.PointcutTypes
import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
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
 *  SPACE typed after a visibility modifier (public/protected/private/final)
 *              → class-name completion popup (the modifier has been chosen, time to pick a class).
 */
class PointcutMemberAccessTypingHandler : TypedHandlerDelegate() {

    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (c != '>' && c != ':' && c != '(' && c != ' ') return Result.CONTINUE

        val offset = editor.caretModel.offset
        val injectionManager = InjectedLanguageManager.getInstance(project)

        when (c) {
            // ------------------------------------------------------------------ -> / ::
            '>', ':' -> {
                if (offset < 2) return Result.CONTINUE
                val text = editor.document.charsSequence
                val prevChar = text[offset - 2]
                val twoChars = "$prevChar$c"
                if (twoChars != "->" && twoChars != "::") return Result.CONTINUE

                val injectedElement = injectionManager.findInjectedElementAt(file, offset - 2)
                    ?: return Result.CONTINUE
                if (injectedElement.containingFile?.language != PointcutQueryLanguage) return Result.CONTINUE

                // Only inside a MemberReference (execution/access) — not within() etc.
                val memberRef = PsiTreeUtil.getParentOfType(injectedElement, MemberReference::class.java)
                    ?: return Result.CONTINUE

                AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
            }

            // ------------------------------------------------------------------ ( after keyword
            '(' -> {
                if (offset < 1) return Result.CONTINUE
                // The '(' we just typed is at offset-1 in the document.
                val injectedElement = injectionManager.findInjectedElementAt(file, offset - 1)
                    ?: return Result.CONTINUE
                if (injectedElement.containingFile?.language != PointcutQueryLanguage) return Result.CONTINUE

                // T_LEFT_PAREN; check what keyword comes before it
                val prevLeaf = PsiTreeUtil.prevLeaf(injectedElement) ?: return Result.CONTINUE
                val prevType = prevLeaf.node?.elementType
                if (prevType == PointcutTypes.EXECUTION ||
                    prevType == PointcutTypes.ACCESS     ||
                    prevType == PointcutTypes.WITHIN     ||
                    prevType == PointcutTypes.STATICINITIALIZATION ||
                    prevType == PointcutTypes.INITIALIZATION
                ) {
                    AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
                }
            }

            // ------------------------------------------------------------------ SPACE after modifier
            ' ' -> {
                if (offset < 1) return Result.CONTINUE
                // Space at offset-1; check the token just before the space
                val injectedElement = injectionManager.findInjectedElementAt(file, offset - 1)
                    ?: return Result.CONTINUE
                if (injectedElement.containingFile?.language != PointcutQueryLanguage) return Result.CONTINUE

                // WHITE_SPACE; prev token is the modifier keyword
                val prevLeaf = PsiTreeUtil.prevLeaf(injectedElement) ?: return Result.CONTINUE
                val prevType = prevLeaf.node?.elementType
                if (prevType == PointcutTypes.PUBLIC    ||
                    prevType == PointcutTypes.PROTECTED ||
                    prevType == PointcutTypes.PRIVATE   ||
                    prevType == PointcutTypes.FINAL
                ) {
                    AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
                }
            }
        }

        return Result.CONTINUE
    }
}
