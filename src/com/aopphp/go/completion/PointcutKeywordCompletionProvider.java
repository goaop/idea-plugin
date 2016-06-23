package com.aopphp.go.completion;

import com.aopphp.go.completion.insert.PointcutKeywordInsertHandler;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class PointcutKeywordCompletionProvider extends CompletionProvider<CompletionParameters>
{
    private static final String[] POINTCUT_TYPES = new String[]{
        "execution",
        "access",
        "within",
        "initialization",
        "staticinitialization",
        "matchInherited",
    };

    private static final PointcutKeywordInsertHandler keywordInsertionHandler = PointcutKeywordInsertHandler.getInstance();

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

        for (String keyword : POINTCUT_TYPES) {
            LookupElementBuilder lookupElement = LookupElementBuilder.create(keyword);
            lookupElement = lookupElement.withBoldness(true).withInsertHandler(keywordInsertionHandler);
            result.addElement(lookupElement);
        }
    }
}
