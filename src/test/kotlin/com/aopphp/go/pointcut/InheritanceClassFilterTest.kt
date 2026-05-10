package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class InheritanceClassFilterTest {

    @Test
    fun `matches returns false for non-PhpClass element`() {
        val filter = InheritanceClassFilter("App\\MyClass")
        val element = mock<PhpNamedElement>()
        assertFalse(filter.matches(element))
    }

    @Test
    fun `matches returns false when PhpClass FQN is null`() {
        val filter = InheritanceClassFilter("App\\MyClass")
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn(null)
        assertFalse(filter.matches(cls))
    }

    @Test
    fun `matches returns true for exact FQN match with leading backslash`() {
        val filter = InheritanceClassFilter("\\App\\MyClass")
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        assertTrue(filter.matches(cls))
    }

    @Test
    fun `matches returns true for exact FQN match when parentClassName has no leading backslash`() {
        val filter = InheritanceClassFilter("App\\MyClass")
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        assertTrue(filter.matches(cls))
    }

    @Test
    fun `matches returns true when class is a subclass via hierarchy`() {
        val parent = mock<PhpClass>()
        whenever(parent.fqn).thenReturn("\\App\\Base")

        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\Child")
        whenever(cls.superClass).thenReturn(parent)
        whenever(parent.superClass).thenReturn(null)
        whenever(cls.implementedInterfaces).thenReturn(emptyArray<PhpClass>())

        val filter = InheritanceClassFilter("App\\Base")
        assertTrue(filter.matches(cls))
    }

    @Test
    fun `matches returns true when class implements the target interface`() {
        val iface = mock<PhpClass>()
        whenever(iface.fqn).thenReturn("\\App\\TargetInterface")

        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\SomeClass")
        whenever(cls.superClass).thenReturn(null)
        whenever(cls.implementedInterfaces).thenReturn(arrayOf(iface))

        val filter = InheritanceClassFilter("App\\TargetInterface")
        assertTrue(filter.matches(cls))
    }

    @Test
    fun `matches returns false when class is not in hierarchy`() {
        val unrelated = mock<PhpClass>()
        whenever(unrelated.fqn).thenReturn("\\App\\Unrelated")

        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\Unrelated")
        whenever(cls.superClass).thenReturn(null)
        whenever(cls.implementedInterfaces).thenReturn(emptyArray<PhpClass>())

        val filter = InheritanceClassFilter("App\\Base")
        assertFalse(filter.matches(cls))
    }

    @Test
    fun `matches returns false when class has different parent`() {
        val otherParent = mock<PhpClass>()
        whenever(otherParent.fqn).thenReturn("\\App\\OtherBase")

        val cls = mock<PhpClass>()
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
}

