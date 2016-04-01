package com.aopphp.go.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpUse;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PluginUtil {

    /**
     * Returns a map with php use imports
     *
     * @param psiFile Instance of file, should be PHP
     * @return Mapping of short names to the FQN
     */
    public static Map<String, String> getFileUseImports(PsiFile psiFile) {

        // search for use alias in local file
        final Map<String, String> useImports = new HashMap<String, String>();
        psiFile.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if(element instanceof PhpUse) {
                    visitUse((PhpUse) element);
                }
                super.visitElement(element);
            }

            private void visitUse(PhpUse phpUse) {
                String alias = phpUse.getAliasName();
                if(alias != null) {
                    useImports.put(alias, phpUse.getOriginal());
                } else {
                    useImports.put(phpUse.getName(), phpUse.getOriginal());
                }
            }

        });

        return useImports;
    }


    @Nullable
    public static String getClassNameReference(PhpDocTag phpDocTag) {
        return getClassNameReference(phpDocTag, getFileUseImports(phpDocTag.getContainingFile()));
    }

    @Nullable
    public static String getClassNameReference(PhpDocTag phpDocTag, Map<String, String> useImports) {

        if(useImports.size() == 0) {
            return null;
        }

        String annotationName = phpDocTag.getName();
        if(StringUtils.isBlank(annotationName)) {
            return null;
        }

        if(annotationName.startsWith("@")) {
            annotationName = annotationName.substring(1);
        }

        String className = annotationName;
        String subNamespaceName = "";
        if(className.contains("\\")) {
            className = className.substring(0, className.indexOf("\\"));
            subNamespaceName = annotationName.substring(className.length());
        }

        if(!useImports.containsKey(className)) {
            return null;
        }

        // normalize name
        String annotationFqnName = useImports.get(className) + subNamespaceName;
        if(!annotationFqnName.startsWith("\\")) {
            annotationFqnName = "\\" + annotationFqnName;
        }

        return annotationFqnName;

    }

    /**
     * Checks if the given element is aspect
     *
     * @param element Instance of PSI element
     * @return true if element is aspect or false
     */
    public static boolean isAspect(PsiElement element) {
        if (!(element instanceof PhpClass)) {
            return false;
        }

        String[] interfaceNames = ((PhpClass) element).getInterfaceNames();
        for (String interfaceName : interfaceNames) {
            if (interfaceName.equals("\\Go\\Aop\\Aspect")) {
                return true;
            }
        }

        return false;
    }
}
