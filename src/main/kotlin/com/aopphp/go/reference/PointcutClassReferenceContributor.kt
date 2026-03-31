package com.aopphp.go.reference

import com.aopphp.go.psi.AnnotatedAccessPointcut
import com.aopphp.go.psi.AnnotatedExecutionPointcut
import com.aopphp.go.psi.AnnotatedWithinPointcut
import com.aopphp.go.psi.NamespaceName
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
 * Makes PHP class names inside @execution(), @access(), and @within() pointcuts
 * clickable — they resolve to the corresponding PHP class, just like a `use` statement.
 */
class PointcutClassReferenceContributor : PsiReferenceContributor() {

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            psiElement(NamespaceName::class.java).withParent(
                psiElement().andOr(
                    psiElement(AnnotatedExecutionPointcut::class.java),
                    psiElement(AnnotatedAccessPointcut::class.java),
                    psiElement(AnnotatedWithinPointcut::class.java)
                )
            ),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    if (element !is NamespaceName) return PsiReference.EMPTY_ARRAY
                    return arrayOf(PhpClassReference(element))
                }
            }
        )
    }

    // getFQN() already returns the FQN with a leading '\' (e.g. '\Demo\Attribute\Cacheable')
    private class PhpClassReference(element: NamespaceName) : PsiReferenceBase<NamespaceName>(element, true) {
        override fun resolve(): PsiElement? =
            PhpIndex.getInstance(element.project).getAnyByFQN(element.getFQN()).firstOrNull()

        override fun getVariants(): Array<Any> = emptyArray()
    }
}
