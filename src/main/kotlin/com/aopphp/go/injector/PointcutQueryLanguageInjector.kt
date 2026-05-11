package com.aopphp.go.injector

import com.aopphp.go.PointcutQueryLanguage
import com.aopphp.go.pattern.CodePattern
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import kotlin.math.max

/**
 * Injects the Go! AOP Pointcut query language into PHP string literals that are:
 *  - arguments of #[\Go\Lang\Attribute\*] PHP 8 attributes
 *  - arguments of PointcutBuilder->method() calls
 *
 * Uses MultiHostInjector so both single-quoted and double-quoted strings are covered.
 */
class PointcutQueryLanguageInjector : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is StringLiteralExpression) return
        val inject = CodePattern.isInsidePhpAttribute(context, GO_AOP_ATTRIBUTE_PREFIX)
                  || CodePattern.isInsidePointcutBuilderMethod(context)
        if (!inject) return
        val range = TextRange(1, max(context.textLength - 1, 1))
        registrar.startInjecting(PointcutQueryLanguage)
            .addPlace(null, null, context as PsiLanguageInjectionHost, range)
            .doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(StringLiteralExpression::class.java)

    companion object {
        private const val GO_AOP_ATTRIBUTE_PREFIX = "\\Go\\Lang\\Attribute"
    }
}
