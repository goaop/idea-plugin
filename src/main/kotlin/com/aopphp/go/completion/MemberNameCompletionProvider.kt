package com.aopphp.go.completion

import com.aopphp.go.pointcut.ModifierMatcherFilter
import com.aopphp.go.pointcut.PointcutCompiler
import com.aopphp.go.psi.AccessPointcut
import com.aopphp.go.psi.ExecutionPointcut
import com.aopphp.go.psi.MemberReference
import com.aopphp.go.psi.PointcutTypes
import com.aopphp.go.util.PhpClassUtil
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpModifier

/**
 * Provides PHP method/property completion for the member-name part of a pointcut expression.
 *
 * Handles two paths:
 *   Path 1 — Valid PSI: MemberReference exists; reads class, modifiers and access type directly.
 *   Path 2 — Fallback (incomplete PSI): walks backwards through the token stream to reconstruct
 *             the class name, visibility modifiers, and access operator.
 *
 * Filtering rules applied in both paths:
 *   - `$this->` positions are skipped (handled by SelfPointcutReferenceCompletionProvider).
 *   - Visibility modifiers in the pointcut (public/protected/private) restrict the members shown.
 *   - `->` shows only dynamic members; `::` shows only static members.
 *   - execution() lists methods (with `(*)` suffix appended on insert).
 *   - access()    lists only non-static, non-constant properties.
 */
class MemberNameCompletionProvider : CompletionProvider<CompletionParameters>() {

    private data class MemberContext(
        val classFqn: String,
        val isStatic: Boolean,
        val modifierFilter: ModifierMatcherFilter,  // no bits = any modifiers
        val isExecution: Boolean
    )

    private companion object {
        val MODIFIER_TOKENS = setOf(
            PointcutTypes.PUBLIC, PointcutTypes.PROTECTED, PointcutTypes.PRIVATE, PointcutTypes.FINAL,
            PointcutTypes.READONLY, PointcutTypes.PRIVATE_SET, PointcutTypes.PROTECTED_SET,
        )
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.originalPosition ?: parameters.position
        val project = position.project

        val ctx = resolveMemberContext(position) ?: return
        if (ctx.classFqn.contains('*')) return

        val phpIndex = PhpIndex.getInstance(project)
        val phpClass = PhpClassUtil.resolveNonProxyClass(ctx.classFqn, phpIndex) ?: return

        if (ctx.isExecution) {
            phpClass.methods
                .filter { method ->
                    method.name != "__construct" && method.isStatic == ctx.isStatic && ctx.modifierFilter.matches(method)
                }
                .forEach { method ->
                    result.addElement(
                        LookupElementBuilder.createWithSmartPointer(method.name, method)
                            .withIcon(method.getIcon(Iconable.ICON_FLAG_VISIBILITY))
                            .withTailText("(*)")
                            .withInsertHandler { insertCtx, _ ->
                                val tail = insertCtx.tailOffset
                                val text = insertCtx.document.charsSequence
                                if (tail >= text.length || text[tail] != '(') {
                                    insertCtx.document.insertString(tail, "(*)")
                                    insertCtx.editor.caretModel.moveToOffset(tail + 3)
                                }
                            }
                    )
                }
        } else {
            phpClass.fields
                .filter { field ->
                    !field.isConstant && field.modifier.isStatic == ctx.isStatic && ctx.modifierFilter.matches(field)
                }
                .forEach { field ->
                    result.addElement(
                        LookupElementBuilder.createWithSmartPointer(field.name, field)
                            .withIcon(field.getIcon(Iconable.ICON_FLAG_VISIBILITY))
                    )
                }
        }
    }

    // ------------------------------------------------------------------------------------------
    // Context resolution
    // ------------------------------------------------------------------------------------------

