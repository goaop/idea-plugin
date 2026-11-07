package com.aopphp.go.injector

import com.aopphp.go.PointcutQueryLanguage
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

/**
 * Tests that the Go! AOP Pointcut query language is injected into the pointcut expression of
 * `#[\Go\Lang\Attribute\*]` attributes and `PointcutBuilder` calls, and nowhere else.
 */
class PointcutLanguageInjectionTest : BasePlatformTestCase() {

    fun testPointcutLanguageInjectedIntoAdviceAttributes() {
        for (attribute in listOf("Before", "After", "Around", "AfterThrowing", "Pointcut")) {
            configure(
                """
                class MyAspect {
                    #[\Go\Lang\Attribute\$attribute('execution(public MyService->*(*))')]
                    public function advice() {}
                }
                """
            )
            assertTrue("Expected injection into #[$attribute]", isInjected(stringLiterals().single()))
        }
    }

    fun testPointcutLanguageInjectedIntoImportedAttribute() {
        configure(
            """
            use Go\Lang\Attribute\Before;
            class MyAspect {
                #[Before('within(App\**)')]
                public function advice() {}
            }
            """
        )
        assertTrue(isInjected(stringLiterals().single()))
    }

    fun testPointcutLanguageInjectedIntoNamedExpressionArgument() {
        configure(
            """
            class MyAspect {
                #[\Go\Lang\Attribute\Around(order: 10, expression: 'execution(public MyService->*(*))')]
                public function advice() {}
            }
            """
        )
        assertTrue(isInjected(stringLiterals().single()))
    }

    fun testOnlyExpressionOfDeclareParentsIsInjected() {
        configure(
            """
            class MyAspect {
                #[\Go\Lang\Attribute\DeclareParents('within(App\Service\**)', interfaceName: 'App\Countable', traitName: 'App\CountableTrait')]
                protected ${'$'}introduction;
            }
            """
        )
        val (expression, interfaceName, traitName) = stringLiterals()
        assertTrue(isInjected(expression))
        assertFalse(isInjected(interfaceName))
        assertFalse(isInjected(traitName))
    }

    fun testNoInjectionIntoUnknownAttributeOfTheNamespace() {
        configure(
            """
            #[\Go\Lang\Attribute\Execution('execution(public *->*(*))')]
            class MyAspect {}
            """
        )
        assertFalse(isInjected(stringLiterals().single()))
    }

    fun testNoInjectionIntoUnrelatedAttribute() {
        configure(
            """
            #[\Other\Attribute('execution(public *->*(*))')]
            class MyClass {}
            """
        )
        assertFalse(isInjected(stringLiterals().single()))
    }

    fun testNoInjectionIntoNormalPhpString() {
        configure("${'$'}x = 'execution(public *->*(*))';")
        assertFalse(isInjected(stringLiterals().single()))
    }

    // ----- helpers -----

    private fun configure(code: String) {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" + code.trimIndent())
        PsiDocumentManager.getInstance(project).commitAllDocuments()
    }

    private fun stringLiterals(): List<StringLiteralExpression> =
        PsiTreeUtil.findChildrenOfType(myFixture.file, StringLiteralExpression::class.java).toList()

    private fun isInjected(literal: StringLiteralExpression): Boolean {
        val injectedFiles = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(literal) ?: return false
        return injectedFiles.any { it.first.language == PointcutQueryLanguage }
    }
}
