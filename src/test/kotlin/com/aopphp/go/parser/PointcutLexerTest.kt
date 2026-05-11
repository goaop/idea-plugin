package com.aopphp.go.parser

import com.aopphp.go.parser.PointcutLexer
import com.intellij.lexer.FlexAdapter
import com.intellij.testFramework.LexerTestCase

/**
 * Tests the [PointcutLexer] tokenization.
 *
 * Uses [LexerTestCase] which provides [doTest] for comparing lexer output against an expected
 * token-per-line string in the form:
 *   TOKEN_TYPE ('token text')
 *
 * White space is returned as WHITE_SPACE by the flex lexer (TokenType.WHITE_SPACE).
 */
class PointcutLexerTest : LexerTestCase() {

    override fun createLexer() = FlexAdapter(PointcutLexer(null))

    override fun getDirPath() = "src/test/testdata/lexer"

    fun testKeywordsTokenized() {
        doTest(
            "execution access within initialization staticinitialization cflowbelow dynamic matchInherited",
            "PointcutTokenType.execution ('execution')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.access ('access')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.within ('within')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.initialization ('initialization')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.staticinitialization ('staticinitialization')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.cflowbelow ('cflowbelow')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.dynamic ('dynamic')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.matchInherited ('matchInherited')"
        )
    }

    fun testModifierKeywordsTokenized() {
        doTest(
            "public protected private final",
            "PointcutTokenType.public ('public')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.protected ('protected')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.private ('private')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.final ('final')"
        )
    }

    fun testOperatorTokens() {
        doTest(
            "( ) -> :: * ** | ! && ||",
            "PointcutTokenType.( ('(')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.) (')')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.-> ('->')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.:: ('::')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.* ('*')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.** ('**')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.| ('|')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.! ('!')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.&& ('&&')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.|| ('||')"
        )
    }

    fun testNamePartToken() {
        doTest(
            "MyClass",
            "PointcutTokenType.T_NAME_PART ('MyClass')"
        )
    }

    fun testNamespaceToken() {
        doTest(
            "My\\Namespace\\Class",
            "PointcutTokenType.T_NAME_PART ('My')\n" +
            "PointcutTokenType.\\ ('\\')\n" +
            "PointcutTokenType.T_NAME_PART ('Namespace')\n" +
            "PointcutTokenType.\\ ('\\')\n" +
            "PointcutTokenType.T_NAME_PART ('Class')"
        )
    }

    fun testAnnotationToken() {
        doTest(
            "@execution",
            "PointcutTokenType.@ ('@')\n" +
            "PointcutTokenType.execution ('execution')"
        )
    }

    fun testThisToken() {
        doTest(
            "\$this",
            "PointcutTokenType.\$this ('\$this')"
        )
    }

    fun testSubnamespaceSignToken() {
        doTest(
            "MyClass+",
            "PointcutTokenType.T_NAME_PART ('MyClass')\n" +
            "PointcutTokenType.+ ('+')"
        )
    }

    fun testCommentToken() {
        doTest(
            "// this is a comment",
            "PointcutTokenType.T_COMMENT ('// this is a comment')"
        )
    }

    fun testBadCharacterToken() {
        doTest(
            "#",
            "BAD_CHARACTER ('#')"
        )
    }

    fun testFullExecutionExpression() {
        doTest(
            "execution(public MyClass->method(*))",
            "PointcutTokenType.execution ('execution')\n" +
            "PointcutTokenType.( ('(')\n" +
            "PointcutTokenType.public ('public')\n" +
            "WHITE_SPACE (' ')\n" +
            "PointcutTokenType.T_NAME_PART ('MyClass')\n" +
            "PointcutTokenType.-> ('->')\n" +
            "PointcutTokenType.T_NAME_PART ('method')\n" +
            "PointcutTokenType.( ('(')\n" +
            "PointcutTokenType.* ('*')\n" +
            "PointcutTokenType.) (')')\n" +
            "PointcutTokenType.) (')')"
        )
    }
}
