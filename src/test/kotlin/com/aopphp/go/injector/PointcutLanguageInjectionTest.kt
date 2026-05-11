package com.aopphp.go.injector

import com.aopphp.go.PointcutQueryLanguage
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * Tests that the Go! AOP Pointcut query language is correctly injected into PHP string literals
 * that are arguments of `#[\Go\Lang\Attribute\*]` PHP 8 attributes.
 *
 * Extends [BasePlatformTestCase] which provides a lightweight headless IntelliJ Platform
 * environment. Since we target PhpStorm, the PHP plugin is available in the test sandbox.
 */
class PointcutLanguageInjectionTest : BasePlatformTestCase() {

    fun testPointcutLanguageInjectedIntoExecutionAttribute() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
<?php
#[\Go\Lang\Attribute\Execution('execution(public *->*(*)')]
class MyAspect {}
            """.trimIndent()
        )
        assertPointcutInjected()
    }

    fun testPointcutLanguageInjectedIntoAccessAttribute() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
<?php
#[\Go\Lang\Attribute\Access('access(public *->*)']
class MyAspect {}
            """.trimIndent()
        )
        assertPointcutInjected()
    }

    fun testPointcutLanguageInjectedIntoWithinAttribute() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
<?php
#[\Go\Lang\Attribute\Within('within(MyNamespace\*+)']
class MyAspect {}
            """.trimIndent()
        )
        assertPointcutInjected()
    }

    fun testPointcutLanguageInjectedIntoCustomSubAttribute() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
<?php
#[\Go\Lang\Attribute\Before('execution(public MyService->*(*)']
class MyAspect {}
            """.trimIndent()
        )
        assertPointcutInjected()
    }

    fun testNoInjectionIntoUnrelatedAttribute() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
<?php
#[\Other\Attribute('execution(public *->*(*)']
class MyClass {}
            """.trimIndent()
        )
        val stringLiteral = PsiTreeUtil.findChildOfType(myFixture.file, StringLiteralExpression::class.java)
        if (stringLiteral != null) {
            val injectedFiles = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(stringLiteral)
            val hasPointcut = injectedFiles?.any { it.first.language == PointcutQueryLanguage } ?: false
            assertFalse("Should not inject pointcut language into unrelated attribute", hasPointcut)
        }
    }

    fun testNoInjectionIntoNormalPhpString() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
<?php
${'$'}x = 'execution(public *->*(*)';
            """.trimIndent()
        )
        val stringLiteral = PsiTreeUtil.findChildOfType(myFixture.file, StringLiteralExpression::class.java)
        if (stringLiteral != null) {
            val injectedFiles = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(stringLiteral)
            val hasPointcut = injectedFiles?.any { it.first.language == PointcutQueryLanguage } ?: false
            assertFalse("Should not inject pointcut language into a plain string", hasPointcut)
        }
    }

    // ----- helper -----

    private fun assertPointcutInjected() {
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        val stringLiteral = PsiTreeUtil.findChildOfType(myFixture.file, StringLiteralExpression::class.java)
        assertNotNull("Expected a string literal argument in the PHP file", stringLiteral)
        val injectedFiles = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(stringLiteral!!)
        assertNotNull("Expected injected PSI in string literal", injectedFiles)
        assertTrue(
            "Expected Go! AOP Pointcut query language to be injected",
            injectedFiles!!.any { it.first.language == PointcutQueryLanguage }
        )
    }
}
