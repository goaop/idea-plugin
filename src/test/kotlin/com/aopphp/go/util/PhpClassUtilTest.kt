package com.aopphp.go.util

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PhpClassUtilTest {

    // ---- isAopProxy ----

    @Test
    fun `isAopProxy returns false when FQN is null`() {
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn(null)
        assertFalse(PhpClassUtil.isAopProxy(cls))
    }

    @Test
    fun `isAopProxy returns true when FQN contains __AopProx`() {
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\Foo__AopProxied")
        whenever(cls.interfaceNames).thenReturn(emptyArray())
        assertTrue(PhpClassUtil.isAopProxy(cls))
    }

    @Test
    fun `isAopProxy returns true when class implements Go AOP Proxy interface`() {
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        whenever(cls.interfaceNames).thenReturn(arrayOf("\\Go\\Aop\\Proxy"))
        assertTrue(PhpClassUtil.isAopProxy(cls))
    }

    @Test
    fun `isAopProxy returns false for normal class without proxy markers`() {
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\Service\\UserService")
        whenever(cls.interfaceNames).thenReturn(arrayOf("\\Some\\Interface"))
        assertFalse(PhpClassUtil.isAopProxy(cls))
    }

    @Test
    fun `isAopProxy returns false for class with empty interface list and normal FQN`() {
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\Controller\\HomeController")
        whenever(cls.interfaceNames).thenReturn(emptyArray())
        assertFalse(PhpClassUtil.isAopProxy(cls))
    }

    @Test
    fun `isAopProxy returns true when FQN contains __AopProx anywhere`() {
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\Namespace__AopProxFoo\\Bar")
        whenever(cls.interfaceNames).thenReturn(emptyArray())
        assertTrue(PhpClassUtil.isAopProxy(cls))
    }

    @Test
    fun `isAopProxy returns false when Proxy interface name differs slightly`() {
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        whenever(cls.interfaceNames).thenReturn(arrayOf("\\Go\\Aop\\ProxyInterface"))
        assertFalse(PhpClassUtil.isAopProxy(cls))
    }

    // ---- resolveNonProxyClass ----

    @Test
    fun `resolveNonProxyClass returns null when PhpIndex finds nothing`() {
        val phpIndex = mock<PhpIndex>()
        whenever(phpIndex.getClassesByFQN("\\App\\MyClass")).thenReturn(emptyList())
        whenever(phpIndex.getInterfacesByFQN("\\App\\MyClass")).thenReturn(emptyList())
        whenever(phpIndex.getTraitsByFQN("\\App\\MyClass")).thenReturn(emptyList())
        assertNull(PhpClassUtil.resolveNonProxyClass("App\\MyClass", phpIndex))
    }

    @Test
    fun `resolveNonProxyClass normalizes FQN by adding leading backslash`() {
        val phpIndex = mock<PhpIndex>()
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        whenever(cls.interfaceNames).thenReturn(emptyArray())
        whenever(phpIndex.getClassesByFQN("\\App\\MyClass")).thenReturn(listOf(cls))
        whenever(phpIndex.getInterfacesByFQN("\\App\\MyClass")).thenReturn(emptyList())
        whenever(phpIndex.getTraitsByFQN("\\App\\MyClass")).thenReturn(emptyList())
        val result = PhpClassUtil.resolveNonProxyClass("App\\MyClass", phpIndex)
        assertSame(cls, result)
    }

    @Test
    fun `resolveNonProxyClass does not add double leading backslash when already present`() {
        val phpIndex = mock<PhpIndex>()
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        whenever(cls.interfaceNames).thenReturn(emptyArray())
        whenever(phpIndex.getClassesByFQN("\\App\\MyClass")).thenReturn(listOf(cls))
        whenever(phpIndex.getInterfacesByFQN("\\App\\MyClass")).thenReturn(emptyList())
        whenever(phpIndex.getTraitsByFQN("\\App\\MyClass")).thenReturn(emptyList())
        val result = PhpClassUtil.resolveNonProxyClass("\\App\\MyClass", phpIndex)
        assertSame(cls, result)
    }

    @Test
    fun `resolveNonProxyClass skips proxy classes`() {
        val phpIndex = mock<PhpIndex>()
        val proxyClass = mock<PhpClass>()
        whenever(proxyClass.fqn).thenReturn("\\App\\MyClass__AopProxied")
        whenever(proxyClass.interfaceNames).thenReturn(emptyArray())
        val normalClass = mock<PhpClass>()
        whenever(normalClass.fqn).thenReturn("\\App\\MyClass")
        whenever(normalClass.interfaceNames).thenReturn(emptyArray())
        whenever(phpIndex.getClassesByFQN("\\App\\MyClass")).thenReturn(listOf(proxyClass, normalClass))
        whenever(phpIndex.getInterfacesByFQN("\\App\\MyClass")).thenReturn(emptyList())
        whenever(phpIndex.getTraitsByFQN("\\App\\MyClass")).thenReturn(emptyList())
        val result = PhpClassUtil.resolveNonProxyClass("\\App\\MyClass", phpIndex)
        assertSame(normalClass, result)
    }

    @Test
    fun `resolveNonProxyClass falls back to interfaces when no class found`() {
        val phpIndex = mock<PhpIndex>()
        val iface = mock<PhpClass>()
        whenever(iface.fqn).thenReturn("\\App\\MyInterface")
        whenever(iface.interfaceNames).thenReturn(emptyArray())
        whenever(phpIndex.getClassesByFQN("\\App\\MyInterface")).thenReturn(emptyList())
        whenever(phpIndex.getInterfacesByFQN("\\App\\MyInterface")).thenReturn(listOf(iface))
        whenever(phpIndex.getTraitsByFQN("\\App\\MyInterface")).thenReturn(emptyList())
        val result = PhpClassUtil.resolveNonProxyClass("\\App\\MyInterface", phpIndex)
        assertSame(iface, result)
    }

    @Test
    fun `resolveNonProxyClass falls back to traits when no class or interface found`() {
        val phpIndex = mock<PhpIndex>()
        val trait = mock<PhpClass>()
        whenever(trait.fqn).thenReturn("\\App\\MyTrait")
        whenever(trait.interfaceNames).thenReturn(emptyArray())
        whenever(phpIndex.getClassesByFQN("\\App\\MyTrait")).thenReturn(emptyList())
        whenever(phpIndex.getInterfacesByFQN("\\App\\MyTrait")).thenReturn(emptyList())
        whenever(phpIndex.getTraitsByFQN("\\App\\MyTrait")).thenReturn(listOf(trait))
        val result = PhpClassUtil.resolveNonProxyClass("\\App\\MyTrait", phpIndex)
        assertSame(trait, result)
    }
}
