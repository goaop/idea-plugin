package com.aopphp.go.parser;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.aopphp.go.psi.PointcutTypes.*;

%%

%{
  public PointcutLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class PointcutLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
T_COMMENT="//".*
T_LINE_WS=[\ \t\f]
T_NAME_PART=[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*
WHITE_SPACE=({T_LINE_WS}|{EOL})+


%%
<YYINITIAL> {
  {WHITE_SPACE}             { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "\\"                      { return com.aopphp.go.psi.PointcutTypes.T_NS_SEPARATOR; }
  "@"                       { return com.aopphp.go.psi.PointcutTypes.T_ANNOTATION; }
  "("                       { return com.aopphp.go.psi.PointcutTypes.T_LEFT_PAREN; }
  ")"                       { return com.aopphp.go.psi.PointcutTypes.T_RIGHT_PAREN; }
  "->"                      { return com.aopphp.go.psi.PointcutTypes.T_OBJECT_ACCESS; }
  "::"                      { return com.aopphp.go.psi.PointcutTypes.T_STATIC_ACCESS; }
  "*"                       { return com.aopphp.go.psi.PointcutTypes.T_ASTERISK; }
  "|"                       { return com.aopphp.go.psi.PointcutTypes.T_ALTERNATION; }
  "!"                       { return com.aopphp.go.psi.PointcutTypes.T_NEGATION; }
  "&&"                      { return com.aopphp.go.psi.PointcutTypes.T_LOGICAL_AND; }
  "||"                      { return com.aopphp.go.psi.PointcutTypes.T_LOGICAL_OR; }
  "**"                      { return com.aopphp.go.psi.PointcutTypes.T_DOUBLE_ASTERISK; }
  "+"                       { return com.aopphp.go.psi.PointcutTypes.T_SUBNAMESPACE_SIGN; }
  "$this"                   { return com.aopphp.go.psi.PointcutTypes.T_THIS; }
  "access"                  { return com.aopphp.go.psi.PointcutTypes.ACCESS; }
  "execution"               { return com.aopphp.go.psi.PointcutTypes.EXECUTION; }
  "within"                  { return com.aopphp.go.psi.PointcutTypes.WITHIN; }
  "initialization"          { return com.aopphp.go.psi.PointcutTypes.INITIALIZATION; }
  "staticinitialization"    { return com.aopphp.go.psi.PointcutTypes.STATICINITIALIZATION; }
  "cflowbelow"              { return com.aopphp.go.psi.PointcutTypes.CFLOWBELOW; }
  "dynamic"                 { return com.aopphp.go.psi.PointcutTypes.DYNAMIC; }
  "private"                 { return com.aopphp.go.psi.PointcutTypes.PRIVATE; }
  "protected"               { return com.aopphp.go.psi.PointcutTypes.PROTECTED; }
  "public"                  { return com.aopphp.go.psi.PointcutTypes.PUBLIC; }
  "final"                   { return com.aopphp.go.psi.PointcutTypes.FINAL; }

  {T_COMMENT}               { return com.aopphp.go.psi.PointcutTypes.T_COMMENT; }
  {T_NAME_PART}             { return com.aopphp.go.psi.PointcutTypes.T_NAME_PART; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
