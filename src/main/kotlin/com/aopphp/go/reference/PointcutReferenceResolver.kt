package com.aopphp.go.reference

import com.aopphp.go.psi.PointcutReference
import com.aopphp.go.psi.PointcutTypes
import com.aopphp.go.util.GoAopAttributes
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass

/**
 * Resolves named pointcut references: `$this->pointcutName` and `Aspect\Class->pointcutName`.
 */
object PointcutReferenceResolver {

    /** True when [reference] starts with `$this` */
    @JvmStatic
    fun isSelfReference(reference: PointcutReference): Boolean =
        reference.firstChild?.node?.elementType == PointcutTypes.T_THIS

    /**
     * Returns the FQN of the aspect class [reference] points to. [host] is the PHP string that holds the
     * expression; when null, it is looked up from the injected [reference].
     */
    @JvmStatic
    fun aspectClassFqn(reference: PointcutReference, host: PsiElement? = null): String? {
        if (!isSelfReference(reference)) return reference.namespaceName?.getFQN()
        val phpHost = host ?: InjectedLanguageManager.getInstance(reference.project).getInjectionHost(reference)
        return PsiTreeUtil.getParentOfType(phpHost, PhpClass::class.java)?.fqn
    }

    /** Returns the aspect method named by [reference], declared in the aspect or inherited by it */
    @JvmStatic
    fun resolveMethod(reference: PointcutReference, host: PsiElement? = null): Method? {
        val classFqn = aspectClassFqn(reference, host) ?: return null
        val name = reference.namePatternPart.text
        val phpClass = PhpIndex.getInstance(reference.project).getClassesByFQN(classFqn).firstOrNull() ?: return null
        return phpClass.findMethodByName(name)
    }

    /** True when [method] declares a named pointcut with `#[Pointcut]` */
    @JvmStatic
    fun isNamedPointcut(method: Method): Boolean = method.getAttributes(GoAopAttributes.POINTCUT).isNotEmpty()
}
