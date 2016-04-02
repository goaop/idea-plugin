package com.aopphp.go.injector;

import com.aopphp.go.PointcutQueryLanguage;
import com.aopphp.go.pattern.CodePattern;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;

/**
 * PointcutQuery injector is responsible to inject Go! AOP Pointcut syntax into the StringLiteral expressions
 */
public class PointcutQueryLanguageInjector implements LanguageInjector {

    private static final String GO_AOP_ANNOTATION = "\\Go\\Lang\\Annotation";

    @Override
    public void getLanguagesToInject(
            @NotNull PsiLanguageInjectionHost host,
            @NotNull InjectedLanguagePlaces injectionPlacesRegistrar)
    {
        // we accept only PHP literal expressions, such as docBlocks and string for PointcutBuilder->method('<expr>')
        if (!(host instanceof StringLiteralExpression)) {
            return;
        }

        StringLiteralExpression literalExpression = (StringLiteralExpression) host;

        boolean enableInjection = CodePattern.isInsideAnnotation(literalExpression, GO_AOP_ANNOTATION);
        enableInjection |= CodePattern.isInsidePointcutBuilderMethod(literalExpression);

        if (enableInjection) {
            TextRange rangeInsideHost = new TextRange(1, Math.max(literalExpression.getTextLength()-1, 1));
            injectionPlacesRegistrar.addPlace(PointcutQueryLanguage.INSTANCE, rangeInsideHost, null, null);
        }
    }
}
