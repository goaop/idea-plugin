package com.aopphp.go.completion

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
        val allowedAccess: Set<PhpModifier.Access>,  // empty = any visibility
        val requireFinal: Boolean,
        val isExecution: Boolean
    )

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
                    if (method.name == "__construct") return@filter false
                    if (ctx.isStatic && !method.isStatic) return@filter false
                    if (!ctx.isStatic && method.isStatic) return@filter false
                    if (ctx.requireFinal && !method.modifier.isFinal) return@filter false
                    if (ctx.allowedAccess.isNotEmpty() && method.modifier.access !in ctx.allowedAccess) return@filter false
                    true
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
                    if (field.isConstant) return@filter false
                    if (ctx.isStatic && !field.modifier.isStatic) return@filter false
                    if (!ctx.isStatic && field.modifier.isStatic) return@filter false
                    if (ctx.requireFinal) return@filter false  // PHP fields are never final in class sense
                    if (ctx.allowedAccess.isNotEmpty() && field.modifier.access !in ctx.allowedAccess) return@filter false
                    true
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
            val (allowedAccess, requireFinal) = parseModifiers(memberRef)

            return MemberContext(
                classFqn    = nsText,
                isStatic    = accessType == PhpModifier.State.STATIC,
                allowedAccess = allowedAccess,
                requireFinal  = requireFinal,
                isExecution   = isExecution
            )
        }

        // Path 2: broken PSI — walk the token stream
        return resolveMemberContextFromTokens(position)
    }

    /**
     * Extracts allowed visibility modifiers and 'final' flag from a [MemberReference].
     */
    private fun parseModifiers(memberRef: MemberReference): Pair<Set<PhpModifier.Access>, Boolean> {
        val allowedAccess = mutableSetOf<PhpModifier.Access>()
        var requireFinal = false
        memberRef.memberModifiers.memberModifierList.forEach { modifier ->
            when (modifier.node.firstChildNode?.elementType) {
                PointcutTypes.PUBLIC    -> allowedAccess.add(PhpModifier.Access.PUBLIC)
                PointcutTypes.PROTECTED -> allowedAccess.add(PhpModifier.Access.PROTECTED)
                PointcutTypes.PRIVATE   -> allowedAccess.add(PhpModifier.Access.PRIVATE)
                PointcutTypes.FINAL     -> requireFinal = true
            }
        }
        return Pair(allowedAccess, requireFinal)
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

        // Collect modifiers and identify the enclosing keyword
        val allowedAccess = mutableSetOf<PhpModifier.Access>()
        var requireFinal = false
        var keyword: IElementType? = null

        while (current != null) {
            when (current.node?.elementType) {
                PointcutTypes.PUBLIC         -> allowedAccess.add(PhpModifier.Access.PUBLIC)
                PointcutTypes.PROTECTED      -> allowedAccess.add(PhpModifier.Access.PROTECTED)
                PointcutTypes.PRIVATE        -> allowedAccess.add(PhpModifier.Access.PRIVATE)
                PointcutTypes.FINAL          -> requireFinal = true
                PointcutTypes.T_ALTERNATION,
                PointcutTypes.T_LEFT_PAREN,
                TokenType.WHITE_SPACE        -> { /* skip */ }
                PointcutTypes.EXECUTION      -> { keyword = PointcutTypes.EXECUTION; break }
                PointcutTypes.ACCESS         -> { keyword = PointcutTypes.ACCESS; break }
                else                         -> break
            }
            current = PsiTreeUtil.prevLeaf(current)
        }

        if (keyword == null) return null

        return MemberContext(
            classFqn      = classText,
            isStatic      = accessType == PointcutTypes.T_STATIC_ACCESS,
            allowedAccess = allowedAccess,
            requireFinal  = requireFinal,
            isExecution   = keyword == PointcutTypes.EXECUTION
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
