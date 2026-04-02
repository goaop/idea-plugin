package com.aopphp.go.completion.insert

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

/**
 * Insert handler for the `$this` pointcut-reference keyword.
 *
 * After inserting `$this`, it appends `->` immediately and schedules an auto-popup
 * so the user immediately sees the list of `#[\Go\Lang\Attribute\Pointcut]`-annotated
 * members to choose from.
 */
object SelfPointcutInsertHandler : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val editor = context.editor
        val document = context.document
        val tail = context.tailOffset

        if (!document.text.substring(tail).startsWith("->")) {
            document.insertString(tail, "->")
        }
        editor.caretModel.moveToOffset(tail + 2)
        AutoPopupController.getInstance(context.project).scheduleAutoPopup(editor)
    }
}
