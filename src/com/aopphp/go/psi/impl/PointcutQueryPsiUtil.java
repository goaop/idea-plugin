package com.aopphp.go.psi.impl;

import com.aopphp.go.psi.NamespaceName;

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
}
