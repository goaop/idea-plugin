package com.aopphp.go.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext

class MemberModifierCompletionProvider : CompletionProvider<CompletionParameters>() {

    private val modifiers = listOf("public", "protected", "private", "final")

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (parameters.originalPosition == null) return

        for (modifier in modifiers) {
            result.addElement(LookupElementBuilder.create(modifier).withBoldness(true))
        }
    }
}
