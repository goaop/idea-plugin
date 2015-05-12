package com.aopphp.go;

import com.aopphp.go.completion.DoctrineAnnotationCompletionProvider;
import com.aopphp.go.pattern.CodePattern;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;

public class PointcutCompletionContributor extends CompletionContributor {
    public PointcutCompletionContributor() {
        extend(
            CompletionType.BASIC,
            CodePattern.getNamePartAfterLpAndAnnotation(),
            new DoctrineAnnotationCompletionProvider()
        );
    }
}
