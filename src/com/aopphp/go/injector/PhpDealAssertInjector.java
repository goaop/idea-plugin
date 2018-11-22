package com.aopphp.go.injector;

import com.aopphp.go.pattern.CodePattern;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocParamTag;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

/**
 * PhpDeal injector is responsible to inject PHP syntax into the specific annotations
 */
public class PhpDealAssertInjector implements LanguageInjector {

    private static final String PHP_DEAL_ANNOTATION = "\\PhpDeal\\Annotation";

    @Override
    public void getLanguagesToInject(
            @NotNull PsiLanguageInjectionHost host,
            @NotNull InjectedLanguagePlaces injectionPlacesRegistrar)
    {
        // we accept only PHP literal expressions, such as docBlocks and string for PointcutBuilder->method('<expr>')
        if (!(host instanceof StringLiteralExpression) || !host.isValidHost()) {
            return;
        }
        StringLiteralExpression literalExpression = (StringLiteralExpression) host;
        if (!CodePattern.isInsideAnnotation(literalExpression, PHP_DEAL_ANNOTATION)) {
            return;
        }

        PhpClass classInstance    = null;
        TextRange rangeInsideHost = new TextRange(1, Math.max(literalExpression.getTextLength()-1, 1));
        String prefix = "<?php\n";
        String suffix = ";";

        PhpDocComment phpDocComment = PsiTreeUtil.getParentOfType(literalExpression, PhpDocComment.class);
        if (phpDocComment != null) {
            for (PhpDocParamTag param : phpDocComment.getParamTags()) {
                prefix += "/** @var " + param.getType().toStringResolved() + " $" + param.getVarName() + "*/\n";
            }
            PsiElement owner = phpDocComment.getOwner();
            if (owner instanceof PhpClassMember) {
                classInstance = ((PhpClassMember) owner).getContainingClass();
            } else if (owner instanceof PhpClass) {
                classInstance = (PhpClass) owner;
            }
        }
        if (classInstance != null && !classInstance.isInterface()) {
            prefix += "/** @var " + classInstance.getFQN() + " $this */\n";
            prefix += "/** @var " + classInstance.getFQN() + " $__old */\n";
        }

        prefix += "/** @var mixed $__result */\n";
        prefix += "/** @noinspection PhpVoidFunctionResultUsedInspection */\n";
        prefix += "return ";

        injectionPlacesRegistrar.addPlace(PhpLanguage.INSTANCE, rangeInsideHost, prefix, suffix);
    }
}
