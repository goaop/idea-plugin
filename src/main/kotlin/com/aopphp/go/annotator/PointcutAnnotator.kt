package com.aopphp.go.annotator

import com.aopphp.go.pointcut.ModifierMatcherFilter
import com.aopphp.go.psi.AccessPointcut
import com.aopphp.go.psi.CflowbelowPointcut
import com.aopphp.go.psi.DynamicExecutionPointcut
import com.aopphp.go.psi.FunctionExecutionReference
import com.aopphp.go.psi.MemberModifiers
import com.aopphp.go.psi.PointcutReference
import com.aopphp.go.psi.PointcutTypes
import com.aopphp.go.reference.PointcutReferenceResolver
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer

/**
 * Reports pointcut constructs that Go! AOP 4.0 rejects or never matches:
 * - `dynamic(...)` and `cflowbelow(...)` pointcuts, removed in 4.0
 * - more than one group of `|` alternatives in a modifier pattern
 * - `execution(function(*))` without a namespace
 * - `access(... Foo::property)`, static properties are never intercepted
 * - `access(readonly ...)`, readonly properties are never intercepted
 * - named pointcut references that do not resolve to a `#[Pointcut]` method
 */
class PointcutAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is DynamicExecutionPointcut -> annotateDynamic(element, holder)
            is CflowbelowPointcut -> holder.newAnnotation(
                HighlightSeverity.ERROR,
                "The cflowbelow() pointcut is not supported by Go! AOP 4.0"
            ).range(element.firstChild).create()
            is MemberModifiers -> annotateModifiers(element, holder)
            is FunctionExecutionReference -> annotateFunction(element, holder)
            is AccessPointcut -> annotateAccess(element, holder)
            is PointcutReference -> annotateReference(element, holder)
        }
        if (element.node?.elementType == PointcutTypes.T_QUESTION_MARK && element.parent !is com.aopphp.go.psi.ReturnTypePattern) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "The '?' wildcard was removed in Go! AOP 4.0: use '*' instead ('?' only marks a nullable return type)"
            ).range(element).create()
        }
    }

    private fun annotateDynamic(element: DynamicExecutionPointcut, holder: AnnotationHolder) {
        val builder = holder.newAnnotation(
            HighlightSeverity.ERROR,
            "The dynamic() pointcut was removed in Go! AOP 4.0: intercept the magic method and read the invoked name from the arguments"
        ).range(element.firstChild)
        val memberReference = element.memberReference
        val isStatic = memberReference.memberAccessType.text == "::"
        val replacement = "execution(" + memberReference.memberModifiers.text + " " +
            memberReference.classFilter.text + memberReference.memberAccessType.text +
            (if (isStatic) "__callStatic" else "__call") + "(*))"
        builder.withFix(ReplaceInjectedTextFix(element, replacement, "Replace with execution of the magic method"))
        builder.create()
    }

    private fun annotateModifiers(element: MemberModifiers, holder: AnnotationHolder) {
        val groupsWithAlternatives = element.modifierGroupList.filter { it.memberModifierList.size > 1 }
        if (groupsWithAlternatives.size > 1) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Only one group of '|' alternatives is supported per modifier pattern"
            ).range(groupsWithAlternatives[1]).create()
        }
    }

    private fun annotateFunction(element: FunctionExecutionReference, holder: AnnotationHolder) {
        if (!element.namespacePattern.text.contains('\\')) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "A function pattern needs a namespace, e.g. execution(App\\*(*)) or execution(**\\*(*))"
            ).range(element.namespacePattern).create()
        }
    }

    private fun annotateAccess(element: AccessPointcut, holder: AnnotationHolder) {
        val memberReference = element.memberReference
        if (memberReference.memberAccessType.text == "::") {
            holder.newAnnotation(HighlightSeverity.WARNING, "Static properties are never intercepted")
                .range(memberReference.memberAccessType).create()
        }
        val readonlyRequired = memberReference.memberModifiers.modifierGroupList.any { group ->
            group.memberModifierList.size == 1 &&
                ModifierMatcherFilter.bitOf(group.memberModifierList[0].text) == ModifierMatcherFilter.READONLY
        }
        if (readonlyRequired) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Readonly properties are never intercepted")
                .range(memberReference.memberModifiers).create()
        }
    }

    private fun annotateReference(element: PointcutReference, holder: AnnotationHolder) {
        val name = element.namePatternPart
        if (name.text.contains('*') || name.text.contains('|')) {
            holder.newAnnotation(HighlightSeverity.ERROR, "A pointcut reference names one pointcut, patterns are not allowed")
                .range(name).create()
            return
        }
        // Only references inside PHP strings can be resolved: standalone .goaop files have no aspect class
        val isSelf = PointcutReferenceResolver.isSelfReference(element)
        if (isSelf && InjectedLanguageManager.getInstance(element.project).getInjectionHost(element) == null) return
        val classFqn = PointcutReferenceResolver.aspectClassFqn(element) ?: return

        val method = PointcutReferenceResolver.resolveMethod(element)
        if (method == null || !PointcutReferenceResolver.isNamedPointcut(method)) {
            val owner = if (isSelf) "this aspect" else classFqn.removePrefix("\\")
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Unknown pointcut '${name.text}': no method with #[Pointcut] in $owner"
            ).range(name).create()
        }
    }

    /**
     * Replaces an element of an injected pointcut by editing the PHP string that hosts it.
     */
    private class ReplaceInjectedTextFix(
        element: PsiElement,
        private val replacement: String,
        private val text: String,
    ) : IntentionAction {
        private val pointer: SmartPsiElementPointer<PsiElement> =
            SmartPointerManager.getInstance(element.project).createSmartPsiElementPointer(element)

        override fun getText() = text
        override fun getFamilyName() = "Go! AOP 4.0 migration"
        override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = pointer.element != null
        override fun startInWriteAction() = true

        override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
            val element = pointer.element ?: return
            val injectionManager = InjectedLanguageManager.getInstance(project)
            val hostRange = injectionManager.injectedToHost(element, element.textRange)
            val hostFile = injectionManager.getTopLevelFile(element) ?: return
            val document = PsiDocumentManager.getInstance(project).getDocument(hostFile) ?: return
            document.replaceString(hostRange.startOffset, hostRange.endOffset, replacement)
            PsiDocumentManager.getInstance(project).commitDocument(document)
        }
    }
}
