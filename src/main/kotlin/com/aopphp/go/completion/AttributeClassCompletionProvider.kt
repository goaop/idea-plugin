package com.aopphp.go.completion

import com.aopphp.go.index.AttributePhpNamedElementIndex
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.PhpIndex

/**
 * Provides completion for PHP 8 Attribute class names inside annotation pointcuts
 * (@execution, @access, @within). Suggests classes decorated with #[\Attribute].
 */
class AttributeClassCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.originalPosition ?: return
        val project = position.project
        val scope = GlobalSearchScope.allScope(project)
        val phpIndex = PhpIndex.getInstance(project)

        // Query our index for all elements annotated with \Attribute — those are PHP 8 attribute classes
        val fqns = FileBasedIndex.getInstance()
            .getValues(AttributePhpNamedElementIndex.KEY, "\\Attribute", scope)
            .flatten()

        for (fqn in fqns) {
            for (phpClass in phpIndex.getClassesByFQN(fqn)) {
                val presentable = phpClass.presentableFQN ?: continue
                result.addElement(
                    LookupElementBuilder.createWithSmartPointer(presentable, phpClass)
                        .withIcon(phpClass.getIcon(0))
                )
            }
        }
    }
}
