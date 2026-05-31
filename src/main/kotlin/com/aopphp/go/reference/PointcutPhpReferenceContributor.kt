package com.aopphp.go.reference

import com.aopphp.go.pattern.CodePattern
import com.aopphp.go.psi.*
import com.aopphp.go.psiutil.PointcutElementFactory
import com.aopphp.go.util.PhpClassUtil
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * Adds PsiReferences to PHP [StringLiteralExpression] elements that host
 * Go! AOP pointcut expressions.  References point back to the PHP classes,
 * methods, and properties named in the pointcut, enabling **Find Usages**
 * and **Rename refactoring** at the PHP level.
 *
 * Registered for `language="PHP"` so the references are visible to PHP's
 * native Find Usages infrastructure, which does not search injected content.
 *
 * Covers three cases:
 *
 * 1. `@execution(Attr\Class)` / `@access(…)` / `@within(…)` →
 *    reference to the attribute class
 * 2. `execution(public Ns\Class->…)` / `within(Ns\Class)` / etc. →
 *    reference to the class (wildcard patterns are skipped)
 * 3. `execution(public Ns\Class->method(*))` / `access(… Ns\Class->prop)` →
 *    reference to the method or property
 */
class PointcutPhpReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java),
            PointcutStringReferenceProvider()
        )
    }

    // ─── reference provider ────────────────────────────────────────────

    private class PointcutStringReferenceProvider : PsiReferenceProvider() {

        override fun getReferencesByElement(
            element: PsiElement, context: ProcessingContext
        ): Array<PsiReference> {
            val host = element as? StringLiteralExpression ?: return PsiReference.EMPTY_ARRAY
            return getReferencesForHost(host)
        }
    }

    // ─── PSI walker ────────────────────────────────────────────────────

    companion object {

        private const val GO_AOP_ATTR_PREFIX = "\\Go\\Lang\\Attribute"

        /**
         * Creates references for a [StringLiteralExpression] that hosts a Go! AOP pointcut.
         * Called both by the registered [PsiReferenceProvider] and by [PointcutReferencesSearcher].
         */
        @JvmStatic
        fun getReferencesForHost(host: StringLiteralExpression): Array<PsiReference> {
            if (!CodePattern.isInsidePhpAttribute(host, GO_AOP_ATTR_PREFIX)
                && !CodePattern.isInsidePointcutBuilderMethod(host)
            ) {
                return PsiReference.EMPTY_ARRAY
            }

            val content = host.contents
            if (content.isBlank()) return PsiReference.EMPTY_ARRAY

            val pointcutExpr = PointcutElementFactory.createPointcut(host.project, content)
                ?: return PsiReference.EMPTY_ARRAY

            val quoteOffset = host.textLength - host.contents.length - 1
            val contentOffset = if (quoteOffset > 0) quoteOffset else 1

            val refs = mutableListOf<PsiReference>()
            collectReferences(pointcutExpr, host, contentOffset, refs)
            return refs.toTypedArray()
        }

        private fun collectReferences(
            root: PointcutExpression,
            host: StringLiteralExpression,
            contentOffset: Int,
            out: MutableList<PsiReference>
        ) {
            // Case 1: annotation pointcuts — NamespaceName is the attribute class
            for (nsName in PsiTreeUtil.collectElementsOfType(root, NamespaceName::class.java)) {
                // Skip NamespaceName inside pointcutReference ($this->memberName or Ns\Class->method)
                if (PsiTreeUtil.getParentOfType(nsName, PointcutReference::class.java) != null) continue

                val range = rangeOf(nsName, contentOffset)
                out.add(PhpClassHostReference(host, range, nsName.getFQN()))
            }

            // Case 2: classFilter — NamespacePattern is the class pattern
            for (classFilter in PsiTreeUtil.collectElementsOfType(root, ClassFilter::class.java)) {
                val nsPattern = classFilter.namespacePattern
                if (nsPattern.text.contains('*')) continue

                val range = rangeOf(nsPattern, contentOffset)
                out.add(PhpClassHostReference(host, range, nsPattern.text))
            }

            // Case 3: member reference — NamePattern is the method/property name
            for (memberRef in PsiTreeUtil.collectElementsOfType(root, MemberReference::class.java)) {
                val namePattern = memberRef.namePattern
                if (namePattern.text.contains('*')) continue

                val classText = memberRef.classFilter.namespacePattern.text
                if (classText.contains('*')) continue

                val isExecution =
                    PsiTreeUtil.getParentOfType(memberRef, ExecutionPointcut::class.java) != null
                val isAccess =
                    PsiTreeUtil.getParentOfType(memberRef, AccessPointcut::class.java) != null

                val range = rangeOf(namePattern, contentOffset)
                out.add(PhpMemberHostReference(host, range, classText, namePattern.text, isExecution, isAccess))
            }
        }

        /** Calculates TextRange inside the host StringLiteralExpression for a parsed PSI node. */
        private fun rangeOf(node: PsiElement, contentOffset: Int): TextRange {
            val start = contentOffset + node.textRange.startOffset
            return TextRange(start, start + node.textLength)
        }
    }

    // ─── reference classes ─────────────────────────────────────────────

    /**
     * Reference from a substring of a PHP string literal to a PHP class.
     * The [rangeInElement] covers the class-name portion (e.g. `Demo\Attribute\Cacheable`).
     *
     * [handleElementRename] replaces only the **last** namespace segment so that
     * `Demo\Attribute\Cacheable` → `Demo\Attribute\NewName` on class rename.
     */
    private class PhpClassHostReference(
        element: StringLiteralExpression,
        rangeInElement: TextRange,
        private val classFqn: String
    ) : PsiReferenceBase<StringLiteralExpression>(element, rangeInElement, true) {

        override fun resolve(): PsiElement? =
            PhpClassUtil.resolveNonProxyClass(classFqn, PhpIndex.getInstance(element.project))

        override fun handleElementRename(newElementName: String): PsiElement {
            val currentText = rangeInElement.substring(element.text)
            val lastSep = currentText.lastIndexOf('\\')
            val newText = if (lastSep >= 0) {
                currentText.substring(0, lastSep + 1) + newElementName
            } else {
                newElementName
            }
            return handleContentChange(newText)
        }

        override fun getVariants(): Array<Any> = emptyArray()

        private fun handleContentChange(newText: String): PsiElement {
            val manipulator = ElementManipulators.getManipulator(element)
                ?: throw com.intellij.util.IncorrectOperationException(
                    "No manipulator for ${element.javaClass.name}"
                )
            return manipulator.handleContentChange(element, rangeInElement, newText)
                ?: throw com.intellij.util.IncorrectOperationException(
                    "handleContentChange returned null for ${element.javaClass.name}"
                )
        }
    }

    /**
     * Reference from a substring of a PHP string literal to a PHP method or property.
     * The [rangeInElement] covers the member-name portion (e.g. `doSomething`).
     */
    private class PhpMemberHostReference(
        element: StringLiteralExpression,
        rangeInElement: TextRange,
        private val classFqn: String,
        private val memberName: String,
        private val isExecution: Boolean,
        private val isAccess: Boolean
    ) : PsiReferenceBase<StringLiteralExpression>(element, rangeInElement, true) {

        override fun resolve(): PsiElement? {
            val phpIndex = PhpIndex.getInstance(element.project)
            val phpClass = PhpClassUtil.resolveNonProxyClass(classFqn, phpIndex) ?: return null

            if (isExecution) {
                return phpClass.findMethodByName(memberName)
                    ?.takeIf { it.containingClass?.let { c -> !PhpClassUtil.isAopProxy(c) } != false }
            }

            if (isAccess) {
                return phpClass.findFieldByName(memberName, true)
                    ?.takeIf { it.containingClass?.let { c -> !PhpClassUtil.isAopProxy(c) } != false }
            }

            return null
        }

        override fun handleElementRename(newElementName: String): PsiElement {
            // Strip $ prefix if present (PHP field names include $, pointcut syntax doesn't)
            val cleanName = newElementName.removePrefix("$")
            val manipulator = ElementManipulators.getManipulator(element)
                ?: throw com.intellij.util.IncorrectOperationException(
                    "No manipulator for ${element.javaClass.name}"
                )
            return manipulator.handleContentChange(element, rangeInElement, cleanName)
                ?: throw com.intellij.util.IncorrectOperationException(
                    "handleContentChange returned null for ${element.javaClass.name}"
                )
        }

        override fun getVariants(): Array<Any> = emptyArray()
    }
}
