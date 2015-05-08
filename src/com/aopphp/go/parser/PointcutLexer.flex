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
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

COMMENT="//".*
NAMEPART=[a-zA-Z_\x7f-\xff][a-zA-Z0-9_\x7f-\xff]*
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")

%%
<YYINITIAL> {
  {WHITE_SPACE}             { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "\\"                      { return com.aopphp.go.psi.PointcutTypes.NSSEPARATOR; }
  "@"                       { return com.aopphp.go.psi.PointcutTypes.ANNOTATION; }
  "("                       { return com.aopphp.go.psi.PointcutTypes.LP; }
  ")"                       { return com.aopphp.go.psi.PointcutTypes.RP; }
  "->"                      { return com.aopphp.go.psi.PointcutTypes.OBJECTACCESS; }
  "::"                      { return com.aopphp.go.psi.PointcutTypes.STATICACCESS; }
  "*"                       { return com.aopphp.go.psi.PointcutTypes.ASTERISK; }
  "**"                      { return com.aopphp.go.psi.PointcutTypes.DOUBLEASTERISK; }
  "+"                       { return com.aopphp.go.psi.PointcutTypes.SUBCLASSFILTER; }
  "access"                  { return com.aopphp.go.psi.PointcutTypes.ACCESS; }
  "execution"               { return com.aopphp.go.psi.PointcutTypes.EXECUTION; }
  "within"                  { return com.aopphp.go.psi.PointcutTypes.WITHIN; }
  "initialization"          { return com.aopphp.go.psi.PointcutTypes.INITIALIZATION; }
  "staticinitialization"    { return com.aopphp.go.psi.PointcutTypes.STATICINITIALIZATION; }
  "cflowbelow"              { return com.aopphp.go.psi.PointcutTypes.CFLOWBELOW; }
  "dynamic"                 { return com.aopphp.go.psi.PointcutTypes.DYNAMIC; }

  {COMMENT}                 { return com.aopphp.go.psi.PointcutTypes.COMMENT; }
  {NAMEPART}                { return com.aopphp.go.psi.PointcutTypes.NAMEPART; }
  {STRING}                  { return com.aopphp.go.psi.PointcutTypes.STRING; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
