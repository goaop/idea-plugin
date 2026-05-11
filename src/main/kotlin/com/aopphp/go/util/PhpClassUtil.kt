package com.aopphp.go.util

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass

/**
 * Utility helpers for PHP class resolution inside Go! AOP pointcuts.
 */
object PhpClassUtil {

    private const val AOP_PROXY_INTERFACE = "\\Go\\Aop\\Proxy"

    /**
     * Returns true if [cls] is a Go! AOP-generated proxy class that should be excluded from
     * pointcut completion and navigation.
     *
     * Two detection strategies:
     *  1. FQN contains "__AopProx" — matches generated trait names like `Foo__AopProxied`.
     *  2. The class directly implements `\Go\Aop\Proxy` — matches runtime proxy classes that
     *     PhpStorm may index from the framework's cache folder with the same FQN as the original.
     */
    @JvmStatic
    fun isAopProxy(cls: PhpClass): Boolean {
        val fqn = cls.fqn ?: return false
        if (fqn.contains("__AopProx")) return true
        return cls.interfaceNames.any { it == AOP_PROXY_INTERFACE }
    }

    /**
     * Resolves a PHP class/interface/trait by [fqn], skipping any AOP proxy variants.
     * Returns the first non-proxy match, or null if none found.
     */
    @JvmStatic
    fun resolveNonProxyClass(fqn: String, phpIndex: PhpIndex): PhpClass? {
        val normalizedFqn = if (fqn.startsWith('\\')) fqn else "\\$fqn"
        return (phpIndex.getClassesByFQN(normalizedFqn) +
                phpIndex.getInterfacesByFQN(normalizedFqn) +
                phpIndex.getTraitsByFQN(normalizedFqn))
            .firstOrNull { !isAopProxy(it) }
    }
}
