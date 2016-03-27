package com.aopphp.go.line.marker;


import com.aopphp.go.GoAopIcons;
import com.aopphp.go.pointcut.PointcutAdvisor;
import com.aopphp.go.util.PluginUtil;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Provides a navigation links from the concrete advices to the intercepted elements
 */
public class AdvisorLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(
            @NotNull PsiElement element,
            Collection<? super RelatedItemLineMarkerInfo> result
    ) {
        ASTNode node = element.getNode();
        if (node == null) {
            return;
        }
        IElementType elementType = node.getElementType();
        PsiElement parent        = element.getParent();

        boolean isPhpClassMethod = (elementType == PhpTokenTypes.IDENTIFIER) && parent instanceof Method;
        boolean isPhpClassField  = (elementType == PhpTokenTypes.VARIABLE) && parent instanceof Field;
        if (!isPhpClassMethod && !isPhpClassField) {
            return;
        }
        boolean isParentAspect = PluginUtil.isAspect(((PhpClassMember) parent).getContainingClass());
        if (!isParentAspect) {
            return;
        }

        PhpNamedElement aspectMember = PsiTreeUtil.getParentOfType(element, PhpNamedElement.class);
        List<PhpNamedElement> matchedElements = PointcutAdvisor.getMatchedElements(aspectMember);
        if (matchedElements != null && matchedElements.size() > 0) {
            NavigationGutterIconBuilder<PsiElement> builder;

            builder = NavigationGutterIconBuilder.create(GoAopIcons.ADVISED_ELEMENT);
            if (matchedElements.size() > 1) {
                builder.setTooltipText("Navigate to advised elements");
            } else {
                builder.setTooltipText("Advising '" + matchedElements.get(0).getFQN() + "'");
            }
            builder.setTargets(matchedElements);
            result.add(builder.createLineMarkerInfo(element));
        }
    }
}
