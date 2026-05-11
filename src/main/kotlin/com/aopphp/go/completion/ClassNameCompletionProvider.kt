package com.aopphp.go.completion

import com.aopphp.go.psi.ClassFilter
import com.aopphp.go.psi.NamespacePattern
import com.aopphp.go.psi.PointcutTypes
import com.aopphp.go.util.PhpClassUtil
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionUtilCore
import com.intellij.codeInsight.completion.PlainPrefixMatcher
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex

/**
 * Provides PHP class/interface name completion inside classFilter of pointcut expressions.
 *
 * Fires for T_NAME_PART tokens inside a NamespacePattern that is a direct child of ClassFilter,
 * AND for the fallback case where the PSI is incomplete/invalid (see [CodePattern.insideClassFilter]).
 *
 * AOP proxy classes (implementing \Go\Aop\Proxy or with __AopProx in their FQN) are excluded.
 */
class ClassNameCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val originalPosition = parameters.originalPosition
        val position = originalPosition ?: parameters.position
        val project = position.project

        // --- Path 1: Valid PSI — NamespacePattern under ClassFilter exists ---
        val nsPattern = PsiTreeUtil.getParentOfType(position, NamespacePattern::class.java)
        if (nsPattern != null && nsPattern.parent is ClassFilter) {
            if (nsPattern.text.contains('*')) return
            val rawText = if (originalPosition != null) nsPattern.text else ""
            val prefix = rawText
                .removeSuffix(CompletionUtilCore.DUMMY_IDENTIFIER_TRIMMED)
                .trimStart('\\')
            val nsStartOffset = if (originalPosition != null) nsPattern.textRange.startOffset else -1
            addClasses(project, prefix, nsStartOffset, result)
            return
        }

        // --- Path 2: Fallback — PSI is incomplete, collect prefix from raw token stream ---
        // The pattern already filtered positions so we know we're after execution/access/within
        // (with optional modifiers). Collect the typed namespace text by walking backwards
        // through T_NAME_PART and T_NS_SEPARATOR tokens from originalPosition.
        val (prefix, nsStartOffset) = collectNamespacePrefix(originalPosition)
        addClasses(project, prefix, nsStartOffset, result)
    }

    /**
     * Walks backwards from [startElement] collecting T_NAME_PART and T_NS_SEPARATOR tokens.
     * Returns the concatenated prefix (without leading backslash) and the document start offset
     * of the first collected character (-1 when nothing collected).
     */
    private fun collectNamespacePrefix(startElement: PsiElement?): Pair<String, Int> {
        startElement ?: return Pair("", -1)
        val parts = mutableListOf<Pair<String, Int>>()
        var current: PsiElement? = startElement
        while (current != null) {
            val type = current.node?.elementType
            if (type == PointcutTypes.T_NAME_PART || type == PointcutTypes.T_NS_SEPARATOR) {
                parts.add(0, current.text to current.textRange.startOffset)
                current = PsiTreeUtil.prevLeaf(current)
            } else {
                break
            }
        }
        val text = parts.joinToString("") { it.first }.trimStart('\\')
        val startOffset = parts.firstOrNull()?.second ?: startElement.textRange.startOffset
        return Pair(text, startOffset)
    }

    private fun addClasses(project: com.intellij.openapi.project.Project, prefix: String, nsStartOffset: Int, result: CompletionResultSet) {
        val adjustedResult = result.withPrefixMatcher(prefix)
        // PHP index stores FQNs with a leading \; use PlainPrefixMatcher for the index query
        val indexPrefixMatcher = PlainPrefixMatcher("\\$prefix")
        val phpIndex = PhpIndex.getInstance(project)

        fun addElement(fqn: String?, psi: PsiElement) {
            val presentable = fqn?.trimStart('\\') ?: return
            adjustedResult.addElement(
                LookupElementBuilder.createWithSmartPointer(presentable, psi)
                    .withIcon(psi.getIcon(0))
                    .withInsertHandler { ctx, _ ->
                        if (nsStartOffset >= 0 && ctx.startOffset > nsStartOffset) {
                            ctx.document.deleteString(nsStartOffset, ctx.startOffset)
                            ctx.commitDocument()
                            ctx.editor.caretModel.moveToOffset(nsStartOffset + presentable.length)
                        }
                    }
            )
        }

        phpIndex.getAllClassFqns(indexPrefixMatcher).forEach { fqn ->
            phpIndex.getClassesByFQN(fqn)
                .filter { !PhpClassUtil.isAopProxy(it) }
                .forEach { cls -> addElement(cls.fqn, cls) }
        }

        phpIndex.getAllInterfacesFqns(indexPrefixMatcher).forEach { fqn ->
            phpIndex.getInterfacesByFQN(fqn)
                .filter { !PhpClassUtil.isAopProxy(it) }
                .forEach { iface -> addElement(iface.fqn, iface) }
        }
    }
}