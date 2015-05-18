package com.aopphp.go.injector;

import com.aopphp.go.PointcutQueryLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import de.espend.idea.php.annotation.dict.PhpDocTagAnnotation;
import de.espend.idea.php.annotation.pattern.AnnotationPattern;
import de.espend.idea.php.annotation.util.AnnotationUtil;
import org.jetbrains.annotations.NotNull;

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

        boolean enableInjection = false;

        PhpDocTag phpDocTag = PsiTreeUtil.getParentOfType(host, PhpDocTag.class);
        if (phpDocTag == null || !AnnotationPattern.getDocBlockTag().accepts(phpDocTag)) {
            return;
        }

        PhpDocTagAnnotation phpDocAnnotationContainer = AnnotationUtil.getPhpDocAnnotationContainer(phpDocTag);
        PhpClass phpClass = null;
        if (phpDocAnnotationContainer != null) {
            phpClass = phpDocAnnotationContainer.getPhpClass();
        }

        if (phpClass == null || phpClass.getFQN() == null) {
            return;
        }

        if (phpClass.getFQN().startsWith(GO_AOP_ANNOTATION)) {
            enableInjection = true;
        }

        if (enableInjection) {
            TextRange rangeInsideHost = new TextRange(1, host.getTextLength() - 1);
            injectionPlacesRegistrar.addPlace(PointcutQueryLanguage.INSTANCE, rangeInsideHost, "", "");
        }
    }
}
