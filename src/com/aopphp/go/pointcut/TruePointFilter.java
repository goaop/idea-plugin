package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TruePointFilter implements PointFilter
{
    private static Set<KindFilter> KIND_ALL = new HashSet<KindFilter>(Arrays.asList(KindFilter.values())) ;

    private static TruePointFilter instance = null;
    private TruePointFilter() {};

    public static PointFilter getInstance()
    {
        if (instance == null) {
            instance = new TruePointFilter();
        }

        return instance;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        return true;
    }

    @Override
    public Set<KindFilter> getKind() {
        return KIND_ALL;
    }
}
