package com.aopphp.go.completion

import com.aopphp.go.completion.insert.PointcutKeywordInsertHandler
import com.aopphp.go.completion.insert.SelfPointcutInsertHandler
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext

class PointcutKeywordCompletionProvider : CompletionProvider<CompletionParameters>() {

    private val pointcutTypes = listOf(
        "execution", "access", "within", "initialization", "staticinitialization", "matchInherited"
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (parameters.originalPosition == null) return

        for (keyword in pointcutTypes) {
            result.addElement(
                LookupElementBuilder.create(keyword)
                    .withBoldness(true)
                    .withInsertHandler(PointcutKeywordInsertHandler)
            )
        }

        // Suggest $this-> as a self-reference to pointcut-annotated members of the aspect class
        result.addElement(
            LookupElementBuilder.create("\$this")
                .withBoldness(true)
                .withTypeText("self pointcut reference")
                .withInsertHandler(SelfPointcutInsertHandler)
        )
    }
}
