package com.aopphp.go.pointcut;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import de.espend.idea.php.annotation.util.AnnotationUtil;

import java.util.List;
import java.util.Set;

/**
 * Annotation pointcut matches PHP named elements that have specific doctrine annotations in docBlock
 */
public class AnnotationPointcut implements Pointcut
{
    private PointFilter classFilter = TruePointFilter.getInstance();
    private Set<KindFilter> filterKind;

    /**
     * Specifies name of the expected class to receive
     */
    private final String expectedClass;

    public AnnotationPointcut(Set<KindFilter> filterKind, String annotationName) {
        this.filterKind     = filterKind;
        this.expectedClass = annotationName;
    }

    @Override
    public PointFilter getClassFilter() {
        return classFilter;
    }

    public void setClassFilter(PointFilter classFilter) {
        this.classFilter = classFilter;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        if (!canMatchElement(element)) {
            return false;
        }
        PhpDocComment docComment = element.getDocComment();
        if (docComment == null) {
            return false;
        }

        List<PhpDocTag> docTagList = PsiTreeUtil.getChildrenOfTypeAsList(docComment, PhpDocTag.class);
        for (PhpDocTag phpDocTag : docTagList) {
            PhpClass annotationReference = AnnotationUtil.getAnnotationReference(phpDocTag);
            if (annotationReference == null || annotationReference.getPresentableFQN() == null) {
                continue;
            }
            String fqn = annotationReference.getFQN();
            if (fqn != null && fqn.equals(expectedClass)) {
                return true;
            };
        }

        return false;
    }

    /**
     * Checks if filter kind matches instance of concrete element
     *
     * @param element PHP element to check
     *
     * @return true if pointcut can match this named element
     */
    private boolean canMatchElement(PhpNamedElement element) {
        if (element instanceof Method) {
            return filterKind.contains(KindFilter.KIND_METHOD);
        }

        if (element instanceof Field) {
            return filterKind.contains(KindFilter.KIND_PROPERTY);
        }

        if (element instanceof PhpClass) {
            return filterKind.contains(KindFilter.KIND_CLASS);
        }

        return false;
    }

    @Override
    public Set<KindFilter> getKind() {
        return filterKind;
    }
}
