package com.aopphp.go.completion

import com.aopphp.go.index.AttributePhpNamedElementIndex
import com.aopphp.go.psi.NamespaceName
import com.aopphp.go.util.AttributeTargetUtil
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
 * (@execution, @access, @within). Only suggests attribute classes whose target mask
 * is compatible with the enclosing pointcut type:
 *   @execution → TARGET_METHOD or TARGET_FUNCTION
 *   @access    → TARGET_PROPERTY
 *   @within    → TARGET_CLASS
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

        // T_NAME_PART → NamespaceName → AnnotatedXxxPointcut
        val namespaceName = position.parent as? NamespaceName
        val pointcutParent = namespaceName?.parent
        val requiredBits = AttributeTargetUtil.requiredBitsFor(pointcutParent)

        // When the user has already typed a namespace path (e.g. "Demo\Attribute\L"),
        // override the prefix matcher to use the full path so that IntelliJ filters
        // completions by the full FQN rather than just the last token.
        val existingText = namespaceName?.text ?: ""
        val adjustedResult = if (existingText.contains('\\')) {
            result.withPrefixMatcher(existingText)
        } else {
            result
        }

        // Start offset of the NamespaceName in the injected document, used to
        // remove the namespace prefix that IntelliJ leaves behind on insertion.
        val nsStartOffset = namespaceName?.textRange?.startOffset ?: -1

        val fqns = FileBasedIndex.getInstance()
            .getValues(AttributePhpNamedElementIndex.KEY, "\\Attribute", scope)
            .flatten()

        for (fqn in fqns) {
            for (phpClass in phpIndex.getClassesByFQN(fqn)) {
                if (!AttributeTargetUtil.isCompatible(phpClass, requiredBits)) continue
                val presentable = phpClass.presentableFQN ?: continue
                adjustedResult.addElement(
                    LookupElementBuilder.createWithSmartPointer(presentable, phpClass)
                        .withIcon(phpClass.getIcon(0))
                        .withInsertHandler { ctx, _ ->
                            // IntelliJ replaces only the last token (e.g. "L") with the full FQN,
                            // leaving the existing namespace prefix ("Demo\Attribute\") in place.
                            // Delete it so we end up with exactly the selected FQN.
                            if (nsStartOffset >= 0 && ctx.startOffset > nsStartOffset) {
                                ctx.document.deleteString(nsStartOffset, ctx.startOffset)
                                ctx.commitDocument()
                                ctx.editor.caretModel.moveToOffset(nsStartOffset + presentable.length)
                            }
                        }
                )
            }
        }
    }
}
