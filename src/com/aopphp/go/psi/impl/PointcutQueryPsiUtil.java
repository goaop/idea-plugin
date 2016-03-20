package com.aopphp.go.psi.impl;

import com.aopphp.go.pointcut.MemberAccessMatcherFilter;
import com.aopphp.go.psi.MemberAccessType;
import com.aopphp.go.psi.MemberModifier;
import com.aopphp.go.psi.MemberModifiers;
import com.aopphp.go.psi.NamespaceName;
import com.jetbrains.php.lang.psi.elements.PhpModifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class with miscellaneous PSI methods implementation
 */
public class PointcutQueryPsiUtil {

    /**
     * Returns an absolute FQN for namespace name node
     *
     * @param namespaceName Namespace holder
     * @return Fully-qualified namespace
     */
    public static String getFQN(NamespaceName namespaceName) {
        String fqn = "";
        if (namespaceName.getText().charAt(0) != '\\') {
            fqn = "\\";
        }
        fqn += namespaceName.getText();

        return fqn;
    }

    /**
     * Returns a valid instance of access type by text name
     *
     * @param element Current element
     * @return concrete PhpModifier.Access value
     */
    public static PhpModifier.Access getMemberAccess(MemberModifier element) {
        return PhpModifier.Access.valueOf(element.getText());
    }

    /**
     * Returns a member access matcher filter from the list of member access
     *
     * @param element Current element
     * @return filter for members, each item is OR-ed
     */
    public static MemberAccessMatcherFilter getMemberAccessMatcher(MemberModifiers element) {
        List<MemberModifier> memberModifierList = element.getMemberModifierList();
        HashSet<PhpModifier.Access> allowedAccess = new HashSet<>();
        for (MemberModifier memberModifier : memberModifierList) {
            allowedAccess.add(memberModifier.getMemberAccess());
        }

        return new MemberAccessMatcherFilter(allowedAccess);
    }

    /**
     * Returns a valid instance of member state (dynamic/static) by text name
     *
     * @param element Current element
     * @return concrete PhpModifier.State value
     */
    public static PhpModifier.State getMemberAccessType(MemberAccessType element) {
        if (element.getText().equals("->")) {
            return PhpModifier.State.DYNAMIC;
        };

        return PhpModifier.State.STATIC;
    }
}
