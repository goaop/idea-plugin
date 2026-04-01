package com.aopphp.go.annotator

import com.aopphp.go.PointcutQuerySyntaxHighlighter
import com.aopphp.go.pattern.CodePattern
import com.aopphp.go.psi.NamespaceName
import com.aopphp.go.util.AttributeTargetUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex

/**
 * Validates class names inside annotation pointcuts (@execution, @access, @within):
 * - The class must exist in the project.
 * - The class must be a PHP 8 Attribute (decorated with #[\Attribute]).
 * - The attribute's target mask must be compatible with the pointcut type:
 *     @execution → TARGET_METHOD or TARGET_FUNCTION
 *     @access    → TARGET_PROPERTY
 *     @within    → TARGET_CLASS
 * Also applies CLASS_REFERENCE semantic highlighting to valid class names.
 */
class AttributeAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!CodePattern.annotationPointcutClassName().accepts(element)) return

        val nameHolder = element as? NamespaceName ?: return
        val classFQN = nameHolder.getFQN()
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
        if (phpClass.getAttributes("\\Attribute").isEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Class ${nameHolder.text} is not a PHP attribute (#[Attribute] missing)"
            ).range(nameHolder.textRange).create()
            return
        }

        // Check target compatibility with the enclosing pointcut type
        val pointcutParent = nameHolder.parent
        val requiredBits = AttributeTargetUtil.requiredBitsFor(pointcutParent)
        if (!AttributeTargetUtil.isCompatible(phpClass, requiredBits)) {
            val required = AttributeTargetUtil.requiredDescription(pointcutParent)
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Attribute ${nameHolder.text} is not suitable here — requires $required"
            ).range(nameHolder.textRange).create()
            return
        }

        // Valid — apply semantic class-reference highlighting
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(nameHolder.textRange)
            .textAttributes(PointcutQuerySyntaxHighlighter.CLASS_REFERENCE)
            .create()
    }
}
