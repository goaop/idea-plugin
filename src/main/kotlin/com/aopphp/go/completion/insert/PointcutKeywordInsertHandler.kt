package com.aopphp.go.completion.insert

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.completion.insert.PhpInsertHandlerUtil
import com.jetbrains.php.completion.insert.PhpReferenceInsertHandler

object PointcutKeywordInsertHandler : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        PhpReferenceInsertHandler.getInstance().handleInsert(context, item)
        if (!PhpInsertHandlerUtil.isStringAtCaret(context.editor, "(")) {
            PhpInsertHandlerUtil.insertStringAtCaret(context.editor, "()")
            context.editor.caretModel.moveCaretRelatively(-1, 0, false, false, true)
        }
    }
}
