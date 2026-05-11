package com.aopphp.go.util

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpClass
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class PluginUtilTest {

    @Test
    fun `isAspect returns false for null element`() {
        assertFalse(PluginUtil.isAspect(null))
    }

    @Test
    fun `isAspect returns false for non-PhpClass element`() {
        val element = mock<PsiElement>()
        assertFalse(PluginUtil.isAspect(element))
    }

    @Test
    fun `isAspect returns false for PhpClass without Aspect interface`() {
        val cls = mock<PhpClass>()
        whenever(cls.interfaceNames).thenReturn(arrayOf("\\Some\\OtherInterface"))
        assertFalse(PluginUtil.isAspect(cls))
    }

    @Test
    fun `isAspect returns false for PhpClass with empty interface list`() {
        val cls = mock<PhpClass>()
        whenever(cls.interfaceNames).thenReturn(emptyArray())
        assertFalse(PluginUtil.isAspect(cls))
    }

    @Test
    fun `isAspect returns true for PhpClass implementing Go AOP Aspect`() {
        val cls = mock<PhpClass>()
        whenever(cls.interfaceNames).thenReturn(arrayOf("\\Go\\Aop\\Aspect"))
        assertTrue(PluginUtil.isAspect(cls))
    }

    @Test
    fun `isAspect returns true when Aspect is among multiple interfaces`() {
        val cls = mock<PhpClass>()
        whenever(cls.interfaceNames).thenReturn(
            arrayOf("\\Some\\Interface", "\\Go\\Aop\\Aspect", "\\Another\\Interface")
        )
        assertTrue(PluginUtil.isAspect(cls))
    }

    @Test
    fun `isAspect is case-sensitive for interface name`() {
        val cls = mock<PhpClass>()
        whenever(cls.interfaceNames).thenReturn(arrayOf("\\go\\aop\\aspect"))
        assertFalse(PluginUtil.isAspect(cls))
    }
}
