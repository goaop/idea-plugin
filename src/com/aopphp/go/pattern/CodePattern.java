package com.aopphp.go.pattern;

import com.aopphp.go.PointcutQueryLanguage;
import com.aopphp.go.psi.AnnotatedAccessPointcut;
import com.aopphp.go.psi.AnnotatedExecutionPointcut;
import com.aopphp.go.psi.AnnotatedWithinPointcut;
import com.aopphp.go.psi.PointcutTypes;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
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

/**
 * Code pattern contains some useful patterns for matching items
 */
public class CodePattern extends PlatformPatterns {

    /**
     * Matching name part after left parenthesis and one of annotation pointcut type
     *  \@execution(<className>)
     *  \@access(<className>)
     *  \@within(<className>)
     */
    public static ElementPattern<PsiElement> insideAnnotationPointcut() {
        return or(
            psiElement(PointcutTypes.T_NAME_PART).withSuperParent(2, AnnotatedExecutionPointcut.class),
            psiElement(PointcutTypes.T_NAME_PART).withSuperParent(2, AnnotatedAccessPointcut.class),
            psiElement(PointcutTypes.T_NAME_PART).withSuperParent(2, AnnotatedWithinPointcut.class)
        );
    }

    public static ElementPattern<PsiElement> insidePointcutLanguage() {
        return psiElement().withLanguage(PointcutQueryLanguage.INSTANCE);
    }

    public static ElementPattern<PsiElement> startOfMemberModifiers() {
        return psiElement().afterLeafSkipping(
            or(
                psiElement(PointcutTypes.T_LEFT_PAREN),
                psiElement(PointcutTypes.PRIVATE),
                psiElement(PointcutTypes.PROTECTED),
                psiElement(PointcutTypes.PUBLIC),
                psiElement(PointcutTypes.FINAL),
                psiElement(PointcutTypes.T_ALTERNATION),
                psiElement().whitespace()
            ),
            or(
                psiElement(PointcutTypes.EXECUTION),
                psiElement(PointcutTypes.ACCESS)
            )
        );
    }

    /**
     * Checks if host is concrete annotation to inject syntax into it
     *
     * @param host Host element, typically string literal expression
     * @param annotationPrefix Annotation prefix
     * @return boolean
     */
    public static boolean isInsideAnnotation(@NotNull StringLiteralExpression host, String annotationPrefix) {

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

        return phpClass.getFQN().startsWith(annotationPrefix);

    }

    /**
     * Checks if host is PointcutBuilder->method('<pointcutExpression>', function () {...})
     * @param host Host element is typically PHP string
     *
     * @return boolean
     */
    public static boolean isInsidePointcutBuilderMethod(StringLiteralExpression host)
    {
        PsiElement element = host.getParent();
        if (!(element instanceof ParameterList)) {
            return false;
        }
        element = element.getParent();
        if (!(element instanceof MethodReference)) {
            return false;
        }

        PsiElement resolvedElement = ((MethodReference) element).resolve();
        if (!(resolvedElement instanceof MethodImpl)) {
            return false;
        }

        MethodImpl methodImpl  = (MethodImpl) resolvedElement;
        if (methodImpl.getFQN() == null) {
            return false;
        }

        return methodImpl.getFQN().startsWith("\\Go\\Aop\\Support\\PointcutBuilder");
    }
}
