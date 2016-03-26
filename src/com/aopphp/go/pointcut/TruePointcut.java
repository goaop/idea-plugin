package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Canonical pointcut instance that matches all points.
 */
public class TruePointcut implements Pointcut
{
    private static final Set<KindFilter> KIND_ALL = new HashSet<KindFilter>(Arrays.asList(KindFilter.values())) ;

    /**
     * Filter for class
     */
    protected PointFilter classFilter = TruePointFilter.getInstance();

    private Set<KindFilter> kind = KIND_ALL;

    public TruePointcut() {
    }

    public TruePointcut(Set<KindFilter> kind) {
        this.kind = kind;
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
        return true;
    }

    @Override
    public Set<KindFilter> getKind() {
        return kind;
    }
}
