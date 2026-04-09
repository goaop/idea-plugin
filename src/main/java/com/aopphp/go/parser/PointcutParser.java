// This is a generated file. Not intended for manual editing.
package com.aopphp.go.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.aopphp.go.psi.PointcutTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PointcutParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return pointcutFile(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // access '(' memberReference ')'
  public static boolean accessPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "accessPointcut")) return false;
    if (!nextTokenIs(builder_, ACCESS)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, ACCESS, T_LEFT_PAREN);
    result_ = result_ && memberReference(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, ACCESS_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // '@' access '(' namespaceName ')'
  public static boolean annotatedAccessPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "annotatedAccessPointcut")) return false;
    if (!nextTokenIs(builder_, T_ANNOTATION)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, T_ANNOTATION, ACCESS, T_LEFT_PAREN);
    result_ = result_ && namespaceName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, ANNOTATED_ACCESS_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // '@' execution '(' namespaceName ')'
  public static boolean annotatedExecutionPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "annotatedExecutionPointcut")) return false;
    if (!nextTokenIs(builder_, T_ANNOTATION)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, T_ANNOTATION, EXECUTION, T_LEFT_PAREN);
    result_ = result_ && namespaceName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, ANNOTATED_EXECUTION_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // '@' within '(' namespaceName ')'
  public static boolean annotatedWithinPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "annotatedWithinPointcut")) return false;
    if (!nextTokenIs(builder_, T_ANNOTATION)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, T_ANNOTATION, WITHIN, T_LEFT_PAREN);
    result_ = result_ && namespaceName(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, ANNOTATED_WITHIN_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // '*'
  public static boolean argumentList(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "argumentList")) return false;
    if (!nextTokenIs(builder_, T_ASTERISK)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_ASTERISK);
    exit_section_(builder_, marker_, ARGUMENT_LIST, result_);
    return result_;
  }

  /* ********************************************************** */
  // singlePointcut
  //   | '(' pointcutExpression ')'
  public static boolean brakedExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "brakedExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, BRAKED_EXPRESSION, "<braked expression>");
    result_ = singlePointcut(builder_, level_ + 1);
    if (!result_) result_ = brakedExpression_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '(' pointcutExpression ')'
  private static boolean brakedExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "brakedExpression_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_LEFT_PAREN);
    result_ = result_ && pointcutExpression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // cflowbelow '(' executionPointcut ')'
  public static boolean cflowbelowPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "cflowbelowPointcut")) return false;
    if (!nextTokenIs(builder_, CFLOWBELOW)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, CFLOWBELOW, T_LEFT_PAREN);
    result_ = result_ && executionPointcut(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, CFLOWBELOW_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // namespacePattern '+'?
  public static boolean classFilter(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classFilter")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CLASS_FILTER, "<class filter>");
    result_ = namespacePattern(builder_, level_ + 1);
    result_ = result_ && classFilter_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '+'?
  private static boolean classFilter_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "classFilter_1")) return false;
    consumeToken(builder_, T_SUBNAMESPACE_SIGN);
    return true;
  }

  /* ********************************************************** */
  // (negatedExpression '&&' conjugatedExpression)
  //   | negatedExpression
  public static boolean conjugatedExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conjugatedExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, CONJUGATED_EXPRESSION, "<conjugated expression>");
    result_ = conjugatedExpression_0(builder_, level_ + 1);
    if (!result_) result_ = negatedExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // negatedExpression '&&' conjugatedExpression
  private static boolean conjugatedExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "conjugatedExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = negatedExpression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_LOGICAL_AND);
    result_ = result_ && conjugatedExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // dynamic '(' memberReference '(' argumentList ')' ')'
  public static boolean dynamicExecutionPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "dynamicExecutionPointcut")) return false;
    if (!nextTokenIs(builder_, DYNAMIC)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, DYNAMIC, T_LEFT_PAREN);
    result_ = result_ && memberReference(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_LEFT_PAREN);
    result_ = result_ && argumentList(builder_, level_ + 1);
    result_ = result_ && consumeTokens(builder_, 0, T_RIGHT_PAREN, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, DYNAMIC_EXECUTION_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // execution '(' methodExecutionReference ')'
  //   | execution '(' functionExecutionReference ')'
  public static boolean executionPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "executionPointcut")) return false;
    if (!nextTokenIs(builder_, EXECUTION)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = executionPointcut_0(builder_, level_ + 1);
    if (!result_) result_ = executionPointcut_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, EXECUTION_POINTCUT, result_);
    return result_;
  }

  // execution '(' methodExecutionReference ')'
  private static boolean executionPointcut_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "executionPointcut_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, EXECUTION, T_LEFT_PAREN);
    result_ = result_ && methodExecutionReference(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // execution '(' functionExecutionReference ')'
  private static boolean executionPointcut_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "executionPointcut_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, EXECUTION, T_LEFT_PAREN);
    result_ = result_ && functionExecutionReference(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // namespacePattern '(' argumentList ')'
  public static boolean functionExecutionReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "functionExecutionReference")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, FUNCTION_EXECUTION_REFERENCE, "<function execution reference>");
    result_ = namespacePattern(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_LEFT_PAREN);
    result_ = result_ && argumentList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // initialization '(' classFilter ')'
  public static boolean initializationPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "initializationPointcut")) return false;
    if (!nextTokenIs(builder_, INITIALIZATION)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, INITIALIZATION, T_LEFT_PAREN);
    result_ = result_ && classFilter(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, INITIALIZATION_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // matchInherited '(' ')'
  public static boolean matchInheritedPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "matchInheritedPointcut")) return false;
    if (!nextTokenIs(builder_, MATCHINHERITED)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, MATCHINHERITED, T_LEFT_PAREN, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, MATCH_INHERITED_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // ['::'|'->']
  public static boolean memberAccessType(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberAccessType")) return false;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MEMBER_ACCESS_TYPE, "<member access type>");
    memberAccessType_0(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, true, false, null);
    return true;
  }

  // '::'|'->'
  private static boolean memberAccessType_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberAccessType_0")) return false;
    boolean result_;
    result_ = consumeToken(builder_, T_STATIC_ACCESS);
    if (!result_) result_ = consumeToken(builder_, T_OBJECT_ACCESS);
    return result_;
  }

  /* ********************************************************** */
  // private|protected|public|final
  public static boolean memberModifier(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberModifier")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MEMBER_MODIFIER, "<member modifier>");
    result_ = consumeToken(builder_, PRIVATE);
    if (!result_) result_ = consumeToken(builder_, PROTECTED);
    if (!result_) result_ = consumeToken(builder_, PUBLIC);
    if (!result_) result_ = consumeToken(builder_, FINAL);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // memberModifier {'|' memberModifier}*
  public static boolean memberModifiers(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberModifiers")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MEMBER_MODIFIERS, "<member modifiers>");
    result_ = memberModifier(builder_, level_ + 1);
    result_ = result_ && memberModifiers_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // {'|' memberModifier}*
  private static boolean memberModifiers_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberModifiers_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!memberModifiers_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "memberModifiers_1", pos_)) break;
    }
    return true;
  }

  // '|' memberModifier
  private static boolean memberModifiers_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberModifiers_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_ALTERNATION);
    result_ = result_ && memberModifier(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // memberModifiers classFilter memberAccessType namePattern
  public static boolean memberReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "memberReference")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, MEMBER_REFERENCE, "<member reference>");
    result_ = memberModifiers(builder_, level_ + 1);
    result_ = result_ && classFilter(builder_, level_ + 1);
    result_ = result_ && memberAccessType(builder_, level_ + 1);
    result_ = result_ && namePattern(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // memberReference '(' argumentList ')' (':' returnTypePattern)?
  public static boolean methodExecutionReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "methodExecutionReference")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, METHOD_EXECUTION_REFERENCE, "<method execution reference>");
    result_ = memberReference(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_LEFT_PAREN);
    result_ = result_ && argumentList(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    result_ = result_ && methodExecutionReference_4(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (':' returnTypePattern)?
  private static boolean methodExecutionReference_4(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "methodExecutionReference_4")) return false;
    methodExecutionReference_4_0(builder_, level_ + 1);
    return true;
  }

  // ':' returnTypePattern
  private static boolean methodExecutionReference_4_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "methodExecutionReference_4_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_COLON);
    result_ = result_ && returnTypePattern(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // namePatternPart {'|' namePatternPart}*
  public static boolean namePattern(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namePattern")) return false;
    if (!nextTokenIs(builder_, "<name pattern>", T_ASTERISK, T_NAME_PART)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, NAME_PATTERN, "<name pattern>");
    result_ = namePatternPart(builder_, level_ + 1);
    result_ = result_ && namePattern_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // {'|' namePatternPart}*
  private static boolean namePattern_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namePattern_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!namePattern_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "namePattern_1", pos_)) break;
    }
    return true;
  }

  // '|' namePatternPart
  private static boolean namePattern_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namePattern_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_ALTERNATION);
    result_ = result_ && namePatternPart(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '*'|T_NAME_PART
  static boolean namePatternItem(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namePatternItem")) return false;
    if (!nextTokenIs(builder_, "", T_ASTERISK, T_NAME_PART)) return false;
    boolean result_;
    result_ = consumeToken(builder_, T_ASTERISK);
    if (!result_) result_ = consumeToken(builder_, T_NAME_PART);
    return result_;
  }

  /* ********************************************************** */
  // namePatternItem+
  public static boolean namePatternPart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namePatternPart")) return false;
    if (!nextTokenIs(builder_, "<name pattern part>", T_ASTERISK, T_NAME_PART)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, NAME_PATTERN_PART, "<name pattern part>");
    result_ = namePatternItem(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!namePatternItem(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "namePatternPart", pos_)) break;
    }
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // T_NAME_PART {'\' T_NAME_PART}*
  public static boolean namespaceName(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespaceName")) return false;
    if (!nextTokenIs(builder_, T_NAME_PART)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_NAME_PART);
    result_ = result_ && namespaceName_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, NAMESPACE_NAME, result_);
    return result_;
  }

  // {'\' T_NAME_PART}*
  private static boolean namespaceName_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespaceName_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!namespaceName_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "namespaceName_1", pos_)) break;
    }
    return true;
  }

  // '\' T_NAME_PART
  private static boolean namespaceName_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespaceName_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, T_NS_SEPARATOR, T_NAME_PART);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // namespacePatternPart {'\' namespacePatternPart}*
  public static boolean namespacePattern(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespacePattern")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, NAMESPACE_PATTERN, "<namespace pattern>");
    result_ = namespacePatternPart(builder_, level_ + 1);
    result_ = result_ && namespacePattern_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // {'\' namespacePatternPart}*
  private static boolean namespacePattern_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespacePattern_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!namespacePattern_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "namespacePattern_1", pos_)) break;
    }
    return true;
  }

  // '\' namespacePatternPart
  private static boolean namespacePattern_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespacePattern_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_NS_SEPARATOR);
    result_ = result_ && namespacePatternPart(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // namePatternPart|'**'
  public static boolean namespacePatternPart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "namespacePatternPart")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, NAMESPACE_PATTERN_PART, "<namespace pattern part>");
    result_ = namePatternPart(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, T_DOUBLE_ASTERISK);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // brakedExpression
  //   | '!' brakedExpression
  public static boolean negatedExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "negatedExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, NEGATED_EXPRESSION, "<negated expression>");
    result_ = brakedExpression(builder_, level_ + 1);
    if (!result_) result_ = negatedExpression_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '!' brakedExpression
  private static boolean negatedExpression_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "negatedExpression_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_NEGATION);
    result_ = result_ && brakedExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (conjugatedExpression '||' pointcutExpression)
  //   | conjugatedExpression
  public static boolean pointcutExpression(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pointcutExpression")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, POINTCUT_EXPRESSION, "<pointcut expression>");
    result_ = pointcutExpression_0(builder_, level_ + 1);
    if (!result_) result_ = conjugatedExpression(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // conjugatedExpression '||' pointcutExpression
  private static boolean pointcutExpression_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pointcutExpression_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = conjugatedExpression(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_LOGICAL_OR);
    result_ = result_ && pointcutExpression(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // pointcutExpression
  static boolean pointcutFile(PsiBuilder builder_, int level_) {
    return pointcutExpression(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // [namespaceName|'$this'] '->' namePatternPart
  public static boolean pointcutReference(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pointcutReference")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, POINTCUT_REFERENCE, "<pointcut reference>");
    result_ = pointcutReference_0(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_OBJECT_ACCESS);
    result_ = result_ && namePatternPart(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // [namespaceName|'$this']
  private static boolean pointcutReference_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pointcutReference_0")) return false;
    pointcutReference_0_0(builder_, level_ + 1);
    return true;
  }

  // namespaceName|'$this'
  private static boolean pointcutReference_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "pointcutReference_0_0")) return false;
    boolean result_;
    result_ = namespaceName(builder_, level_ + 1);
    if (!result_) result_ = consumeToken(builder_, T_THIS);
    return result_;
  }

  /* ********************************************************** */
  // returnTypeAtomPart {'\' returnTypeAtomPart}*
  static boolean returnTypeAtom(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnTypeAtom")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = returnTypeAtomPart(builder_, level_ + 1);
    result_ = result_ && returnTypeAtom_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // {'\' returnTypeAtomPart}*
  private static boolean returnTypeAtom_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnTypeAtom_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!returnTypeAtom_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "returnTypeAtom_1", pos_)) break;
    }
    return true;
  }

  // '\' returnTypeAtomPart
  private static boolean returnTypeAtom_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnTypeAtom_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, T_NS_SEPARATOR);
    result_ = result_ && returnTypeAtomPart(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '**' | '*' | T_NAME_PART
  static boolean returnTypeAtomItem(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnTypeAtomItem")) return false;
    boolean result_;
    result_ = consumeToken(builder_, T_DOUBLE_ASTERISK);
    if (!result_) result_ = consumeToken(builder_, T_ASTERISK);
    if (!result_) result_ = consumeToken(builder_, T_NAME_PART);
    return result_;
  }

  /* ********************************************************** */
  // returnTypeAtomItem+
  static boolean returnTypeAtomPart(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnTypeAtomPart")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = returnTypeAtomItem(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!returnTypeAtomItem(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "returnTypeAtomPart", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // '?'? returnTypeAtom
  public static boolean returnTypePattern(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnTypePattern")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, RETURN_TYPE_PATTERN, "<return type pattern>");
    result_ = returnTypePattern_0(builder_, level_ + 1);
    result_ = result_ && returnTypeAtom(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // '?'?
  private static boolean returnTypePattern_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "returnTypePattern_0")) return false;
    consumeToken(builder_, T_QUESTION_MARK);
    return true;
  }

  /* ********************************************************** */
  // accessPointcut
  //   | annotatedAccessPointcut
  //   | executionPointcut
  //   | annotatedExecutionPointcut
  //   | withinPointcut
  //   | annotatedWithinPointcut
  //   | initializationPointcut
  //   | staticInitializationPointcut
  //   | cflowbelowPointcut
  //   | dynamicExecutionPointcut
  //   | matchInheritedPointcut
  //   | pointcutReference
  public static boolean singlePointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "singlePointcut")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, SINGLE_POINTCUT, "<single pointcut>");
    result_ = accessPointcut(builder_, level_ + 1);
    if (!result_) result_ = annotatedAccessPointcut(builder_, level_ + 1);
    if (!result_) result_ = executionPointcut(builder_, level_ + 1);
    if (!result_) result_ = annotatedExecutionPointcut(builder_, level_ + 1);
    if (!result_) result_ = withinPointcut(builder_, level_ + 1);
    if (!result_) result_ = annotatedWithinPointcut(builder_, level_ + 1);
    if (!result_) result_ = initializationPointcut(builder_, level_ + 1);
    if (!result_) result_ = staticInitializationPointcut(builder_, level_ + 1);
    if (!result_) result_ = cflowbelowPointcut(builder_, level_ + 1);
    if (!result_) result_ = dynamicExecutionPointcut(builder_, level_ + 1);
    if (!result_) result_ = matchInheritedPointcut(builder_, level_ + 1);
    if (!result_) result_ = pointcutReference(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // staticinitialization '(' classFilter ')'
  public static boolean staticInitializationPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "staticInitializationPointcut")) return false;
    if (!nextTokenIs(builder_, STATICINITIALIZATION)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, STATICINITIALIZATION, T_LEFT_PAREN);
    result_ = result_ && classFilter(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, STATIC_INITIALIZATION_POINTCUT, result_);
    return result_;
  }

  /* ********************************************************** */
  // within '(' classFilter ')'
  public static boolean withinPointcut(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "withinPointcut")) return false;
    if (!nextTokenIs(builder_, WITHIN)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeTokens(builder_, 0, WITHIN, T_LEFT_PAREN);
    result_ = result_ && classFilter(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, T_RIGHT_PAREN);
    exit_section_(builder_, marker_, WITHIN_POINTCUT, result_);
    return result_;
  }

}
