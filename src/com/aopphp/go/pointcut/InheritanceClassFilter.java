package com.aopphp.go.pointcut;

import com.jetbrains.php.PhpClassHierarchyUtils;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Collections;
import java.util.Set;

/**
 * Inheritance class matcher that match single class name or any subclass
 */
public class InheritanceClassFilter implements PointFilter {

    /**
     * Filter type
     */
    private static final Set<KindFilter> KIND_CLASS = Collections.singleton(KindFilter.KIND_CLASS);

    /**
     * Instance class name to match
     */
    private String parentClassName;

    public InheritanceClassFilter(String parentClassName) {

        this.parentClassName = parentClassName;
    }

    @Override
    public Set<KindFilter> getKind() {
        return KIND_CLASS;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        if (!(element instanceof PhpClass)) {
            return false;
        }

        PhpClass parentClass = PhpIndex.getInstance(element.getProject()).getClassByName(parentClassName);
        if (parentClass == null) {
            return false;
        }

        return PhpClassHierarchyUtils.isSuperClass(parentClass, (PhpClass) element, false);
    }
}
