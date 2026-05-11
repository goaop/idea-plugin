package com.aopphp.go

import com.aopphp.go.parser.PointcutLexer
import com.aopphp.go.psi.PointcutTypes
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class PointcutQuerySyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        @JvmField val SEPARATOR       = createTextAttributesKey("GO_POINTCUT_SEPARATOR",       DefaultLanguageHighlighterColors.OPERATION_SIGN)
        @JvmField val KEYWORD         = createTextAttributesKey("GO_POINTCUT_KEYWORD",          DefaultLanguageHighlighterColors.KEYWORD)
        @JvmField val COMMENT         = createTextAttributesKey("GO_POINTCUT_COMMENT",          DefaultLanguageHighlighterColors.LINE_COMMENT)
        @JvmField val PARENTHESIS     = createTextAttributesKey("GO_POINTCUT_PARENTHESIS",      DefaultLanguageHighlighterColors.PARENTHESES)
        @JvmField val MEMBER_MODIFIER = createTextAttributesKey("GO_POINTCUT_MEMBER_MODIFIER",  DefaultLanguageHighlighterColors.KEYWORD)
        @JvmField val CLASS_REFERENCE = createTextAttributesKey("GO_POINTCUT_CLASS_REFERENCE",  DefaultLanguageHighlighterColors.CLASS_REFERENCE)
        @JvmField val BAD_CHARACTER   = createTextAttributesKey("GO_POINTCUT_BAD_CHARACTER",  HighlighterColors.BAD_CHARACTER)
    }

    override fun getHighlightingLexer() = FlexAdapter(PointcutLexer(null))

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        PointcutTypes.T_ANNOTATION,
        PointcutTypes.T_NS_SEPARATOR,
        PointcutTypes.T_ASTERISK,
        PointcutTypes.T_STATIC_ACCESS,
        PointcutTypes.T_OBJECT_ACCESS,
        PointcutTypes.T_SUBNAMESPACE_SIGN,
        PointcutTypes.T_COLON,
        PointcutTypes.T_QUESTION_MARK -> arrayOf(SEPARATOR)

        PointcutTypes.ACCESS,
        PointcutTypes.EXECUTION,
        PointcutTypes.WITHIN,
        PointcutTypes.INITIALIZATION,
        PointcutTypes.STATICINITIALIZATION,
        PointcutTypes.CFLOWBELOW,
        PointcutTypes.DYNAMIC,
        PointcutTypes.MATCHINHERITED -> arrayOf(KEYWORD)

        PointcutTypes.T_LEFT_PAREN,
        PointcutTypes.T_RIGHT_PAREN -> arrayOf(PARENTHESIS)

        PointcutTypes.PRIVATE,
        PointcutTypes.PROTECTED,
        PointcutTypes.PUBLIC,
        PointcutTypes.FINAL -> arrayOf(MEMBER_MODIFIER)

        PointcutTypes.T_COMMENT -> arrayOf(COMMENT)
        TokenType.BAD_CHARACTER -> arrayOf(BAD_CHARACTER)
        else -> emptyArray()
    }
}
