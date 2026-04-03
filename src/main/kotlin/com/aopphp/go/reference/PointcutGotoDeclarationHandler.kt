package com.aopphp.go.reference

import com.aopphp.go.pattern.CodePattern
import com.aopphp.go.psi.AccessPointcut
import com.aopphp.go.psi.ClassFilter
import com.aopphp.go.psi.ExecutionPointcut
import com.aopphp.go.psi.MemberReference
import com.aopphp.go.psi.NamePattern
import com.aopphp.go.psi.NamespacePattern
import com.aopphp.go.psi.PointcutTypes
import com.aopphp.go.util.PhpClassUtil
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.lang.ASTNode
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex

/**
 * Handles Ctrl+Click navigation from class name tokens inside Go! AOP pointcut expressions
 * injected into PHP strings.
 *
 * GotoDeclarationHandler is injection-aware: IntelliJ calls it with the actual injected element
 * (not the PHP host), making it the reliable mechanism for cross-PSI-tree navigation.
 *
 * For a FQDN like `Demo\Attribute\Cacheable`:
 * - Intermediate parts (`Demo`, `Attribute`) navigate to the PHP namespace at that depth.
 * - The last part (`Cacheable`) navigates to the PHP class/interface/trait.
 */
class PointcutGotoDeclarationHandler : GotoDeclarationHandler {

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor
    ): Array<PsiElement>? {
        sourceElement ?: return null
        if (sourceElement.node?.elementType != PointcutTypes.T_NAME_PART) return null

        // Case 1: T_NAME_PART inside @execution/@access/@within annotation pointcut
        if (CodePattern.insideAnnotationPointcut().accepts(sourceElement)) {
            val namespaceName = sourceElement.parent ?: return null
            return resolveByPosition(sourceElement, namespaceName)
        }

        // Case 2: T_NAME_PART inside a concrete (wildcard-free) classFilter NamespacePattern
        val nsPattern = PsiTreeUtil.getParentOfType(sourceElement, NamespacePattern::class.java)
        if (nsPattern != null && !nsPattern.text.contains('*') &&
            PsiTreeUtil.getParentOfType(nsPattern, ClassFilter::class.java) != null
        ) {
            return resolveByPosition(sourceElement, nsPattern)
        }

        // Case 3: T_NAME_PART inside the member NamePattern of a MemberReference.
        // Navigates to the PHP method (execution) or dynamic property (access) by name.
        val namePattern = PsiTreeUtil.getParentOfType(sourceElement, NamePattern::class.java)
        if (namePattern != null) {
            val memberRef = namePattern.parent as? MemberReference ?: return null
            val memberName = namePattern.text
            if (memberName.contains('*')) return null   // wildcard — no single target

            // Resolve the containing class
            val nsPatternText = memberRef.classFilter.namespacePattern.text
            if (nsPatternText.contains('*')) return null
            val fqn = if (nsPatternText.startsWith('\\')) nsPatternText else "\\$nsPatternText"

            val phpIndex = PhpIndex.getInstance(sourceElement.project)
            val phpClass = PhpClassUtil.resolveNonProxyClass(fqn, phpIndex) ?: return null

            val isExecution = PsiTreeUtil.getParentOfType(memberRef, ExecutionPointcut::class.java) != null
            if (isExecution) {
                val method = phpClass.findMethodByName(memberName)
                    ?.takeIf { it.containingClass?.let { c -> !PhpClassUtil.isAopProxy(c) } != false }
                return if (method != null) arrayOf(method) else null
            }

            val isAccess = PsiTreeUtil.getParentOfType(memberRef, AccessPointcut::class.java) != null
            if (isAccess) {
                val field = phpClass.findFieldByName(memberName, true)
                    ?.takeIf { it.containingClass?.let { c -> !PhpClassUtil.isAopProxy(c) } != false }
                return if (field != null) arrayOf(field) else null
            }
        }

        return null
    }

    /**
     * Generic position-aware resolution for a PSI container whose subtree contains T_NAME_PART tokens.
     * - Last part → PHP class/interface/trait.
     * - Any earlier part → PHP namespace at that depth.
     *
     * Works for both:
     * - NamespaceName  (@execution/@access/@within): T_NAME_PART are DIRECT children
     * - NamespacePattern (within/execution classFilter): T_NAME_PART are 3 levels deep
     *   (NamespacePattern → NamespacePatternPart → NamePatternPart → T_NAME_PART)
     */
    private fun resolveByPosition(element: PsiElement, container: PsiElement): Array<PsiElement>? {
        // Collect all T_NAME_PART leaf tokens in the subtree in document order
        val parts = mutableListOf<PsiElement>()
        collectNameParts(container.node, parts)

        val clickedIndex = parts.indexOf(element)
        if (clickedIndex < 0) return null

        val partialFqn = "\\" + parts.take(clickedIndex + 1).joinToString("\\") { it.text }

        return if (clickedIndex == parts.size - 1) {
            resolveClass(partialFqn, element)
        } else {
            val targets: List<PsiElement> =
                PhpIndex.getInstance(element.project).getNamespacesByName(partialFqn).toList()
            targets.toTypedArray().takeIf { it.isNotEmpty() }
        }
    }

    /** Recursively collects all T_NAME_PART AST leaf nodes under [node], in document order. */
    private fun collectNameParts(node: ASTNode, result: MutableList<PsiElement>) {
        if (node.elementType == PointcutTypes.T_NAME_PART) {
            result.add(node.psi)
            return
        }
        var child = node.firstChildNode
        while (child != null) {
            collectNameParts(child, result)
            child = child.treeNext
        }
    }

    private fun resolveClass(fqn: String, context: PsiElement): Array<PsiElement>? {
        val result = PhpClassUtil.resolveNonProxyClass(fqn, PhpIndex.getInstance(context.project))
        return if (result != null) arrayOf(result) else null
    }
}
