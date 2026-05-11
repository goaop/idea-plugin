package com.aopphp.go.util

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpClass

object PluginUtil {
    /**
     * Returns true if the given element is a Go! AOP Aspect (implements \Go\Aop\Aspect).
     */
    @JvmStatic
    fun isAspect(element: PsiElement?): Boolean {
        if (element !is PhpClass) return false
        return element.interfaceNames.contains("\\Go\\Aop\\Aspect")
    }
}
