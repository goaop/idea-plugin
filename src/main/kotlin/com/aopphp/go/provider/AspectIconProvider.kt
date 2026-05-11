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
        val isAspect = when (element) {
            is PhpClass -> element.implementedInterfaces.any { it.fqn == "\\Go\\Aop\\Aspect" }
            is PhpFile  -> element.topLevelDefs.values().filterIsInstance<PhpClass>().any { phpClass ->
                phpClass.implementedInterfaces.any { it.fqn == "\\Go\\Aop\\Aspect" }
            }
            else        -> false
        }
        return if (isAspect) GoAopIcons.ASPECT else null
    }
}