    /**
     * Resolves the [MemberContext] either from valid PSI (MemberReference) or from tokens.
     */
    private fun resolveMemberContext(position: PsiElement): MemberContext? {
        // Path 1: valid PSI
        val memberRef = PsiTreeUtil.getParentOfType(position, MemberReference::class.java)
        if (memberRef != null) {
            val isExecution = PsiTreeUtil.getParentOfType(memberRef, ExecutionPointcut::class.java) != null
            val isAccess    = PsiTreeUtil.getParentOfType(memberRef, AccessPointcut::class.java) != null
            if (!isExecution && !isAccess) return null

            val nsText = memberRef.classFilter.namespacePattern.text
            val accessType = memberRef.memberAccessType.memberAccessType  // DYNAMIC or STATIC
            return MemberContext(
                classFqn       = nsText,
                isStatic       = accessType == PhpModifier.State.STATIC,
                modifierFilter = PointcutCompiler.getModifierMatcher(memberRef.memberModifiers),
                isExecution    = isExecution
            )
        }

        // Path 2: broken PSI — walk the token stream
        return resolveMemberContextFromTokens(position)
    }

    /**
     * Token-level fallback. Walks backwards from [position] to reconstruct the class name,
     * access type, visibility modifiers, and enclosing keyword (execution/access).
     */
    private fun resolveMemberContextFromTokens(position: PsiElement): MemberContext? {
        // Previous token must be -> or ::
        val accessToken = prevNonWs(position) ?: return null
        val accessType = accessToken.node?.elementType
        if (accessType != PointcutTypes.T_OBJECT_ACCESS && accessType != PointcutTypes.T_STATIC_ACCESS) return null

        // Skip $this-> positions — handled by SelfPointcutReferenceCompletionProvider
        val beforeAccess = prevNonWs(accessToken)
        if (beforeAccess?.node?.elementType == PointcutTypes.T_THIS) return null

        // Collect class name tokens (T_NAME_PART + T_NS_SEPARATOR) walking backwards
        val classParts = mutableListOf<String>()
        var current = beforeAccess
        while (current != null) {
            val type = current.node?.elementType
            if (type == PointcutTypes.T_NAME_PART || type == PointcutTypes.T_NS_SEPARATOR) {
                classParts.add(0, current.text)
                current = PsiTreeUtil.prevLeaf(current)
            } else if (type == TokenType.WHITE_SPACE) {
                current = PsiTreeUtil.prevLeaf(current)
            } else {
                break
            }
        }

        val classText = classParts.joinToString("").trimStart('\\')
        if (classText.isEmpty()) return null

        // Collect modifier groups (walking backwards) and identify the enclosing keyword
        // Only '|' joins modifiers into one group of alternatives, whitespace is not significant
        val groups = mutableListOf(mutableListOf<String>())
        var keyword: IElementType? = null
        var joinWithNext = false

        while (current != null) {
            when (current.node?.elementType) {
                in MODIFIER_TOKENS           -> {
                    if (groups.last().isEmpty() || joinWithNext) groups.last().add(current.text)
                    else groups.add(mutableListOf(current.text))
                    joinWithNext = false
                }
                PointcutTypes.T_ALTERNATION  -> joinWithNext = true
                TokenType.WHITE_SPACE,
                PointcutTypes.T_LEFT_PAREN   -> { /* skip */ }
                PointcutTypes.EXECUTION      -> { keyword = PointcutTypes.EXECUTION; break }
                PointcutTypes.ACCESS         -> { keyword = PointcutTypes.ACCESS; break }
                else                         -> break
            }
            current = PsiTreeUtil.prevLeaf(current)
        }

        if (keyword == null) return null

        var modifierFilter = ModifierMatcherFilter()
        for (group in groups.filter { it.isNotEmpty() }) {
            val bits = group.fold(0) { mask, modifier -> mask or ModifierMatcherFilter.bitOf(modifier) }
            modifierFilter = if (group.size == 1) modifierFilter.andMatch(bits) else modifierFilter.orMatch(bits)
        }

        return MemberContext(
            classFqn       = classText,
            isStatic       = accessType == PointcutTypes.T_STATIC_ACCESS,
            modifierFilter = modifierFilter,
            isExecution    = keyword == PointcutTypes.EXECUTION
        )
    }

    /** Returns the previous non-whitespace leaf before [element]. */
    private fun prevNonWs(element: PsiElement): PsiElement? {
        var prev = PsiTreeUtil.prevLeaf(element)
        while (prev != null && prev.node?.elementType == TokenType.WHITE_SPACE) {
            prev = PsiTreeUtil.prevLeaf(prev)
        }
        return prev
    }
}
