package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class InheritanceClassFilterTest {

    /** A class mock without traits and interfaces: PhpStorm never returns null arrays */
    private fun phpClass(): PhpClass = mock<PhpClass>().also {
        whenever(it.traitNames).thenReturn(emptyArray())
        whenever(it.implementedInterfaces).thenReturn(emptyArray())
    }

    @Test
    fun `matches returns false for non-PhpClass element`() {
        val filter = InheritanceClassFilter("App\\MyClass")
        val element = mock<PhpNamedElement>()
        assertFalse(filter.matches(element))
    }

    @Test
    fun `matches returns true for exact FQN match with leading backslash`() {
        val filter = InheritanceClassFilter("\\App\\MyClass")
        val cls = phpClass()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        assertTrue(filter.matches(cls))
    }

    @Test
    fun `matches returns true for exact FQN match when parentClassName has no leading backslash`() {
        val filter = InheritanceClassFilter("App\\MyClass")
        val cls = phpClass()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        assertTrue(filter.matches(cls))
    }

    @Test
    fun `matches returns true when class is a subclass via hierarchy`() {
        val parent = phpClass()
        whenever(parent.fqn).thenReturn("\\App\\Base")

        val cls = phpClass()
        whenever(cls.fqn).thenReturn("\\App\\Child")
        whenever(cls.superClass).thenReturn(parent)
        whenever(parent.superClass).thenReturn(null)
        whenever(cls.implementedInterfaces).thenReturn(emptyArray<PhpClass>())

        val filter = InheritanceClassFilter("App\\Base")
        assertTrue(filter.matches(cls))
    }

    @Test
    fun `matches returns true when class implements the target interface`() {
        val iface = phpClass()
        whenever(iface.fqn).thenReturn("\\App\\TargetInterface")

        val cls = phpClass()
        whenever(cls.fqn).thenReturn("\\App\\SomeClass")
        whenever(cls.superClass).thenReturn(null)
        whenever(cls.implementedInterfaces).thenReturn(arrayOf(iface))

        val filter = InheritanceClassFilter("App\\TargetInterface")
        assertTrue(filter.matches(cls))
    }

    @Test
    fun `matches returns false when class is not in hierarchy`() {
        val unrelated = phpClass()
        whenever(unrelated.fqn).thenReturn("\\App\\Unrelated")

        val cls = phpClass()
        whenever(cls.fqn).thenReturn("\\App\\Unrelated")
        whenever(cls.superClass).thenReturn(null)
        whenever(cls.implementedInterfaces).thenReturn(emptyArray<PhpClass>())

        val filter = InheritanceClassFilter("App\\Base")
        assertFalse(filter.matches(cls))
    }

    @Test
    fun `matches returns false when class has different parent`() {
        val otherParent = phpClass()
        whenever(otherParent.fqn).thenReturn("\\App\\OtherBase")

        val cls = phpClass()
        whenever(cls.fqn).thenReturn("\\App\\Child")
        whenever(cls.superClass).thenReturn(otherParent)
        whenever(otherParent.superClass).thenReturn(null)
        whenever(cls.implementedInterfaces).thenReturn(emptyArray<PhpClass>())

        val filter = InheritanceClassFilter("App\\Base")
        assertFalse(filter.matches(cls))
    }

    @Test
    fun `getKind returns only KIND_CLASS`() {
        val filter = InheritanceClassFilter("App\\MyClass")
        assertEquals(setOf(KindFilter.KIND_CLASS), filter.getKind())
    }

    @Test
    fun `two filters with same class name are equal`() {
        val a = InheritanceClassFilter("App\\MyClass")
        val b = InheritanceClassFilter("App\\MyClass")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two filters with different class names are not equal`() {
        val a = InheritanceClassFilter("App\\MyClass")
        val b = InheritanceClassFilter("App\\OtherClass")
        assertNotEquals(a, b)
    }

    @Test
    fun `not equal to null or non-InheritanceClassFilter`() {
        val filter = InheritanceClassFilter("App\\MyClass")
        assertNotEquals(filter, null)
        assertNotEquals(filter, "App\\MyClass")
    }

    @Test
    fun `matches classes using the trait directly or through a parent class`() {
        val parent = phpClass()
        whenever(parent.fqn).thenReturn("\\App\\Base")
        whenever(parent.traitNames).thenReturn(arrayOf("\\App\\LoggerTrait"))

        val cls = phpClass()
        whenever(cls.fqn).thenReturn("\\App\\Child")
        whenever(cls.superClass).thenReturn(parent)

        assertTrue(InheritanceClassFilter("App\\LoggerTrait").matches(cls))
        assertTrue(InheritanceClassFilter("App\\LoggerTrait").matches(parent))
        assertFalse(InheritanceClassFilter("App\\OtherTrait").matches(cls))
    }

    @Test
    fun `matches interfaces inherited through parent interfaces`() {
        val baseInterface = phpClass()
        whenever(baseInterface.fqn).thenReturn("\\App\\Base")
        val childInterface = phpClass()
        whenever(childInterface.fqn).thenReturn("\\App\\Child")
        whenever(childInterface.implementedInterfaces).thenReturn(arrayOf(baseInterface))

        val cls = phpClass()
        whenever(cls.fqn).thenReturn("\\App\\Service")
        whenever(cls.implementedInterfaces).thenReturn(arrayOf(childInterface))

        assertTrue(InheritanceClassFilter("App\\Base").matches(cls))
    }
}
