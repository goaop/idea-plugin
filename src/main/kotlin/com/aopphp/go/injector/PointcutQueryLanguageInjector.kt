package com.aopphp.go.injector

import com.aopphp.go.PointcutQueryLanguage
import com.aopphp.go.pattern.CodePattern
import com.intellij.openapi.util.TextRange
import com.intellij.psi.InjectedLanguagePlaces
import com.intellij.psi.LanguageInjector
import com.intellij.psi.PsiLanguageInjectionHost
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import kotlin.math.max

/**
 * Injects the Go! AOP Pointcut query language into PHP string literals that are:
 *  - arguments of #[\Go\Lang\Annotation\*] PHP 8 attributes
 *  - arguments of PointcutBuilder->method() calls
 */
class PointcutQueryLanguageInjector : LanguageInjector {

    override fun getLanguagesToInject(
        host: PsiLanguageInjectionHost,
        injectionPlacesRegistrar: InjectedLanguagePlaces
    ) {
        if (host !is StringLiteralExpression || !host.isValidHost) return

        val inject = CodePattern.isInsidePhpAttribute(host, GO_AOP_ANNOTATION_PREFIX)
                  || CodePattern.isInsidePointcutBuilderMethod(host)

        if (inject) {
            val range = TextRange(1, max(host.textLength - 1, 1))
            injectionPlacesRegistrar.addPlace(PointcutQueryLanguage, range, null, null)
        }
    }

    companion object {
        private const val GO_AOP_ANNOTATION_PREFIX = "\\Go\\Lang\\Annotation"
    }
}
