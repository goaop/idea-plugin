package com.aopphp.go.injector;

import com.aopphp.go.PointcutQueryLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl;
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

        boolean enableInjection = isInjectionIntoAnnotation(host);
        enableInjection |= isInjectionIntoPointcutBuilder(host);

        if (enableInjection && host.getTextLength() > 1) {
            TextRange rangeInsideHost = new TextRange(1, host.getTextLength() - 1);
            injectionPlacesRegistrar.addPlace(PointcutQueryLanguage.INSTANCE, rangeInsideHost, null, null);
        }
    }

    /**
     * Checks if injection is performed into PointcutBuilder->method('<pointcutExpression>', function () {...})
     * @param host Host element is typically PHP string
     *
     * @return boolean
     */
    private boolean isInjectionIntoPointcutBuilder(PsiLanguageInjectionHost host)
    {
        PsiElement element = host.getParent();
        if (!(element instanceof ParameterList)) {
            return false;
        }
        element = element.getParent();
        if (!(element instanceof MethodReference)) {
            return false;
        }

        MethodImpl methodImpl = (MethodImpl) ((MethodReference) element).resolve();
        if (methodImpl == null || methodImpl.getFQN() == null) {
            return false;
        }

        return methodImpl.getFQN().startsWith("\\Go\\Aop\\Support\\PointcutBuilder");
    }

    /**
     * Checks if injection is performed into annotation
     *
     * @param host Host element, typically string literal expression
     *
     * @return boolean
     */
    private boolean isInjectionIntoAnnotation (@NotNull PsiLanguageInjectionHost host)
    {
        boolean enableInjection = false;

        PhpDocTag phpDocTag = PsiTreeUtil.getParentOfType(host, PhpDocTag.class);
        if (phpDocTag == null || !AnnotationPattern.getDocBlockTag().accepts(phpDocTag)) {
            return false;
        }

        PhpDocTagAnnotation phpDocAnnotationContainer = AnnotationUtil.getPhpDocAnnotationContainer(phpDocTag);
        PhpClass phpClass = null;
        if (phpDocAnnotationContainer != null) {
            phpClass = phpDocAnnotationContainer.getPhpClass();
        }

        if (phpClass == null || phpClass.getFQN() == null) {
            return false;
        }

        if (phpClass.getFQN().startsWith(GO_AOP_ANNOTATION)) {
            enableInjection = true;
        }

        return enableInjection;
    }
}
