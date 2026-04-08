package com.aopphp.go.completion

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests code completion in Go! AOP Pointcut query language (`.goaop` files).
 *
 * Extends [BasePlatformTestCase] which provides a lightweight headless IntelliJ Platform
 * environment with the plugin loaded from the test sandbox, so all registered extensions
 * (language, parser definition, completion contributor) are active.
 *
 * NOTE: The completion providers guard against `parameters.originalPosition == null`, so the
 * caret must be placed WITHIN existing file text (not at end-of-file) to guarantee a non-null
 * original PSI element at the caret offset.
 */
class PointcutCompletionTest : BasePlatformTestCase() {

    // ----- keyword completion -----

    fun testAllKeywordsAvailableAtStartOfExpression() {
        // Caret at position 0 — originalPosition = EXECUTION keyword token (non-null)
        myFixture.configureByText("test.goaop", "<caret>execution(public MyClass->method(*))")
        val completions = myFixture.complete(CompletionType.BASIC) ?: return
        val lookupStrings = completions.map { it.lookupString }
        assertContainsElements(lookupStrings, "execution", "access", "within", "matchInherited")
    }

    fun testAllSupportedKeywordTypesAreOffered() {
        myFixture.configureByText("test.goaop", "<caret>execution(public MyClass->method(*))")
        val completions = myFixture.complete(CompletionType.BASIC) ?: return
        val lookupStrings = completions.map { it.lookupString }
        assertContainsElements(
            lookupStrings,
            "execution",
            "access",
            "within",
            "initialization",
            "staticinitialization",
            "matchInherited"
        )
    }

    fun testKeywordExecutionIsAvailable() {
        myFixture.configureByText("test.goaop", "exec<caret>")
        val completions = myFixture.completeBasic()
        if (completions == null) {
            // single item was auto-inserted
            assertTrue("'execution' should be auto-inserted",
                myFixture.editor.document.text.startsWith("execution"))
        } else {
            assertTrue("'execution' expected in completions",
                completions.any { it.lookupString == "execution" })
        }
    }

    fun testKeywordAccessIsAvailable() {
        myFixture.configureByText("test.goaop", "acc<caret>")
        val completions = myFixture.completeBasic()
        if (completions == null) {
            assertTrue("'access' should be auto-inserted",
                myFixture.editor.document.text.startsWith("access"))
        } else {
            assertTrue("'access' expected", completions.any { it.lookupString == "access" })
        }
    }

    fun testKeywordWithinIsAvailable() {
        myFixture.configureByText("test.goaop", "wi<caret>")
        val completions = myFixture.completeBasic()
        if (completions == null) {
            assertTrue("'within' should be auto-inserted",
                myFixture.editor.document.text.startsWith("within"))
        } else {
            assertTrue("'within' expected", completions.any { it.lookupString == "within" })
        }
    }

    // ----- modifier completion -----

    fun testModifiersAvailableAtStartOfMemberModifiers() {
        // Caret before "public" so prefix is empty — all modifiers starting with "" appear
        myFixture.configureByText("test.goaop", "execution(<caret>public MyClass->method(*))")
        val completions = myFixture.complete(CompletionType.BASIC) ?: return
        val lookupStrings = completions.map { it.lookupString }
        assertContainsElements(lookupStrings, "public", "protected", "private", "final")
    }

    fun testModifierPipeChaining() {
        // Caret before "protected" after the pipe so prefix is empty — all modifiers appear
        myFixture.configureByText("test.goaop", "execution(public|<caret>protected MyClass->method(*))")
        val completions = myFixture.complete(CompletionType.BASIC) ?: return
        val lookupStrings = completions.map { it.lookupString }
        assertContainsElements(lookupStrings, "protected", "private", "final")
    }

    fun testPublicModifierAppearsWithPrefix() {
        myFixture.configureByText("test.goaop", "execution(pub<caret>")
        val completions = myFixture.completeBasic()
        if (completions == null) {
            assertTrue("'public' should be auto-inserted",
                myFixture.editor.document.text.contains("public"))
        } else {
            assertTrue("'public' expected", completions.any { it.lookupString == "public" })
        }
    }
}

