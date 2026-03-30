package com.aopphp.go.line.marker

import com.aopphp.go.GoAopIcons
import com.aopphp.go.pointcut.PointcutAdvisor
import com.aopphp.go.util.PluginUtil
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Provides gutter icons on aspect methods/fields pointing to the elements they advise.
 */
class AdvisorLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val node = element.node ?: return
        val elementType = node.elementType
        val parent = element.parent

        val isMethod = elementType == PhpTokenTypes.IDENTIFIER && parent is Method
        val isField  = elementType == PhpTokenTypes.VARIABLE   && parent is Field
        if (!isMethod && !isField) return

        val containingClass = (parent as? PhpClassMember)?.containingClass ?: return
        if (!PluginUtil.isAspect(containingClass)) return

        val aspectMember = PsiTreeUtil.getParentOfType(element, PhpNamedElement::class.java) ?: return
        val matched = PointcutAdvisor.getMatchedElements(aspectMember)
        if (matched.isNullOrEmpty()) return

        val builder = NavigationGutterIconBuilder.create(GoAopIcons.ADVISED_ELEMENT)
        builder.setTooltipText(
            if (matched.size > 1) "Navigate to advised elements"
            else "Advising '${matched[0].fqn}'"
        )
        builder.setTargets(matched)
        result.add(builder.createLineMarkerInfo(element))
    }
}
