package com.aopphp.go.util

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpAttribute
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl
import com.jetbrains.php.lang.psi.elements.impl.ParameterListImpl

/**
 * The attributes of the Go! AOP 4.0 `Go\Lang\Attribute` namespace and the PHP strings that hold pointcut expressions.
 */
object GoAopAttributes {
    const val NAMESPACE = "\\Go\\Lang\\Attribute\\"

    const val BEFORE = "\\Go\\Lang\\Attribute\\Before"
    const val AFTER = "\\Go\\Lang\\Attribute\\After"
    const val AFTER_THROWING = "\\Go\\Lang\\Attribute\\AfterThrowing"
    const val AROUND = "\\Go\\Lang\\Attribute\\Around"
    const val POINTCUT = "\\Go\\Lang\\Attribute\\Pointcut"
    const val DECLARE_PARENTS = "\\Go\\Lang\\Attribute\\DeclareParents"

    /** Removed in Go! AOP 4.0 */
    const val ASPECT = "\\Go\\Lang\\Attribute\\Aspect"

    /** Removed in Go! AOP 4.0 */
    const val DECLARE_ERROR = "\\Go\\Lang\\Attribute\\DeclareError"

    const val POINTCUT_BUILDER = "\\Go\\Aop\\Support\\PointcutBuilder"

    /** Attributes that turn an aspect method into an advice */
    @JvmField
    val ADVICE_ATTRIBUTES: Set<String> = setOf(BEFORE, AFTER, AFTER_THROWING, AROUND)

    /** Attributes whose `expression` argument is a pointcut expression */
    @JvmField
    val EXPRESSION_ATTRIBUTES: Set<String> = ADVICE_ATTRIBUTES + POINTCUT + DECLARE_PARENTS

    /** Attributes removed in Go! AOP 4.0, with the migration hint */
    @JvmField
    val REMOVED_ATTRIBUTES: Map<String, String> = mapOf(
        ASPECT to "The #[Aspect] attribute was removed in Go! AOP 4.0: aspects are identified by implementing Go\\Aop\\Aspect",
        DECLARE_ERROR to "#[DeclareError] was removed in Go! AOP 4.0: emit the warning or throw the exception from a #[Before] or #[Around] advice",
    )

    /** Returns the string literal holding the pointcut expression of [attribute], if it is a Go! AOP attribute */
    @JvmStatic
    fun expressionArgument(attribute: PhpAttribute): StringLiteralExpression? {
        if (attribute.fqn !in EXPRESSION_ATTRIBUTES) return null
        return expressionArgument(attribute.parameters, "expression")
    }

    /**
     * Returns true when [host] is the pointcut expression of a Go! AOP attribute
     * (`#[Before('...')]`, `#[DeclareParents(expression: '...', ...)]`) or of a `PointcutBuilder` call.
     */
    @JvmStatic
    fun isPointcutExpression(host: StringLiteralExpression): Boolean {
        val paramList = host.parent as? ParameterList ?: return false
        when (val owner = paramList.parent) {
            is PhpAttribute -> return expressionArgument(owner) == host
            is MethodReference -> {
                if (expressionArgument(paramList.parameters, "pointcutExpression") != host) return false
                val resolved = owner.resolve() as? MethodImpl ?: return false
                return resolved.fqn.startsWith(POINTCUT_BUILDER)
            }
            else -> return false
        }
    }

    /** The argument named [name], or the first positional argument */
    private fun expressionArgument(parameters: Array<PsiElement>, name: String): StringLiteralExpression? {
        var positional: PsiElement? = null
        for (parameter in parameters) {
            val argumentName = ParameterListImpl.getNameIdentifier(parameter)?.text
            if (argumentName == name) return parameter as? StringLiteralExpression
            if (argumentName == null && positional == null) positional = parameter
        }
        return positional as? StringLiteralExpression
    }
}
