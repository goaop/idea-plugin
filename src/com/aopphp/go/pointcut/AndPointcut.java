package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Logic "AND" pointcut implementation
 */
public class AndPointcut implements Pointcut {

    /**
     * Stores pointcut kind
     */
    protected Set<KindFilter> kind;

    /**
     * Filter for class
     */
    protected PointFilter classFilter;

    protected Pointcut first;
    protected Pointcut second;

    public AndPointcut(Pointcut first, Pointcut second) {

        this.first = first;
        this.second = second;

        // intersect kinds
        Set<KindFilter> intersection = new HashSet<KindFilter>(first.getKind());
        intersection.retainAll(second.getKind());

        this.kind        = intersection;
        this.classFilter = new AndPointFilter(first.getClassFilter(), second.getClassFilter());
    }

    /**
     * Default constructor to allow inheritance without calling to super()
     */
    protected AndPointcut() {}

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
        return isMatchesPointcut(element, first) && isMatchesPointcut(element, second);
    }

    /**
     * Checks if point filter matches the point
     *
     * @param point Point to check
     * @param pointcut Pointcut part
     *
     * @return bool
     */
    protected boolean isMatchesPointcut(PhpNamedElement point, Pointcut pointcut) {
        if (!(point instanceof PhpClassMember)) {
            return false;
        }

        PhpClass containingClass = ((PhpClassMember) point).getContainingClass();

        return pointcut.matches(point) && pointcut.getClassFilter().matches(containingClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AndPointcut)) return false;

        AndPointcut that = (AndPointcut) o;

        if (!kind.equals(that.kind)) return false;
        if (!classFilter.equals(that.classFilter)) return false;
        if (!first.equals(that.first)) return false;
        return second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = kind.hashCode();
        result = 31 * result + classFilter.hashCode();
        result = 31 * result + first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
}
