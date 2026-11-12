package com.aopphp.go.inspection

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.lang.PhpFileType

/**
 * Tests the PHP inspections for the Go! AOP 4.0 aspect rules.
 */
class AspectInspectionsTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(
            AdviceMethodInspection(),
            RemovedAttributeInspection(),
            ReturningByReferenceInspection()
        )
    }

    private fun configure(code: String) {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php\n" + code.trimIndent())
    }

    private fun problems(): List<String> = myFixture.doHighlighting()
        .filter { it.severity >= HighlightSeverity.WEAK_WARNING }
        .mapNotNull { it.description }

    fun testNonPublicAdviceMethodIsReported() {
        configure(
            """
            use Go\Lang\Attribute\Before;
            class MyAspect implements \Go\Aop\Aspect {
                #[Before('execution(public App\Foo->*(*))')]
                protected function beforeFoo(): void {}
            }
            """
        )
        assertTrue(problems().toString(), problems().any { it.contains("Advice method 'beforeFoo' must be public") })
    }

    fun testMakePublicQuickFix() {
        configure(
            """
            use Go\Lang\Attribute\Before;
            class MyAspect implements \Go\Aop\Aspect {
                #[Before('execution(public App\Foo->*(*))')]
                protected function <caret>beforeFoo(): void {}
            }
            """
        )
        myFixture.launchAction(myFixture.findSingleIntention("Make method public"))
        assertTrue(myFixture.editor.document.text.contains("public function beforeFoo()"))
    }

    fun testPointcutMethodMayKeepAnyVisibility() {
        configure(
            """
            use Go\Lang\Attribute\Pointcut;
            class MyAspect implements \Go\Aop\Aspect {
                #[Pointcut('execution(public App\Foo->*(*))')]
                protected function fooMethods(): void {}
            }
            """
        )
        assertFalse(problems().toString(), problems().any { it.contains("must be public") })
    }

    fun testSecondAdviceAttributeIsReported() {
        configure(
            """
            use Go\Lang\Attribute\After;
            use Go\Lang\Attribute\Before;
            use Go\Lang\Attribute\Pointcut;
            class MyAspect implements \Go\Aop\Aspect {
                #[Pointcut('execution(public App\Foo->*(*))')]
                #[Before('execution(public App\Foo->*(*))')]
                #[After('execution(public App\Foo->*(*))')]
                public function aroundFoo(): void {}
            }
            """
        )
        val problems = problems()
        assertEquals(problems.toString(), 1, problems.count { it.contains("Only one advice attribute per method") })
    }

    fun testRemovedAspectAttributeIsReportedAndRemoved() {
        configure(
            """
            #[\Go\Lang\Attribute\Asp<caret>ect]
            class MyAspect implements \Go\Aop\Aspect {}
            """
        )
        assertTrue(problems().toString(), problems().any { it.contains("#[Aspect] attribute was removed in Go! AOP 4.0") })
        myFixture.launchAction(myFixture.findSingleIntention("Remove attribute"))
        assertFalse(myFixture.editor.document.text.contains("Attribute\\Aspect"))
    }

    fun testRemovedDeclareErrorAttributeIsReported() {
        configure(
            """
            class MyAspect implements \Go\Aop\Aspect {
                #[\Go\Lang\Attribute\DeclareError('execution(public App\Foo->*(*))', message: 'Do not call')]
                protected ${'$'}error;
            }
            """
        )
        assertTrue(problems().toString(), problems().any { it.contains("#[DeclareError] was removed in Go! AOP 4.0") })
    }

    fun testAdvisedMethodReturningByReferenceIsReported() {
        configure(
            """
            namespace App {
                class Foo {
                    public function &items(): array { static ${'$'}items = []; return ${'$'}items; }
                    public function plain(): array { return []; }
                }
            }
            namespace Aspect {
                use Go\Lang\Attribute\Before;
                class MyAspect implements \Go\Aop\Aspect {
                    #[Before('execution(public App\Foo->*(*))')]
                    public function beforeFoo(): void {}
                }
            }
            """
        )
        val problems = problems()
        assertEquals(problems.toString(), 1, problems.count { it.contains("Method returning by reference is matched by") })
    }
}
