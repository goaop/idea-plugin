package com.aopphp.go.psiutil;

import com.aopphp.go.psi.MemberAccessType;
import com.aopphp.go.psi.MemberModifier;
import com.aopphp.go.psi.NamespaceName;
import com.jetbrains.php.lang.psi.elements.PhpModifier;

public class PointcutPsiUtil {

    public static String getFQN(NamespaceName namespaceName) {
        String fqn = "";
        if (namespaceName.getText().charAt(0) != '\\') {
            fqn = "\\";
        }
        fqn += namespaceName.getText();

        return fqn;
    }

    public static PhpModifier.Access getMemberAccess(MemberModifier element) {
        return PhpModifier.Access.valueOf(element.getText().toUpperCase());
    }

    public static PhpModifier.State getMemberAccessType(MemberAccessType element) {
        if (element.getText().equals("->")) {
            return PhpModifier.State.DYNAMIC;
        }

        return PhpModifier.State.STATIC;
    }
}
