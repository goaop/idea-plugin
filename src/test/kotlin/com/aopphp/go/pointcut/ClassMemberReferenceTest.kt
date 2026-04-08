package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpModifier
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class ClassMemberReferenceTest {

    private val classFilter = mock<PointFilter>()
    private val visibilityFilter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
    private val accessTypeFilter = MemberStateMatcherFilter(PhpModifier.State.DYNAMIC)

    @Test
    fun `data class stores all fields correctly`() {
        val ref = ClassMemberReference(classFilter, visibilityFilter, accessTypeFilter, "myMethod")
        assertSame(classFilter, ref.classFilter)
        assertSame(visibilityFilter, ref.visibilityFilter)
        assertSame(accessTypeFilter, ref.accessTypeFilter)
        assertEquals("myMethod", ref.memberNamePattern)
    }

    @Test
    fun `two references with same values are equal`() {
        val a = ClassMemberReference(classFilter, visibilityFilter, accessTypeFilter, "foo")
        val b = ClassMemberReference(classFilter, visibilityFilter, accessTypeFilter, "foo")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `references with different member names are not equal`() {
        val a = ClassMemberReference(classFilter, visibilityFilter, accessTypeFilter, "foo")
        val b = ClassMemberReference(classFilter, visibilityFilter, accessTypeFilter, "bar")
        assertNotEquals(a, b)
    }

    @Test
    fun `toString contains the member name pattern`() {
        val ref = ClassMemberReference(classFilter, visibilityFilter, accessTypeFilter, "doSomething")
        assertTrue(ref.toString().contains("doSomething"))
    }

    @Test
    fun `copy creates a new reference with modified field`() {
        val original = ClassMemberReference(classFilter, visibilityFilter, accessTypeFilter, "original")
        val copied = original.copy(memberNamePattern = "copied")
        assertEquals("copied", copied.memberNamePattern)
        assertSame(classFilter, copied.classFilter)
    }
}
