package com.aopphp.go.inspection

import com.aopphp.go.util.GoAopAttributes
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpModifierList
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

/**
 * Checks the Go! AOP 4.0 rules for advice methods, which the aspect loader enforces with an `AspectException`:
 * - a method with `#[Before]`, `#[After]`, `#[Around]` or `#[AfterThrowing]` must be public,
 *   because generated proxies call it as a first-class callable on the aspect instance;
 * - a method carries at most one advice attribute (`#[Pointcut]` can be combined with one).
 */
class AdviceMethodInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
        object : PhpElementVisitor() {
            override fun visitPhpMethod(method: Method) {
                val adviceAttributes = method.attributes.filter { it.fqn in GoAopAttributes.ADVICE_ATTRIBUTES }
                if (adviceAttributes.isEmpty()) return

                adviceAttributes.drop(1).forEach { attribute ->
                    holder.registerProblem(
                        attribute,
                        "Only one advice attribute per method is allowed: split it into one method per advice",
                        ProblemHighlightType.GENERIC_ERROR
                    )
                }

                if (!method.modifier.isPublic) {
                    val nameIdentifier = method.nameIdentifier ?: return
                    holder.registerProblem(
                        nameIdentifier,
                        "Advice method '${method.name}' must be public: Go! AOP 4.0 calls advices as first-class callables",
                        ProblemHighlightType.GENERIC_ERROR,
                        MakePublicFix()
                    )
                }
            }
        }

    private class MakePublicFix : LocalQuickFix {
        override fun getFamilyName() = "Make method public"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val method = PsiTreeUtil.getParentOfType(descriptor.psiElement, Method::class.java) ?: return
            val modifierList = PsiTreeUtil.getChildOfType(method, PhpModifierList::class.java) ?: return
            val visibility = generateSequence(modifierList.firstChild) { it.nextSibling }
                .firstOrNull { it.text == "protected" || it.text == "private" } ?: return
            val document = PsiDocumentManager.getInstance(project).getDocument(method.containingFile) ?: return
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)
            document.replaceString(visibility.textRange.startOffset, visibility.textRange.endOffset, "public")
            PsiDocumentManager.getInstance(project).commitDocument(document)
        }
    }
}
