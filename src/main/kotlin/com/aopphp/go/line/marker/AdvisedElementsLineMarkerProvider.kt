package com.aopphp.go.line.marker

import com.aopphp.go.GoAopIcons
import com.aopphp.go.pointcut.PointcutAdvisor
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Provides gutter icons on PHP methods/fields/classes that are advised by AOP aspects.
 */
class AdvisedElementsLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val node = element.node ?: return
        val elementType = node.elementType
        val parent = element.parent

        val isMethod = elementType == PhpTokenTypes.IDENTIFIER && parent is Method
        val isClass  = elementType == PhpTokenTypes.IDENTIFIER && parent is PhpClass
        val isField  = elementType == PhpTokenTypes.VARIABLE   && parent is Field

        if (!isMethod && !isClass && !isField) return

        val classMember = PsiTreeUtil.getParentOfType(element, PhpNamedElement::class.java) ?: return
        val advices = PointcutAdvisor.getMatchedAdvices(classMember)
        if (advices.isEmpty()) return

        val builder = NavigationGutterIconBuilder.create(GoAopIcons.ADVISING_ELEMENT)
        builder.setTooltipText(
            if (advices.size > 1) "Navigate to AOP advices"
            else "Advised by '${advices[0].fqn}'"
        )
        builder.setTargets(advices)
        result.add(builder.createLineMarkerInfo(element))
    }
}
