package com.aopphp.go.completion

import com.aopphp.go.psi.PointcutReference
import com.aopphp.go.psi.PointcutTypes
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpAttribute
import com.jetbrains.php.lang.psi.elements.PhpAttributesOwner
import com.jetbrains.php.lang.psi.elements.PhpClass

/**
 * Provides completion for `$this->` pointcut references inside Go! AOP pointcut expressions.
 *
 * When the user types `$this->` inside a pointcut string (e.g. `#[Before('$this->')}`),
 * this provider lists all methods and properties of the containing aspect class that are
 * annotated with `#[\Go\Lang\Attribute\Pointcut]`.
 */
class SelfPointcutReferenceCompletionProvider : CompletionProvider<CompletionParameters>() {

    companion object {
        private const val POINTCUT_ATTR_FQN = "\\Go\\Lang\\Attribute\\Pointcut"
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position

        // Verify that the enclosing PointcutReference begins with $this (not a qualified class name)
        val pointcutRef = PsiTreeUtil.getParentOfType(position, PointcutReference::class.java) ?: return
        if (pointcutRef.firstChild?.node?.elementType != PointcutTypes.T_THIS) return

        // Navigate from the injected-language PSI back to the host PHP StringLiteralExpression.
        // Must pass an actual injected element (position), not the file — the file is not
        // itself an injection fragment so getInjectionHost(file) may return null.
        val injectionHost = InjectedLanguageManager.getInstance(position.project)
            .getInjectionHost(position) ?: return

        // StringLiteralExpression → ParameterList → PhpAttribute → ... → PhpClass
        val paramList = injectionHost.parent as? ParameterList ?: return
        val phpAttribute = paramList.parent as? PhpAttribute ?: return
        val phpClass = PsiTreeUtil.getParentOfType(phpAttribute, PhpClass::class.java) ?: return

        // Suggest methods tagged with #[\Go\Lang\Attribute\Pointcut]
        phpClass.ownMethods
            .filter { method -> (method as? PhpAttributesOwner)?.attributes?.any { it.fqn == POINTCUT_ATTR_FQN } == true }
            .forEach { method ->
                result.addElement(
                    LookupElementBuilder.createWithSmartPointer(method.name, method)
                        .withIcon(method.getIcon(0))
                        .withTypeText("Pointcut")
                )
            }

        // Suggest fields tagged with #[\Go\Lang\Attribute\Pointcut]
        phpClass.ownFields
            .filter { field -> (field as? PhpAttributesOwner)?.attributes?.any { it.fqn == POINTCUT_ATTR_FQN } == true }
            .forEach { field ->
                result.addElement(
                    LookupElementBuilder.createWithSmartPointer(field.name, field)
                        .withIcon(field.getIcon(0))
                        .withTypeText("Pointcut")
                )
            }
    }
}
