package com.aopphp.go.pointcut

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Return-type pattern semantics of Go! AOP 4.0 (`ReturnTypePointcut`).
 */
class ReturnTypeFilterTest {

    @Test
    fun `single type pattern matches any member of the declared type`() {
        val filter = ReturnTypeFilter("App\\Entity")
        assertTrue(filter.matchesType("App\\Entity"))
        assertTrue(filter.matchesType("?App\\Entity"))
        assertTrue(filter.matchesType("App\\Entity|null"))
        assertFalse(filter.matchesType("App\\Other"))
    }

    @Test
    fun `nullable marker equals a union with null`() {
        val filter = ReturnTypeFilter("?App\\Entity")
        assertTrue(filter.matchesType("App\\Entity|null"))
        assertTrue(filter.matchesType("?App\\Entity"))
        assertFalse(filter.matchesType("App\\Entity"))
    }

    @Test
    fun `union pattern must correspond one to one`() {
        val filter = ReturnTypeFilter("string|int")
        assertTrue(filter.matchesType("int|string"))
        assertFalse(filter.matchesType("string"))
        assertFalse(filter.matchesType("string|int|null"))
    }

    @Test
    fun `intersection and DNF patterns are normalized`() {
        val filter = ReturnTypeFilter("Iterator|Countable&Iterator|null")
        assertTrue(filter.matchesType("(Iterator&Countable)|Iterator|null"))
        assertTrue(ReturnTypeFilter("Countable&Iterator").matchesType("Iterator&Countable"))
        assertFalse(ReturnTypeFilter("Countable&Iterator").matchesType("Iterator"))
    }

    @Test
    fun `leading backslashes are ignored`() {
        assertTrue(ReturnTypeFilter("App\\Entity").matchesType("\\App\\Entity"))
    }

    @Test
    fun `normalizeTypeExpression sorts groups and atoms`() {
        assertEquals(
            listOf(listOf("A", "B"), listOf("C"), listOf("null")),
            ReturnTypeFilter.normalizeTypeExpression("C|(B&A)|null")
        )
        assertEquals(listOf(listOf("Foo"), listOf("null")), ReturnTypeFilter.normalizeTypeExpression("?Foo"))
    }
}
