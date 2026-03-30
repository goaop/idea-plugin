package com.aopphp.go.annotator

import com.aopphp.go.pattern.CodePattern
import com.aopphp.go.psi.NamespaceName
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex

/**
 * Validates class names inside annotation pointcuts (@execution, @access, @within):
 * the referenced class must exist and must be a PHP 8 Attribute (decorated with #[\Attribute]).
 */
class AttributeAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!CodePattern.insideAnnotationPointcut().accepts(element)) return

        val nameHolder = PsiTreeUtil.getParentOfType(element, NamespaceName::class.java) ?: return
        val classFQN = nameHolder.fqn
        val phpIndex = PhpIndex.getInstance(element.project)

        val classInstances = phpIndex.getClassesByFQN(classFQN)
        if (classInstances.isEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Class ${nameHolder.text} is not defined in the project"
            ).range(nameHolder.textRange).create()
            return
        }

        val phpClass = classInstances.first()
        val isAttributeClass = phpClass.getAttributes("\\Attribute").isNotEmpty()
        if (!isAttributeClass) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Class ${nameHolder.text} is not a PHP attribute (#[Attribute] missing)"
            ).range(nameHolder.textRange).create()
        }
    }
}
