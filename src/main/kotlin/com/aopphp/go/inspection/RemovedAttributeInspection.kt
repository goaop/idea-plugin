package com.aopphp.go.inspection

import com.aopphp.go.util.GoAopAttributes
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.psi.elements.PhpAttribute
import com.jetbrains.php.lang.psi.elements.PhpAttributesList
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

/**
 * Reports the attributes removed in Go! AOP 4.0: `#[Aspect]`, which the framework never read,
 * and `#[DeclareError]`, whose interceptor is gone.
 */
class RemovedAttributeInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
        object : PhpElementVisitor() {
            override fun visitPhpAttribute(attribute: PhpAttribute) {
                val fqn = attribute.fqn ?: return
                val message = GoAopAttributes.REMOVED_ATTRIBUTES[fqn] ?: return
                val fixes = if (fqn == GoAopAttributes.ASPECT) arrayOf<LocalQuickFix>(RemoveAttributeFix()) else emptyArray()
                holder.registerProblem(attribute, message, ProblemHighlightType.LIKE_DEPRECATED, *fixes)
            }
        }

    private class RemoveAttributeFix : LocalQuickFix {
        override fun getFamilyName() = "Remove attribute"

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val attribute = descriptor.psiElement as? PhpAttribute ?: return
            val list = attribute.parent as? PhpAttributesList
            // Remove the whole #[...] group when it holds only this attribute
            if (list != null && list.attributes.size == 1) list.delete() else attribute.delete()
        }
    }
}
