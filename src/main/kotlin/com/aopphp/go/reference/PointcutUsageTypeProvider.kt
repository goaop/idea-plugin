package com.aopphp.go.reference

import com.aopphp.go.pattern.CodePattern
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
        if (!CodePattern.isInsidePhpAttribute(host, GO_AOP_ATTR_PREFIX)
            && !CodePattern.isInsidePointcutBuilderMethod(host)
        ) {
            return null
        }
        return POINTCUT_USAGE_TYPE
    }

    companion object {
        private const val GO_AOP_ATTR_PREFIX = "\\Go\\Lang\\Attribute"
        private val POINTCUT_USAGE_TYPE = UsageType { "References in AOP pointcuts" }
    }
}
