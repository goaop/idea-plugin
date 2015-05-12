package com.aopphp.go.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.annotation.util.AnnotationUtil;
import org.jetbrains.annotations.NotNull;

public class DoctrineAnnotationCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(
            @NotNull CompletionParameters parameters,
            ProcessingContext context,
            @NotNull CompletionResultSet result)
    {
        PsiElement psiElement = parameters.getOriginalPosition();
        if (psiElement == null) {
            return;
        }

        PhpClass[] annotationsClasses = AnnotationUtil.getAnnotationsClasses(psiElement.getProject());

        for (PhpClass annotationsClass : annotationsClasses) {
            String annotationClassName = annotationsClass.getPresentableFQN();
            LookupElementBuilder lookupElement = LookupElementBuilder.createWithSmartPointer(
                    annotationClassName,
                    annotationsClass
            );
            lookupElement = lookupElement.withIcon(annotationsClass.getIcon());
            result.addElement(lookupElement);
        }
    }
}
