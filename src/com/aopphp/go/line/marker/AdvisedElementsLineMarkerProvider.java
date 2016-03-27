package com.aopphp.go.line.marker;


import com.aopphp.go.GoAopIcons;
import com.aopphp.go.pointcut.PointcutAdvisor;
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
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Provides a navigation links from the intercepted elements to the concrete advices
 */
public class AdvisedElementsLineMarkerProvider extends RelatedItemLineMarkerProvider {
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
        boolean isPhpClass       = (elementType == PhpTokenTypes.IDENTIFIER) && parent instanceof PhpClass;
        boolean isPhpClassField  = (elementType == PhpTokenTypes.VARIABLE) && parent instanceof Field;

        if (isPhpClass || isPhpClassMethod || isPhpClassField) {
            PhpNamedElement classMember = PsiTreeUtil.getParentOfType(element, PhpNamedElement.class);
            List<PhpNamedElement> matchedAdvices = PointcutAdvisor.getMatchedAdvices(classMember);
            if (matchedAdvices.size() > 0) {
                NavigationGutterIconBuilder<PsiElement> builder = null;

                builder = NavigationGutterIconBuilder.create(GoAopIcons.ADVISING_ELEMENT);
                if (matchedAdvices.size() > 1) {
                    builder.setTooltipText("Navigate to AOP advices");
                } else {
                    builder.setTooltipText("Advised by '" + matchedAdvices.get(0).getFQN() + "'");
                }
                builder.setTargets(matchedAdvices);
                result.add(builder.createLineMarkerInfo(element));
            }
        }
    }
}
