package com.aopphp.go.pattern

import com.aopphp.go.PointcutQueryLanguage
import com.aopphp.go.psi.AnnotatedAccessPointcut
import com.aopphp.go.psi.AnnotatedExecutionPointcut
import com.aopphp.go.psi.AnnotatedWithinPointcut
import com.aopphp.go.psi.NamespaceName
import com.aopphp.go.psi.PointcutTypes
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.PhpAttribute
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.MethodImpl

object CodePattern : PlatformPatterns() {

    /**
     * Matches the leaf T_NAME_PART tokens inside annotation-pointcut class names.
     * Used by completion — fires at the typing position (leaf level).
     *   @execution(<className>), @access(<className>), @within(<className>)
     */
    @JvmStatic
    fun insideAnnotationPointcut(): ElementPattern<PsiElement> = or(
        psiElement(PointcutTypes.T_NAME_PART).withSuperParent(2, AnnotatedExecutionPointcut::class.java),
        psiElement(PointcutTypes.T_NAME_PART).withSuperParent(2, AnnotatedAccessPointcut::class.java),
        psiElement(PointcutTypes.T_NAME_PART).withSuperParent(2, AnnotatedWithinPointcut::class.java),
    )

    /**
     * Matches the NamespaceName node (whole class reference) inside annotation pointcuts.
     * Used by the annotator — fires on the composite node so annotations can cover its full range.
     */
    @JvmStatic
    fun annotationPointcutClassName(): ElementPattern<PsiElement> = or(
        psiElement(NamespaceName::class.java).withParent(AnnotatedExecutionPointcut::class.java),
        psiElement(NamespaceName::class.java).withParent(AnnotatedAccessPointcut::class.java),
        psiElement(NamespaceName::class.java).withParent(AnnotatedWithinPointcut::class.java),
    )

    @JvmStatic
    fun insidePointcutLanguage(): ElementPattern<PsiElement> =
        psiElement().withLanguage(PointcutQueryLanguage)

    @JvmStatic
    fun startOfMemberModifiers(): ElementPattern<PsiElement> = psiElement().afterLeafSkipping(
        or(
            psiElement(PointcutTypes.T_LEFT_PAREN),
            psiElement(PointcutTypes.PRIVATE),
            psiElement(PointcutTypes.PROTECTED),
            psiElement(PointcutTypes.PUBLIC),
            psiElement(PointcutTypes.FINAL),
            psiElement(PointcutTypes.T_ALTERNATION),
            psiElement().whitespace(),
        ),
        or(
            psiElement(PointcutTypes.EXECUTION),
            psiElement(PointcutTypes.ACCESS),
        )
    )

    /**
     * Returns true when [host] is a string literal argument of a PHP 8 Attribute
     * whose class FQN starts with [attributePrefix].
     *
     * PSI path: StringLiteralExpression → ParameterList → PhpAttribute
     */
    @JvmStatic
    fun isInsidePhpAttribute(host: StringLiteralExpression, attributePrefix: String): Boolean {
        val paramList = host.parent as? ParameterList ?: return false
        val attr = paramList.parent as? PhpAttribute ?: return false
        return attr.fqn?.startsWith(attributePrefix) == true
    }

    /**
     * Returns true when [host] is the first argument of a PointcutBuilder method call:
     *   PointcutBuilder->method('<pointcutExpression>', ...)
     */
    @JvmStatic
    fun isInsidePointcutBuilderMethod(host: StringLiteralExpression): Boolean {
        val paramList = host.parent as? ParameterList ?: return false
        val methodRef = paramList.parent as? MethodReference ?: return false
        val resolved = methodRef.resolve() as? MethodImpl ?: return false
        return resolved.fqn?.startsWith("\\Go\\Aop\\Support\\PointcutBuilder") == true
    }
}
