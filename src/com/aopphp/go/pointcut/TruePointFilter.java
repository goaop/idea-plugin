package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Canonical PointFilter instance that matches all points.
 */
public final class TruePointFilter implements PointFilter
{
    private static final Set<KindFilter> KIND_ALL = new HashSet<KindFilter>(Arrays.asList(KindFilter.values())) ;

    private static final TruePointFilter INSTANCE = new TruePointFilter();
    private TruePointFilter() {};

    public static PointFilter getInstance()
    {
        return INSTANCE;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        return true;
    }

    @Override
    public Set<KindFilter> getKind() {
        return KIND_ALL;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TruePointFilter;
    }
}
