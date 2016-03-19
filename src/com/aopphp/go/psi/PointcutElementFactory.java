package com.aopphp.go.psi;

import com.aopphp.go.GoAopFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Factory for pointcut elements
 */
public class PointcutElementFactory {

    /**
     * Creates a pointcut expression from string expression
     *
     * @param project Instance of current project
     * @param expression Literal expression of pointcut
     *
     * @return PointcutExpression instance if Ok or null
     */
    public static PointcutExpression createPointcut(Project project, String expression) {
        final PointcutFile file = createFile(project, expression);
        PsiElement firstChild = file.getFirstChild();

        boolean isPointcut = (firstChild instanceof PointcutExpression);

        return isPointcut ? (PointcutExpression)firstChild : null;
    }

    @NotNull
    public static PointcutFile createFile(Project project, String text) {
        String name = "dummy.goaop";

        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);

        return (PointcutFile) fileFactory.createFileFromText(name, GoAopFileType.INSTANCE, text);
    }
}
