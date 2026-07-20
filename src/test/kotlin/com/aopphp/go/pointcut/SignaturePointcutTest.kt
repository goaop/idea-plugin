package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SignaturePointcutTest {

    private val allKinds = KindFilter.entries.toSet()

    // ---- Exact name matching ----

    @Test
    fun `matches Method by exact name`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "doSomething", TruePointFilter)
        val method = mockMember("doSomething")
        assertTrue(pointcut.matches(method))
    }

    @Test
    fun `does not match Method with different name`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "doSomething", TruePointFilter)
        val method = mockMember("otherMethod")
        assertFalse(pointcut.matches(method))
    }

    @Test
    fun `matches PhpClass by FQN without leading backslash`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_CLASS), "App\\MyClass", TruePointFilter)
        val cls = mockClass("\\App\\MyClass")
        assertTrue(pointcut.matches(cls))
    }

    @Test
    fun `does not match PhpClass with different FQN`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_CLASS), "App\\MyClass", TruePointFilter)
        val cls = mockClass("\\Other\\MyClass")
        assertFalse(pointcut.matches(cls))
    }

    @Test
    fun `does not match non-member non-class element`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "something", TruePointFilter)
        val element = mock<PhpNamedElement>()
        // element is neither Method, Field nor PhpClass -> rejected by the kind gate
        assertFalse(pointcut.matches(element))
    }

    @Test
    fun `does not match unsupported element kind even when name pattern is empty`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "", TruePointFilter)
        val element = mock<PhpNamedElement>()
        // the kind gate rejects the element before any name matching happens
        assertFalse(pointcut.matches(element))
    }

    // ---- Kind gate ----

    @Test
    fun `does not match Field when kind is KIND_METHOD`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "get*", TruePointFilter)
        val field = mock<Field>()
        whenever(field.name).thenReturn("getUser")
        assertFalse(pointcut.matches(field))
    }

    @Test
    fun `does not match Method when kind is KIND_PROPERTY`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_PROPERTY), "get*", TruePointFilter)
        val method = mock<Method>()
        whenever(method.name).thenReturn("getUser")
        assertFalse(pointcut.matches(method))
    }

    @Test
    fun `matches Field by name when kind is KIND_PROPERTY`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_PROPERTY), "get*", TruePointFilter)
        val field = mock<Field>()
        whenever(field.name).thenReturn("getUser")
        assertTrue(pointcut.matches(field))
    }

    @Test
    fun `does not match PhpClass when kind excludes KIND_CLASS`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "App\\MyClass", TruePointFilter)
        assertFalse(pointcut.matches(mockClass("\\App\\MyClass")))
    }

    // ---- Wildcard matching ----

    @Test
    fun `matches method name with trailing star wildcard`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "get*", TruePointFilter)
        assertTrue(pointcut.matches(mockMember("getUser")))
        assertTrue(pointcut.matches(mockMember("getProduct")))
        assertFalse(pointcut.matches(mockMember("setUser")))
    }

    @Test
    fun `matches method name with leading star wildcard`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "*Service", TruePointFilter)
        assertTrue(pointcut.matches(mockMember("UserService")))
        assertTrue(pointcut.matches(mockMember("ProductService")))
        assertFalse(pointcut.matches(mockMember("UserRepository")))
    }

    @Test
    fun `star wildcard does not cross namespace separators`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_CLASS), "App\\*", TruePointFilter)
        // Direct child matches (no nested namespace)
        assertTrue(pointcut.matches(mockClass("\\App\\Service")))
        // But double-backslash still matches because the * regex is [^\\]+?
        assertFalse(pointcut.matches(mockClass("\\App\\Service\\Deep")))
    }

    @Test
    fun `double star wildcard crosses namespace separators`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_CLASS), "App\\**", TruePointFilter)
        assertTrue(pointcut.matches(mockClass("\\App\\Service\\MyService")))
        assertTrue(pointcut.matches(mockClass("\\App\\Controller\\UserController")))
    }

    @Test
    fun `question mark wildcard matches single character`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "?etUser", TruePointFilter)
        assertTrue(pointcut.matches(mockMember("getUser")))
        assertTrue(pointcut.matches(mockMember("setUser")))
        assertFalse(pointcut.matches(mockMember("fetchUser")))
    }

    @Test
    fun `pipe alternation matches either option`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "getUser|setUser", TruePointFilter)
        assertTrue(pointcut.matches(mockMember("getUser")))
        assertTrue(pointcut.matches(mockMember("setUser")))
        assertFalse(pointcut.matches(mockMember("deleteUser")))
    }

    // ---- Modifier filter ----

    @Test
    fun `returns false when modifier filter rejects the member`() {
        val modFilter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "doSomething", modFilter)
        val method = mock<Method>()
        whenever(method.name).thenReturn("doSomething")
        whenever(method.modifier).thenReturn(PhpModifier.PRIVATE_IMPLEMENTED_DYNAMIC)
        assertFalse(pointcut.matches(method))
    }

    @Test
    fun `returns true when modifier filter accepts the member`() {
        val modFilter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "doSomething", modFilter)
        val method = mock<Method>()
        whenever(method.name).thenReturn("doSomething")
        whenever(method.modifier).thenReturn(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC)
        assertTrue(pointcut.matches(method))
    }

    @Test
    fun `modifier filter is not applied to PhpClass elements`() {
        val modFilter = MemberAccessMatcherFilter(setOf(PhpModifier.Access.PUBLIC))
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_CLASS), "App\\MyClass", modFilter)
        val cls = mockClass("\\App\\MyClass")
        // PhpClass is not a PhpClassMember, so modifier filter is skipped
        assertTrue(pointcut.matches(cls))
    }

    // ---- getKind ----

    @Test
    fun `getKind returns the provided kind set`() {
        val kinds = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_PROPERTY)
        val pointcut = SignaturePointcut(kinds, "foo", TruePointFilter)
        assertEquals(kinds, pointcut.getKind())
    }

    // ---- classFilter ----

    @Test
    fun `classFilter can be set and retrieved`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "foo", TruePointFilter)
        val classFilter = mock<PointFilter>()
        pointcut._classFilter = classFilter
        assertSame(classFilter, pointcut.getClassFilter())
    }

    // ---- equals / hashCode ----

    @Test
    fun `two pointcuts with same values are equal`() {
        val a = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "doSomething", TruePointFilter)
        val b = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "doSomething", TruePointFilter)
        a._classFilter = TruePointFilter
        b._classFilter = TruePointFilter
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `two pointcuts with different names are not equal`() {
        val a = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "foo", TruePointFilter)
        val b = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "bar", TruePointFilter)
        a._classFilter = TruePointFilter
        b._classFilter = TruePointFilter
        assertNotEquals(a, b)
    }

    @Test
    fun `not equal to non-SignaturePointcut`() {
        val pointcut = SignaturePointcut(setOf(KindFilter.KIND_METHOD), "foo", TruePointFilter)
        pointcut._classFilter = TruePointFilter
        assertNotEquals(pointcut, TruePointFilter)
    }

    // ---- Helpers ----

    private fun mockMember(name: String): PhpClassMember {
        val m = mock<Method>()
        whenever(m.name).thenReturn(name)
        return m
    }

    private fun mockClass(fqn: String): PhpClass {
        val cls = mock<PhpClass>()
        whenever(cls.fqn).thenReturn(fqn)
        return cls
    }
}
