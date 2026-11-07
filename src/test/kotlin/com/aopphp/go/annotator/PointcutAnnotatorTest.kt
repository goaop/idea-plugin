package com.aopphp.go.annotator

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.lang.PhpFileType

/**
 * Tests the Go! AOP 4.0 checks of [PointcutAnnotator].
 */
class PointcutAnnotatorTest : BasePlatformTestCase() {

    private fun problems(text: String, fileName: String = "test.goaop"): List<String> {
        myFixture.configureByText(fileName, text)
        return myFixture.doHighlighting()
            .filter { it.severity >= HighlightSeverity.WARNING }
            .mapNotNull { it.description }
    }

    private fun assertProblem(text: String, expected: String) {
        val problems = problems(text)
        assertTrue("Expected '$expected' in $problems", problems.any { it.contains(expected) })
    }

    fun testValidExpressionHasNoProblems() {
        val text = "execution(final public|protected App\\**->get*(*): ?App\\Entity) && !matchReturningByReference()" +
            " || access(public private(set) App\\Model->*) || within(App\\Cart|Order) || initialization(App\\Base+)"
        assertEquals(emptyList<String>(), problems(text))
    }

    fun testDynamicPointcutIsReported() {
        assertProblem("dynamic(public App\\Foo->save*(*))", "dynamic() pointcut was removed in Go! AOP 4.0")
    }

    fun testDynamicPointcutQuickFixUsesTheMagicMethod() {
        myFixture.configureByText("test.goaop", "<caret>dynamic(public App\\Foo::save*(*))")
        myFixture.doHighlighting()
        val fix = myFixture.findSingleIntention("Replace with execution of the magic method")
        myFixture.launchAction(fix)
        myFixture.checkResult("execution(public App\\Foo::__callStatic(*))")
    }

    fun testCflowbelowPointcutIsReported() {
        assertProblem("cflowbelow(execution(public App\\Foo->bar(*)))", "cflowbelow() pointcut is not supported")
    }

    fun testOnlyOneGroupOfAlternatives() {
        assertProblem("execution(public|protected final|private App\\Foo->*(*))", "Only one group of '|' alternatives")
    }

    fun testStaticPropertyAccessIsNeverIntercepted() {
        assertProblem("access(public App\\Foo::bar)", "Static properties are never intercepted")
    }

    fun testReadonlyPropertyAccessIsNeverIntercepted() {
        assertProblem("access(readonly App\\Foo->bar)", "Readonly properties are never intercepted")
    }

    fun testFunctionPatternNeedsANamespace() {
        assertProblem("execution(file_get_contents(*))", "A function pattern needs a namespace")
    }

    fun testQuestionMarkWildcardIsReported() {
        assertProblem("execution(public App\\Foo->?etName(*))", "'?' wildcard was removed in Go! AOP 4.0")
    }

    fun testUnknownSelfPointcutReference() {
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
            <?php
            use Go\Lang\Attribute\Before;
            use Go\Lang\Attribute\Pointcut;
            class MyAspect implements \Go\Aop\Aspect {
                #[Pointcut('execution(public App\Foo->*(*))')]
                protected function fooMethods(): void {}

                #[Before('${'$'}this->fooMethods || ${'$'}this->missing')]
                public function beforeFoo(): void {}
            }
            """.trimIndent()
        )
        val problems = myFixture.doHighlighting()
            .filter { it.severity == HighlightSeverity.ERROR }
            .mapNotNull { it.description }
        assertTrue(problems.toString(), problems.any { it.contains("Unknown pointcut 'missing'") })
        assertFalse(problems.toString(), problems.any { it.contains("'fooMethods'") })
    }
}
