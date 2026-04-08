package com.aopphp.go.pointcut

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class InheritanceClassFilterTest {

    // ---- Non-PhpIndex paths ----

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
        // Filter normalizes "App\MyClass" → "\App\MyClass" for comparison
        val filter = InheritanceClassFilter("App\\MyClass")
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\MyClass")
        assertTrue(filter.matches(cls))
    }

    // ---- PhpIndex-dependent paths (subclass lookup) ----

    @Test
    fun `matches returns true when class is a subclass via PhpIndex`() {
        val project = mock<Project>()
        val phpIndex = mock<PhpIndex>()
        val childFqn = "\\App\\Child"
        val child = mock<PhpClass>()
        whenever(child.fqn).thenReturn(childFqn)
        whenever(phpIndex.getAllSubclasses("\\App\\Base")).thenReturn(listOf(child))

        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn(childFqn)
        whenever(cls.project).thenReturn(project)

        mockStatic(PhpIndex::class.java).use { mocked ->
            mocked.`when`<PhpIndex> { PhpIndex.getInstance(project) }.thenReturn(phpIndex)
            val filter = InheritanceClassFilter("App\\Base")
            assertTrue(filter.matches(cls))
        }
    }

    @Test
    fun `matches returns false when class is not in PhpIndex subclasses`() {
        val project = mock<Project>()
        val phpIndex = mock<PhpIndex>()
        val child = mock<PhpClass>()
        whenever(child.fqn).thenReturn("\\App\\Child")
        whenever(phpIndex.getAllSubclasses("\\App\\Base")).thenReturn(listOf(child))

        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\Unrelated")
        whenever(cls.project).thenReturn(project)

        mockStatic(PhpIndex::class.java).use { mocked ->
            mocked.`when`<PhpIndex> { PhpIndex.getInstance(project) }.thenReturn(phpIndex)
            val filter = InheritanceClassFilter("App\\Base")
            assertFalse(filter.matches(cls))
        }
    }

    @Test
    fun `matches returns false when PhpIndex returns empty subclasses`() {
        val project = mock<Project>()
        val phpIndex = mock<PhpIndex>()
        whenever(phpIndex.getAllSubclasses("\\App\\Base")).thenReturn(emptyList())

        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn("\\App\\SomeClass")
        whenever(cls.project).thenReturn(project)

        mockStatic(PhpIndex::class.java).use { mocked ->
            mocked.`when`<PhpIndex> { PhpIndex.getInstance(project) }.thenReturn(phpIndex)
            val filter = InheritanceClassFilter("App\\Base")
            assertFalse(filter.matches(cls))
        }
    }

    // ---- getKind ----

    @Test
    fun `getKind returns only KIND_CLASS`() {
        val filter = InheritanceClassFilter("App\\MyClass")
        assertEquals(setOf(KindFilter.KIND_CLASS), filter.getKind())
    }

    // ---- equals / hashCode ----

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

