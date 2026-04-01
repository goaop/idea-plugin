package com.aopphp.go.reference

import com.aopphp.go.pattern.CodePattern
import com.aopphp.go.psi.ClassFilter
import com.aopphp.go.psi.NamespaceName
import com.aopphp.go.psi.NamespacePattern
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
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
                    return arrayOf(PhpClassLeafReference(element, namespaceName))
                }
            }
        )

        // Case 2: NamespacePattern inside classFilter — only for concrete (wildcard-free) names.
        registrar.registerReferenceProvider(
            psiElement(NamespacePattern::class.java).withParent(psiElement(ClassFilter::class.java)),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement, context: ProcessingContext
                ): Array<PsiReference> {
                    if (element !is NamespacePattern) return PsiReference.EMPTY_ARRAY
                    if (element.text.contains('*')) return PsiReference.EMPTY_ARRAY
                    return arrayOf(PhpClassPatternReference(element))
                }
            }
        )
    }

    /**
     * Reference attached to an individual T_NAME_PART leaf token inside a NamespaceName.
     * Resolves to the PHP class identified by the full FQN of the enclosing NamespaceName,
     * so clicking any segment of "Demo\Attribute\Cacheable" navigates to \Demo\Attribute\Cacheable.
     */
    private class PhpClassLeafReference(
        element: PsiElement,
        private val namespaceName: NamespaceName
    ) : PsiReferenceBase<PsiElement>(element, true) {

        override fun resolve(): PsiElement? {
            val fqn = namespaceName.getFQN()
            val index = PhpIndex.getInstance(element.project)
            return index.getClassesByFQN(fqn).firstOrNull()
                ?: index.getInterfacesByFQN(fqn).firstOrNull()
                ?: index.getTraitsByFQN(fqn).firstOrNull()
        }

        override fun getVariants(): Array<Any> = emptyArray()
    }

    /**
     * Reference for a concrete (wildcard-free) NamespacePattern inside a classFilter.
     * The pattern text (e.g. "Demo\Aspect\SomeClass") is normalised to FQN by prepending '\'.
     */
    private class PhpClassPatternReference(element: NamespacePattern) : PsiReferenceBase<NamespacePattern>(element, true) {
        override fun resolve(): PsiElement? {
            val text = element.text
            val fqn = if (text.startsWith("\\")) text else "\\$text"
            val index = PhpIndex.getInstance(element.project)
            return index.getClassesByFQN(fqn).firstOrNull()
                ?: index.getInterfacesByFQN(fqn).firstOrNull()
                ?: index.getTraitsByFQN(fqn).firstOrNull()
        }

        override fun getVariants(): Array<Any> = emptyArray()
    }
}
