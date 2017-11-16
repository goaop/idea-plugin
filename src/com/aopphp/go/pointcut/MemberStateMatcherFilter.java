package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpElementWithModifier;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * MemberStateMatcherFilter performs checks of state for element
 */
public class MemberStateMatcherFilter implements PointFilter {

    private static Set<KindFilter> KIND_ALL = new HashSet<KindFilter>(Arrays.asList(KindFilter.values())) ;
    private PhpModifier.State allowedState;

    public MemberStateMatcherFilter(PhpModifier.State allowedState) {

        this.allowedState = allowedState;
    }

    @Override
    public Set<KindFilter> getKind() {
        return KIND_ALL;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        if (!(element instanceof PhpElementWithModifier)) {
            return false;
        }
        PhpModifier modifier = ((PhpElementWithModifier) element).getModifier();

        return allowedState.equals(modifier.getState());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberStateMatcherFilter)) return false;

        MemberStateMatcherFilter that = (MemberStateMatcherFilter) o;

        return allowedState == that.allowedState;
    }

    @Override
    public int hashCode() {
        return allowedState.hashCode();
    }
}
