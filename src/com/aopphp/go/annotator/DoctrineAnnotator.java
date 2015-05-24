package com.aopphp.go.annotator;

import com.aopphp.go.pattern.CodePattern;
import com.aopphp.go.psi.NamespaceName;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import de.espend.idea.php.annotation.util.AnnotationUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Highlights an incorrect usage of doctrine annotations in the annotation pointcuts
 */
public class DoctrineAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (CodePattern.insideAnnotationPointcut().accepts(element)) {
            NamespaceName nameHolder = PsiTreeUtil.getParentOfType(element, NamespaceName.class);
            if (nameHolder == null) {
                return;
            }
            String classFQN   = nameHolder.getFQN();
            PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());

            Collection<PhpClass> classInstances = phpIndex.getClassesByFQN(classFQN);
            if (classInstances.isEmpty()) {
                holder.createErrorAnnotation(
                    nameHolder.getTextRange(),
                    "Class " + nameHolder.getText() + " is not defined in the project"
                );
                return;
            }
            if (!AnnotationUtil.isAnnotationClass(classInstances.iterator().next())) {
                holder.createErrorAnnotation(nameHolder.getTextRange(), "Unknown annotation " + nameHolder.getText());
            }
        }
    }
}
