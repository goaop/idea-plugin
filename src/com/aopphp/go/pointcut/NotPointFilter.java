package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Set;

/**
 * Logic "NOT" point filter implementation
 */
public class NotPointFilter implements PointFilter {

    /**
     * Filter kind
     */
    private final Set<KindFilter> kind;

    /**
     * First part of filter
     */
    protected PointFilter first;

    public NotPointFilter(PointFilter first) {

        this.first = first;
        this.kind  = first.getKind();
    }

    @Override
    public Set<KindFilter> getKind() {
        return kind;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        return !first.matches(element);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotPointFilter)) return false;

        NotPointFilter that = (NotPointFilter) o;

        if (!kind.equals(that.kind)) return false;
        return first.equals(that.first);
    }

    @Override
    public int hashCode() {
        int result = kind.hashCode();
        result = 31 * result + first.hashCode();
        return result;
    }
}
