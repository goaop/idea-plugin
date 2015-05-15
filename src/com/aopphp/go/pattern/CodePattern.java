package com.aopphp.go.pattern;

import com.aopphp.go.PointcutQueryLanguage;
import com.aopphp.go.psi.*;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;

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
            psiElement(PointcutTypes.NAMEPART).withSuperParent(2, AnnotatedExecutionPointcut.class),
            psiElement(PointcutTypes.NAMEPART).withSuperParent(2, AnnotatedAccessPointcut.class),
            psiElement(PointcutTypes.NAMEPART).withSuperParent(2, AnnotatedWithinPointcut.class)
        );
    }

    public static ElementPattern<PsiElement> insidePointcutLanguage() {
        return psiElement().withLanguage(PointcutQueryLanguage.INSTANCE);
    }

    public static ElementPattern<PsiElement> startOfMemberModifiers() {
        return psiElement().afterLeafSkipping(
            or(
                psiElement(PointcutTypes.LP),
                psiElement(PointcutTypes.PRIVATE),
                psiElement(PointcutTypes.PROTECTED),
                psiElement(PointcutTypes.PUBLIC),
                psiElement(PointcutTypes.FINAL),
                psiElement(PointcutTypes.ALTERNATION),
                psiElement().whitespace()
            ),
            or(
                psiElement(PointcutTypes.EXECUTION),
                psiElement(PointcutTypes.ACCESS)
            )
        );
    }
}
