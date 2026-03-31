package com.aopphp.go.provider

import com.aopphp.go.GoAopIcons
import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpClass
import javax.swing.Icon

/**
 * Replaces the default PHP class/file icon with the Aspect icon for classes implementing \Go\Aop\Aspect.
 * The project tree passes PhpFile; structure view and navigation pass PhpClass directly.
 */
class AspectIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        val phpClass = when (element) {
            is PhpClass -> element
            is PhpFile  -> element.topLevelDefs.values().filterIsInstance<PhpClass>().firstOrNull()
            else        -> return null
        } ?: return null
        if (phpClass.implementedInterfaces.none { it.fqn == "\\Go\\Aop\\Aspect" }) return null
        return GoAopIcons.ASPECT
    }
}
