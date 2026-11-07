package com.aopphp.go.pointcut

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Name pattern semantics of Go! AOP 4.0, see `docs/pointcuts.md` of the framework.
 */
class NamePatternsTest {

    private fun matches(pattern: String, name: String) =
        NamePatterns.matches(pattern, NamePatterns.compile(pattern), name)

    @Test
    fun `star matches any characters of one name part including none`() {
        assertTrue(matches("get*", "get"))
        assertTrue(matches("get*", "getName"))
        assertTrue(matches("*Service", "Service"))
        assertFalse(matches("App\\*", "App\\Sub\\Service"))
    }

    @Test
    fun `star in the middle of a namespace matches exactly one level`() {
        assertTrue(matches("App\\*\\Controller", "App\\Admin\\Controller"))
        assertFalse(matches("App\\*\\Controller", "App\\Admin\\Sub\\Controller"))
    }

    @Test
    fun `double star matches any namespace depth`() {
        assertTrue(matches("App\\**", "App\\Service"))
        assertTrue(matches("App\\**", "App\\Admin\\Sub\\Controller"))
        assertFalse(matches("App\\**", "Other\\Service"))
    }

    @Test
    fun `alternatives stay inside their name part`() {
        assertTrue(matches("App\\Cart|Order", "App\\Cart"))
        assertTrue(matches("App\\Cart|Order", "App\\Order"))
        assertFalse(matches("App\\Cart|Order", "Order"))
        assertTrue(matches("get*|is*", "isEnabled"))
        assertFalse(matches("get*|is*", "hasName"))
    }

    @Test
    fun `question mark is a literal character`() {
        assertFalse(matches("?etName", "getName"))
    }

    @Test
    fun `matching is case-sensitive`() {
        assertFalse(matches("getName", "getname"))
    }
}
