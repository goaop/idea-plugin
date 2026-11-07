package com.aopphp.go.reference

import com.aopphp.go.util.GoAopAttributes
import com.intellij.psi.PsiElement
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProvider
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * Labels usages found inside Go! AOP pointcut expressions as
 * "References in AOP pointcuts" instead of the default "Unclassified".
 */
class PointcutUsageTypeProvider : UsageTypeProvider {

    override fun getUsageType(element: PsiElement): UsageType? {
        val host = element as? StringLiteralExpression ?: return null
        if (!GoAopAttributes.isPointcutExpression(host)) return null
        return POINTCUT_USAGE_TYPE
    }

    companion object {
        private val POINTCUT_USAGE_TYPE = UsageType { "References in AOP pointcuts" }
    }
}
