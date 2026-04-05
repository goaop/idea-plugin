package com.aopphp.go.reference

import com.aopphp.go.pattern.CodePattern
import com.aopphp.go.psi.AccessPointcut
import com.aopphp.go.psi.ClassFilter
import com.aopphp.go.psi.ExecutionPointcut
import com.aopphp.go.psi.MemberReference
import com.aopphp.go.psi.NamePattern
import com.aopphp.go.psi.NamespaceName
import com.aopphp.go.psi.NamespacePattern
import com.aopphp.go.psi.PointcutElementFactory
import com.aopphp.go.psi.PointcutTypes
import com.aopphp.go.util.PhpClassUtil
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndex

/**
 * Makes PHP class names inside pointcuts clickable — they resolve to the PHP class.
 *
 * Covers two cases:
 *
 * 1. Annotation-style pointcuts @execution/@access/@within:
 *    References are attached to each individual T_NAME_PART leaf token inside the NamespaceName.
 *    This is required because IntelliJ's Ctrl+Click looks up references on the leaf element under
 *    the cursor; a reference on the composite NamespaceName parent is NOT found.
 *    All tokens in e.g. "Demo\Attribute\Cacheable" resolve to the same PHP class \Demo\Attribute\Cacheable,
 *    matching how PHP type-hint navigation works: clicking anywhere in a class name → go to class.
 *
 * 2. Regular classFilter patterns without wildcards in execution/within/access/initialization:
 *    e.g. execution(public Demo\Aspect\SomeClass->method(*))
 */
class PointcutClassReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        // Case 1: each T_NAME_PART leaf inside @execution/@access/@within class name.
        // All leaves share the same target: the full PHP class determined by NamespaceName.getFQN().
        registrar.registerReferenceProvider(
            CodePattern.insideAnnotationPointcut(),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement, context: ProcessingContext
                ): Array<PsiReference> {
                    val namespaceName = element.parent as? NamespaceName ?: return PsiReference.EMPTY_ARRAY
                    return arrayOf(PhpClassLeafReference(element, namespaceName::getFQN))
                }
            }
        )

        // Case 2: each T_NAME_PART leaf inside a concrete (wildcard-free) NamespacePattern → ClassFilter.
        // Leaf-level references are required for Find Usages: IntelliJ resolves references on leaf
        // tokens found by text search, so a reference on the composite NamespacePattern parent is ignored.
        // All leaves of e.g. "Demo\Aspect\SomeClass" resolve to the same PHP class \Demo\Aspect\SomeClass.
        registrar.registerReferenceProvider(
            psiElement(PointcutTypes.T_NAME_PART)
                .inside(psiElement(NamespacePattern::class.java).withParent(ClassFilter::class.java)),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement, context: ProcessingContext
                ): Array<PsiReference> {
                    val nsPattern = PsiTreeUtil.getParentOfType(element, NamespacePattern::class.java)
                        ?: return PsiReference.EMPTY_ARRAY
                    if (nsPattern.text.contains('*')) return PsiReference.EMPTY_ARRAY
                    return arrayOf(PhpClassLeafReference(element, nsPattern::getText))
                }
            }
        )

        // Case 3: T_NAME_PART inside NamePattern → MemberReference.
        // Resolves to a PHP method (in execution context) or dynamic property (in access context).
        // Only registered for concrete (wildcard-free) member names.
        registrar.registerReferenceProvider(
            psiElement(PointcutTypes.T_NAME_PART).withSuperParent(3, MemberReference::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement, context: ProcessingContext
                ): Array<PsiReference> {
                    val namePattern = element.parent?.parent as? NamePattern ?: return PsiReference.EMPTY_ARRAY
                    if (namePattern.text.contains('*')) return PsiReference.EMPTY_ARRAY
                    val memberRef = namePattern.parent as? MemberReference ?: return PsiReference.EMPTY_ARRAY
                    return arrayOf(PhpMemberReference(element, memberRef))
                }
            }
        )
    }

    /**
     * Reference attached to an individual T_NAME_PART leaf token inside a class-name container.
     * The [fqnProvider] lazily returns the FQN text of the enclosing container at resolution time.
     * Resolves to the PHP class identified by that FQN, so clicking any segment of
     * e.g. "Demo\Attribute\Cacheable" navigates to \Demo\Attribute\Cacheable.
     *
     * Used for two cases:
     * - NamespaceName inside @execution/@access/@within: fqnProvider = namespaceName::getFQN
     * - NamespacePattern inside a classFilter: fqnProvider = nsPattern::getText
     */
    private class PhpClassLeafReference(
        element: PsiElement,
        private val fqnProvider: () -> String?
    ) : PsiReferenceBase<PsiElement>(element, TextRange.from(0, element.textLength), true) {

        override fun resolve(): PsiElement? {
            val fqn = fqnProvider() ?: return null
            return PhpClassUtil.resolveNonProxyClass(fqn, PhpIndex.getInstance(element.project))
        }

        override fun handleElementRename(newElementName: String): PsiElement {
            val newNamePart = PointcutElementFactory.createNamePart(element.project, newElementName)
                ?: throw IncorrectOperationException("Cannot create name part for: $newElementName")
            return element.replace(newNamePart)
        }

        override fun getVariants(): Array<Any> = emptyArray()
    }

    /**
     * Reference for a T_NAME_PART token inside the member NamePattern of a MemberReference.
     * Resolves to the PHP method (execution context) or dynamic property (access context).
     */
    private class PhpMemberReference(
        element: PsiElement,
        private val memberRef: MemberReference
    ) : PsiReferenceBase<PsiElement>(element, TextRange.from(0, element.textLength), true) {

        override fun resolve(): PsiElement? {
            val memberName = element.text
            val nsPatternText = memberRef.classFilter.namespacePattern.text
            if (nsPatternText.contains('*')) return null

            val phpIndex = PhpIndex.getInstance(element.project)
            val phpClass = PhpClassUtil.resolveNonProxyClass(nsPatternText, phpIndex) ?: return null

            val isExecution = PsiTreeUtil.getParentOfType(memberRef, ExecutionPointcut::class.java) != null
            if (isExecution) {
                return phpClass.findMethodByName(memberName)
                    ?.takeIf { it.containingClass?.let { c -> !PhpClassUtil.isAopProxy(c) } != false }
            }

            val isAccess = PsiTreeUtil.getParentOfType(memberRef, AccessPointcut::class.java) != null
            if (isAccess) {
                return phpClass.findFieldByName(memberName, true)
                    ?.takeIf { it.containingClass?.let { c -> !PhpClassUtil.isAopProxy(c) } != false }
            }

            return null
        }

        override fun handleElementRename(newElementName: String): PsiElement {
            val newNamePart = PointcutElementFactory.createNamePart(element.project, newElementName)
                ?: throw IncorrectOperationException("Cannot create name part for: $newElementName")
            return element.replace(newNamePart)
        }

        override fun getVariants(): Array<Any> = emptyArray()
    }
}
