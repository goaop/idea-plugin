package com.aopphp.go.completion.insert

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.jetbrains.php.completion.insert.PhpInsertHandlerUtil
import com.jetbrains.php.completion.insert.PhpReferenceInsertHandler

/** Annotation-style keywords that accept a PHP Attribute class name as their argument. */
private val ANNOTATION_KEYWORDS = setOf("execution", "access", "within")

object PointcutKeywordInsertHandler : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        PhpReferenceInsertHandler.getInstance().handleInsert(context, item)
        if (!PhpInsertHandlerUtil.isStringAtCaret(context.editor, "(")) {
            PhpInsertHandlerUtil.insertStringAtCaret(context.editor, "()")
            context.editor.caretModel.moveCaretRelatively(-1, 0, false, false, true)
        }
        // For annotation-style pointcuts (@execution, @access, @within), trigger attribute class
        // completion automatically so the user sees the list of valid attributes right away.
        val keyword = item.lookupString
        if (keyword in ANNOTATION_KEYWORDS) {
            val chars = context.document.charsSequence
            val atSignOffset = context.startOffset - 1
            if (atSignOffset >= 0 && chars[atSignOffset] == '@') {
                AutoPopupController.getInstance(context.project).scheduleAutoPopup(context.editor)
            }
        }
    }
}
