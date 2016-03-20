package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Logic "OR" point filter implementation
 */
public class OrPointFilter implements PointFilter {

    /**
     * Filter kind
     */
    private Set<KindFilter> kind;

    /**
     * First part of filter
     */
    private PointFilter first;

    /**
     * Second part of filter
     */
    private PointFilter second;

    public OrPointFilter(PointFilter first, PointFilter second) {

        this.first = first;
        this.second = second;

        // combine both sets
        Set<KindFilter> combined = new HashSet<KindFilter>(first.getKind());
        combined.addAll(second.getKind());

        this.kind = combined;
    }

    @Override
    public Set<KindFilter> getKind() {
        return kind;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        return first.matches(element) || second.matches(element);
    }
}
