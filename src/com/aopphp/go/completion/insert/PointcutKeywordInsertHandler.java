package com.aopphp.go.completion.insert;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtilCore;
import com.jetbrains.php.completion.insert.PhpInsertHandlerUtil;
import com.jetbrains.php.completion.insert.PhpReferenceInsertHandler;
import org.jetbrains.annotations.NotNull;

public class PointcutKeywordInsertHandler implements InsertHandler<LookupElement> {

    private static final PointcutKeywordInsertHandler INSTANCE = new PointcutKeywordInsertHandler();

    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {

        // reuse jetbrains "use importer": this is private only so we need some workaround
        // to not implement your own algo for that
        PhpReferenceInsertHandler.getInstance().handleInsert(context, lookupElement);
        if(!PhpInsertHandlerUtil.isStringAtCaret(context.getEditor(), "(")) {
            PhpInsertHandlerUtil.insertStringAtCaret(context.getEditor(), "()");
            context.getEditor().getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
        }
    }

    public static PointcutKeywordInsertHandler getInstance(){
        return INSTANCE;
    }
}