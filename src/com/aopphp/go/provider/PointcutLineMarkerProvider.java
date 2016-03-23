package com.aopphp.go.provider;


import com.aopphp.go.GoAopIcons;
import com.aopphp.go.pointcut.PointcutAdvisor;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Provides a navigation links from the intercepted elements to the concrete advices
 */
public class PointcutLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(
            @NotNull PsiElement element,
            Collection<? super RelatedItemLineMarkerInfo> result
    ) {
        ASTNode node = element.getNode();
        if (node == null) {
            return;
        }
        if (node.getElementType() == PhpTokenTypes.IDENTIFIER && element.getParent() instanceof Method) {
            Method classMethod = PsiTreeUtil.getParentOfType(element, Method.class);
            Collection<PhpNamedElement> matchedAdvices = PointcutAdvisor.getMatchedAdvices(classMethod);
            if (matchedAdvices.size() > 0) {
                NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(GoAopIcons.FILE);
                builder.setTooltipText("Intercepted method");
                builder.setTargets(matchedAdvices);
                result.add(builder.createLineMarkerInfo(element));
            }
        }
    }
}
