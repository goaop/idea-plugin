package com.aopphp.go.parser

import com.aopphp.go.PointcutQueryParserDefinition
import com.aopphp.go.psiutil.PointcutFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.ParsingTestCase

/**
 * Tests that the Go! AOP Pointcut grammar parser handles all supported pointcut types correctly.
 *
 * Uses [ParsingTestCase] which sets up a headless IntelliJ Platform environment,
 * registers the [PointcutQueryParserDefinition], and exposes helpers for parsing inline text.
 *
 * Tests validate that valid expressions produce a clean [PointcutFile] PSI tree with no
 * [PsiErrorElement] nodes, while invalid input does produce error nodes.
 */
class PointcutParserTest : ParsingTestCase("", "goaop", PointcutQueryParserDefinition()) {

    override fun getTestDataPath() = "src/test/testdata/parser"

    // ----- valid pointcut types -----

    fun testExecutionPointcut() {
        assertParses("execution(public MyClass->method(*))")
    }

    fun testExecutionWithStaticAccess() {
        assertParses("execution(public MyClass::staticMethod(*))")
    }

    fun testExecutionWithWildcardMethod() {
        assertParses("execution(public MyService->*(*))");
    }

    fun testExecutionWithNamespaceClass() {
        assertParses("execution(public MyNamespace\\MyClass->method(*))")
    }

    fun testExecutionWithDoubleAsteriskNamespace() {
        assertParses("execution(public My\\**\\Service->*(*))")
    }

    fun testWithinPointcut() {
        assertParses("within(MyClass)")
    }

    fun testWithinPointcutWithSubclasses() {
        assertParses("within(MyNamespace\\MyClass+)")
    }

    fun testWithinPointcutWithNamespaceWildcard() {
        assertParses("within(MyNamespace\\*)")
    }

    fun testAccessPointcut() {
        assertParses("access(protected MyClass->field)")
    }

    fun testAccessPointcutPublic() {
        assertParses("access(public MyClass->*)")
    }

    fun testAnnotatedExecutionPointcut() {
        assertParses("@execution(MyAnnotation)")
    }

    fun testAnnotatedAccessPointcut() {
        assertParses("@access(MyAnnotation)")
    }

    fun testAnnotatedWithinPointcut() {
        assertParses("@within(MyAnnotation)")
    }

    fun testAnnotatedPointcutWithNamespace() {
        assertParses("@execution(My\\Namespace\\MyAttribute)")
    }

    fun testInitializationPointcut() {
        assertParses("initialization(MyClass)")
    }

    fun testStaticInitializationPointcut() {
        assertParses("staticinitialization(MyClass)")
    }

    fun testMatchInheritedPointcut() {
        assertParses("matchInherited()")
    }

    fun testNegatedExpression() {
        assertParses("!within(MyClass)")
    }

    fun testConjunctionExpression() {
        assertParses("execution(public MyClass->*(*))&&within(MyClass)")
    }

    fun testDisjunctionExpression() {
        assertParses("execution(public MyService->*(*))||within(MyNamespace\\*)")
    }

    fun testComplexBooleanExpression() {
        assertParses("execution(public MyClass->*(*))&&!within(ExcludedClass)")
    }

    fun testBracketedExpression() {
        assertParses("(execution(public A->*(*))||within(B))")
    }

    fun testMultipleModifiers() {
        assertParses("execution(public|protected MyClass->method(*))")
    }

    fun testFinalModifier() {
        assertParses("execution(final MyClass->method(*))")
    }

    fun testAllModifiers() {
        assertParses("execution(public|protected|private|final MyClass->method(*))")
    }

    fun testSelfPointcutReference() {
        assertParses("\$this->myPointcut")
    }

    fun testFunctionExecution() {
        assertParses("execution(myFunction(*))")
    }

    fun testFunctionExecutionWithNamespace() {
        assertParses("execution(My\\Namespace\\myFunction(*))")
    }

    fun testCflowbelowPointcut() {
        assertParses("cflowbelow(execution(public MyClass->method(*)))")
    }

    fun testLineComment() {
        assertParses("execution(public MyClass->method(*)) // this is a comment")
    }

    // ----- invalid input should produce PsiErrorElement -----

    fun testEmptyExpressionHasErrors() {
        assertHasErrors("")
    }

    fun testMissingCloseParenHasErrors() {
        assertHasErrors("execution(public MyClass->method(*)")
    }

    fun testKeywordAloneHasErrors() {
        assertHasErrors("execution")
    }

    // ----- helpers -----

    private fun assertParses(text: String) {
        val file = createPsiFile("test", text)
        ensureParsed(file)
        val errors = PsiTreeUtil.collectElementsOfType(file, PsiErrorElement::class.java)
        assertTrue(
            "Expected no parse errors for: '$text'\nErrors: ${errors.map { it.errorDescription }}",
            errors.isEmpty()
        )
        assertTrue("Result should be a PointcutFile", file is PointcutFile)
    }

    private fun assertHasErrors(text: String) {
        val file = createPsiFile("test", text)
        ensureParsed(file)
        val errors = PsiTreeUtil.collectElementsOfType(file, PsiErrorElement::class.java)
        assertTrue("Expected parse errors for: '$text'", errors.isNotEmpty())
    }
}
