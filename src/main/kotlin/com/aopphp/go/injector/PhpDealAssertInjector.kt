package com.aopphp.go.injector

import com.aopphp.go.pattern.CodePattern
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.jetbrains.php.lang.PhpLanguage
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpAttribute
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import kotlin.math.max

/**
 * Injects PHP syntax into string literal arguments of #[\PhpDeal\Annotation\*] attributes,
 * providing $this, $__old, $__result, and parameter type hints in the injected fragment.
 *
 * Uses MultiHostInjector so both single-quoted and double-quoted strings are covered.
 */
class PhpDealAssertInjector : MultiHostInjector {

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        if (context !is StringLiteralExpression) return
        if (!CodePattern.isInsidePhpAttribute(context, PHP_DEAL_ANNOTATION_PREFIX)) return

        val range = TextRange(1, max(context.textLength - 1, 1))

        // Navigate up to the attribute's owner (method / function / class)
        val paramList = context.parent as? ParameterList ?: return
        val attribute = paramList.parent as? PhpAttribute ?: return
        val owner = attribute.owner

        var prefix = "<?php\n"

        // Add @var hints for method parameters from the owning method's DocBlock
        if (owner is Method) {
            owner.docComment?.paramTags?.forEach { param ->
                prefix += "/** @var ${param.type.toStringResolved()} \$${param.varName} */\n"
            }
        }

        // Add $this and $__old typed as the containing class
        val classInstance: PhpClass? = when (owner) {
            is PhpClassMember -> owner.containingClass
            is PhpClass -> owner
            else -> null
        }
        if (classInstance != null && !classInstance.isInterface) {
            prefix += "/** @var ${classInstance.fqn} \$this */\n"
            prefix += "/** @var ${classInstance.fqn} \$__old */\n"
        }

        prefix += "/** @var mixed \$__result */\n"
        prefix += "/** @noinspection PhpVoidFunctionResultUsedInspection */\n"
        prefix += "return "

        registrar.startInjecting(PhpLanguage.INSTANCE)
            .addPlace(prefix, ";", context as PsiLanguageInjectionHost, range)
            .doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(StringLiteralExpression::class.java)

    companion object {
        private const val PHP_DEAL_ANNOTATION_PREFIX = "\\PhpDeal\\Annotation"
    }
}
