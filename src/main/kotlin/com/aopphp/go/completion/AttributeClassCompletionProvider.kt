package com.aopphp.go.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.stubs.indexes.PhpAttributesFQNsIndex

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

        StubIndex.getInstance().processElements(
            PhpAttributesFQNsIndex.KEY,
            "\\Attribute",
            project,
            scope,
            PhpClass::class.java
        ) { phpClass ->
            val fqn = phpClass.presentableFQN ?: return@processElements true
            result.addElement(
                LookupElementBuilder.createWithSmartPointer(fqn, phpClass)
                    .withIcon(phpClass.getIcon(0))
            )
            true
        }
    }
}
