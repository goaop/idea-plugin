package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Set;

/**
 * Filter that restricts matching of a pointcut or introduction to a given set of reflection points.
 */
public interface PointFilter
{
    boolean matches(PhpNamedElement element);

    Set<KindFilter> getKind();
}
