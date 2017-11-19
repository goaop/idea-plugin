package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpElementWithModifier;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * MemberAccessMatcherFilter performs checks of access for element
 */
public class MemberAccessMatcherFilter implements PointFilter {

    private static Set<KindFilter> KIND_ALL = new HashSet<KindFilter>(Arrays.asList(KindFilter.values())) ;
    private Set<PhpModifier.Access> allowedAccess;

    public MemberAccessMatcherFilter(Set<PhpModifier.Access> allowedAccess) {

        this.allowedAccess = allowedAccess;
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

        return allowedAccess.contains(modifier.getAccess());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberAccessMatcherFilter)) return false;

        MemberAccessMatcherFilter that = (MemberAccessMatcherFilter) o;

        return allowedAccess.equals(that.allowedAccess);
    }

    @Override
    public int hashCode() {
        return allowedAccess.hashCode();
    }
}
