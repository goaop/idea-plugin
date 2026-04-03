package com.aopphp.go.pattern

import com.aopphp.go.PointcutQueryLanguage
import com.aopphp.go.psi.AnnotatedAccessPointcut
import com.aopphp.go.psi.AnnotatedExecutionPointcut
import com.aopphp.go.psi.AnnotatedWithinPointcut
import com.aopphp.go.psi.ClassFilter
import com.aopphp.go.psi.MemberReference
import com.aopphp.go.psi.NamespaceName
import com.aopphp.go.psi.NamespacePattern
import com.aopphp.go.psi.PointcutReference
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

    /**
     * Matches T_NAME_PART tokens after `$this->` inside a pointcut reference.
     * Used by [com.aopphp.go.completion.SelfPointcutReferenceCompletionProvider].
     * The provider additionally checks that the enclosing PointcutReference starts
     * with T_THIS (not a namespace-qualified reference like `SomeClass->method`).
     */
    @JvmStatic
    fun insidePointcutSelfReference(): ElementPattern<PsiElement> =
        psiElement(PointcutTypes.T_NAME_PART).withSuperParent(2, PointcutReference::class.java)

    /**
     * Matches T_NAME_PART tokens at the class-name position of a pointcut classFilter.
     *
     * Two cases are handled:
     *
     * Case 1 — Valid PSI: T_NAME_PART inside NamespacePattern → ClassFilter.
     *   Fires when the expression is syntactically complete enough for the parser to
     *   build a ClassFilter node (e.g., method name and argument list already present).
     *
     * Case 2 — Fallback for incomplete/invalid PSI: T_NAME_PART that follows an
     *   execution/access/within/staticinitialization/initialization keyword after skipping
     *   the opening paren, optional whitespace, and any visibility modifiers.
     *   This fires while the user is still typing the class name and the expression is
     *   not yet syntactically valid (missing -> methodName(*)).
     */
    @JvmStatic
    fun insideClassFilter(): ElementPattern<PsiElement> = or(
        psiElement(PointcutTypes.T_NAME_PART)
            .inside(psiElement(NamespacePattern::class.java).withParent(ClassFilter::class.java)),
        psiElement(PointcutTypes.T_NAME_PART).afterLeafSkipping(
            or(
                psiElement().whitespace(),
                psiElement(PointcutTypes.T_LEFT_PAREN),
                psiElement(PointcutTypes.PRIVATE),
                psiElement(PointcutTypes.PROTECTED),
                psiElement(PointcutTypes.PUBLIC),
                psiElement(PointcutTypes.FINAL),
                psiElement(PointcutTypes.T_ALTERNATION),
            ),
            or(
                psiElement(PointcutTypes.EXECUTION),
                psiElement(PointcutTypes.ACCESS),
                psiElement(PointcutTypes.WITHIN),
                psiElement(PointcutTypes.STATICINITIALIZATION),
                psiElement(PointcutTypes.INITIALIZATION),
            )
        )
    )

    /**
     * Matches T_NAME_PART tokens at the member-name position of a pointcut memberReference.
     *
     * Two cases are handled:
     *
     * Case 1 — Valid PSI: T_NAME_PART at depth 3 below MemberReference
     *   (T_NAME_PART → NamePatternPart → NamePattern → MemberReference).
     *
     * Case 2 — Fallback for incomplete/invalid PSI: T_NAME_PART immediately after a
     *   T_OBJECT_ACCESS (->) or T_STATIC_ACCESS (::) token.
     *   The provider filters out `$this->` references to avoid conflicts with
     *   [com.aopphp.go.completion.SelfPointcutReferenceCompletionProvider].
     */
    @JvmStatic
    fun insideMemberNamePattern(): ElementPattern<PsiElement> = or(
        psiElement(PointcutTypes.T_NAME_PART).withSuperParent(3, MemberReference::class.java),
        psiElement(PointcutTypes.T_NAME_PART).afterLeaf(
            or(psiElement(PointcutTypes.T_OBJECT_ACCESS), psiElement(PointcutTypes.T_STATIC_ACCESS))
        )
    )

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
