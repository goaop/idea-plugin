package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Logic "AND" point filter implementation
 */
public class AndPointFilter implements PointFilter {

    /**
     * Filter kind
     */
    private final Set<KindFilter> kind;

    /**
     * First part of filter
     */
    private PointFilter first;

    /**
     * Second part of filter
     */
    private PointFilter second;

    public AndPointFilter(PointFilter first, PointFilter second) {

        this.first = first;
        this.second = second;

        // intersect kinds
        Set<KindFilter> intersection = new HashSet<KindFilter>(first.getKind());
        intersection.retainAll(second.getKind());

        this.kind = intersection;
    }

    @Override
    public Set<KindFilter> getKind() {
        return kind;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        return first.matches(element) && second.matches(element);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AndPointFilter)) return false;

        AndPointFilter that = (AndPointFilter) o;

        if (!kind.equals(that.kind)) return false;
        if (!first.equals(that.first)) return false;
        return second.equals(that.second);
    }

    @Override
    public int hashCode() {
        int result = kind.hashCode();
        result = 31 * result + first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
}
