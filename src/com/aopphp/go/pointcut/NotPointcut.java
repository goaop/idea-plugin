package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Logic "NOT" pointcut implementation
 */
public class NotPointcut implements Pointcut {

    /**
     * Stores pointcut kind
     */
    protected Set<KindFilter> kind;

    /**
     * Filter for class
     */
    protected PointFilter classFilter = TruePointFilter.getInstance();

    protected Pointcut pointcut;

    public NotPointcut(Pointcut pointcut) {

        this.pointcut = pointcut;
        this.kind     = pointcut.getKind();
    }

    @Override
    public Set<KindFilter> getKind() {
        return kind;
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
        if (!(element instanceof PhpClassMember)) {
            return false;
        }
        PhpClass preFilter = ((PhpClassMember) element).getContainingClass();
        if (!pointcut.getClassFilter().matches(preFilter)) {
            return true;
        }

        return !pointcut.matches(element);
    }
}
