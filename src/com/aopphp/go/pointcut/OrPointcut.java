package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Logic "OR" pointcut implementation
 */
public class OrPointcut extends AndPointcut {


    public OrPointcut(Pointcut first, Pointcut second) {

        this.first = first;
        this.second = second;

        // combine both sets
        Set<KindFilter> combined = new HashSet<KindFilter>(first.getKind());
        combined.addAll(second.getKind());

        this.kind        = combined;
        this.classFilter = new OrPointFilter(first.getClassFilter(), second.getClassFilter());
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        return isMatchesPointcut(element, first) || isMatchesPointcut(element, second);
    }
}
