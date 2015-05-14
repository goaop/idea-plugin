package com.aopphp.go.pattern;

import com.aopphp.go.PointcutQueryLanguage;
import com.aopphp.go.psi.AnnotatedAccessPointcut;
import com.aopphp.go.psi.AnnotatedExecutionPointcut;
import com.aopphp.go.psi.AnnotatedWithinPointcut;
import com.aopphp.go.psi.PointcutTypes;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;

/**
 * Code pattern contains some useful patterns for matching items
 */
public class CodePattern {

    /**
     * Matching name part after left parenthesis and one of annotation pointcut type
     *  \@execution(<className>)
     *  \@access(<className>)
     *  \@within(<className>)
     */
    public static ElementPattern<PsiElement> insideAnnotationPointcut() {
        return PlatformPatterns.or(
                PlatformPatterns.psiElement(PointcutTypes.NAMEPART).withSuperParent(2, AnnotatedExecutionPointcut.class),
                PlatformPatterns.psiElement(PointcutTypes.NAMEPART).withSuperParent(2, AnnotatedAccessPointcut.class),
                PlatformPatterns.psiElement(PointcutTypes.NAMEPART).withSuperParent(2, AnnotatedWithinPointcut.class)
        );
    }

    public static ElementPattern<PsiElement> insidePointcutLanguage() {
        return PlatformPatterns.psiElement().withLanguage(PointcutQueryLanguage.INSTANCE);
    }
}
