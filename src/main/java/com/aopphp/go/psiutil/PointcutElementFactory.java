package com.aopphp.go.psiutil;

import com.aopphp.go.GoAopFileType;
import com.aopphp.go.psi.PointcutExpression;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

public class PointcutElementFactory {

    public static PointcutExpression createPointcut(Project project, String expression) {
        final PointcutFile file = createFile(project, expression);
        PsiElement firstChild = file.getFirstChild();

        boolean isPointcut = (firstChild instanceof PointcutExpression);

        return isPointcut ? (PointcutExpression) firstChild : null;
    }

    @NotNull
    public static PointcutFile createFile(Project project, String text) {
        String name = "dummy.goaop";

        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);

        return (PointcutFile) fileFactory.createFileFromText(name, GoAopFileType.INSTANCE, text);
    }
}
