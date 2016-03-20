package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Canonical PointFilter instance that matches all points.
 */
public class TruePointFilter implements PointFilter
{
    private static Set<KindFilter> KIND_ALL = new HashSet<KindFilter>(Arrays.asList(KindFilter.values())) ;

    public final static PointFilter INSTANCE = new TruePointFilter();
    private TruePointFilter() {};

    @Override
    public boolean matches(PhpNamedElement element) {
        return true;
    }

    @Override
    public Set<KindFilter> getKind() {
        return KIND_ALL;
    }
}
