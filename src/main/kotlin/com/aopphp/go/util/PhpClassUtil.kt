package com.aopphp.go.util

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass

/**
 * Utility helpers for PHP class resolution inside Go! AOP pointcuts.
 */
object PhpClassUtil {

    private const val AOP_PROXY_INTERFACE = "\\Go\\Aop\\Proxy"
    private const val ORIGINAL_TRAIT_SUFFIX = "OriginalTrait"

    /**
     * Returns true if [cls] is a Go! AOP-generated proxy class that should be excluded from
     * pointcut completion and navigation.
     *
     * Detection strategies:
     *  1. The class directly implements `\Go\Aop\Proxy` — matches the proxy classes that PhpStorm
     *     may index from the framework's cache folder with the same FQN as the original.
     *  2. A trait whose name ends with `OriginalTrait` — the original class body that Go! AOP 4.0
     *     moves into `<Class>OriginalTrait` next to the proxy in the cache folder.
     *  3. FQN contains "__AopProx" — the `Foo__AopProxied` parent classes of Go! AOP 3.x caches.
     */
    @JvmStatic
    fun isAopProxy(cls: PhpClass): Boolean {
        val fqn: String = cls.fqn
        if (fqn.contains("__AopProx")) return true
        if (fqn.endsWith(ORIGINAL_TRAIT_SUFFIX) && cls.isTrait) return true
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
