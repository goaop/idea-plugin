package com.aopphp.go;


import com.aopphp.go.parser.PointcutLexer;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.aopphp.go.psi.PointcutTypes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Reader;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class PointcutQuerySyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey SEPARATOR =
            createTextAttributesKey("GO_POINTCUT_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("GO_POINTCUT_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("GO_POINTCUT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey PARENTHESIS =
            createTextAttributesKey("GO_POINTCUT_PARENTHESIS", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey MEMBER_MODIFIER =
            createTextAttributesKey("GO_POINTCUT_MEMBER_MODIFIER", DefaultLanguageHighlighterColors.STRING);

    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("GO_POINTCUT_BAD_CHARACTER",
            new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] PARENTHESIS_KEYS = new TextAttributesKey[]{PARENTHESIS};
    private static final TextAttributesKey[] MEMBER_MODIFIER_KEYS = new TextAttributesKey[]{MEMBER_MODIFIER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FlexAdapter(new PointcutLexer((Reader) null));
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(PointcutTypes.T_NS_SEPARATOR)
            || tokenType.equals(PointcutTypes.T_ASTERISK)
            || tokenType.equals(PointcutTypes.T_STATIC_ACCESS)
            || tokenType.equals(PointcutTypes.T_OBJECT_ACCESS)
            || tokenType.equals(PointcutTypes.T_SUBNAMESPACE_SIGN)) {
            return SEPARATOR_KEYS;
        } else if (tokenType.equals(PointcutTypes.ACCESS)
            || tokenType.equals(PointcutTypes.EXECUTION)
            || tokenType.equals(PointcutTypes.WITHIN)
            || tokenType.equals(PointcutTypes.INITIALIZATION)
            || tokenType.equals(PointcutTypes.STATICINITIALIZATION)
            || tokenType.equals(PointcutTypes.CFLOWBELOW)
            || tokenType.equals(PointcutTypes.DYNAMIC)) {
            return KEY_KEYS;
        } else if (tokenType.equals(PointcutTypes.T_LEFT_PAREN)
            || tokenType.equals(PointcutTypes.T_RIGHT_PAREN)) {
            return PARENTHESIS_KEYS;
        } else if (tokenType.equals(PointcutTypes.PRIVATE)
            || tokenType.equals(PointcutTypes.PROTECTED)
            || tokenType.equals(PointcutTypes.PUBLIC)
            || tokenType.equals(PointcutTypes.FINAL)) {
            return MEMBER_MODIFIER_KEYS;
        } else if (tokenType.equals(PointcutTypes.T_COMMENT)) {
            return COMMENT_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
