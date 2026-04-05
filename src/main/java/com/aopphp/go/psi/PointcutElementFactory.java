package com.aopphp.go.psi;

import com.aopphp.go.GoAopFileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Creates a single T_NAME_PART leaf element with the given text.
     * Used by rename refactoring to produce a replacement token.
     */
    @Nullable
    public static PsiElement createNamePart(@NotNull Project project, @NotNull String name) {
        // "within(name)" always parses successfully and contains exactly one T_NAME_PART
        PointcutFile file = createFile(project, "within(" + name + ")");
        PsiElement leaf = file.findElementAt("within(".length());
        if (leaf != null && leaf.getNode().getElementType() == PointcutTypes.T_NAME_PART) {
            return leaf;
        }
        return null;
    }

    @NotNull
    public static PointcutFile createFile(Project project, String text) {
        String name = "dummy.goaop";

        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);

        return (PointcutFile) fileFactory.createFileFromText(name, GoAopFileType.INSTANCE, text);
    }
}
