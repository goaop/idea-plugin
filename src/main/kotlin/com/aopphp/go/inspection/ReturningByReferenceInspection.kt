package com.aopphp.go.inspection

import com.aopphp.go.pointcut.PointcutAdvisor
import com.aopphp.go.util.PluginUtil
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor

/**
 * Go! AOP 4.0 can not weave a method declared `function &name()`: the joinpoint would return a copy instead
 * of the reference, so weaving fails with a `WeavingException`. Reports such methods selected by an advice.
 */
class ReturningByReferenceInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor =
        object : PhpElementVisitor() {
            override fun visitPhpMethod(method: Method) {
                if (!method.isReturningByReference) return
                val containingClass = method.containingClass ?: return
                if (PluginUtil.isAspect(containingClass)) return

                val advices = PointcutAdvisor.getMatchedAdvices(method)
                if (advices.isEmpty()) return

                val nameIdentifier = method.nameIdentifier ?: return
                val advice = advices.first().fqn.removePrefix("\\")
                holder.registerProblem(
                    nameIdentifier,
                    "Method returning by reference is matched by '$advice' and can not be woven: " +
                        "add '&& !matchReturningByReference()' to the pointcut",
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                )
            }
        }
}
