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

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class PointcutParser implements PsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ACCESS_POINTCUT) {
      r = accessPointcut(b, 0);
    }
    else if (t == ANNOTATED_ACCESS_POINTCUT) {
      r = annotatedAccessPointcut(b, 0);
    }
    else if (t == ANNOTATED_EXECUTION_POINTCUT) {
      r = annotatedExecutionPointcut(b, 0);
    }
    else if (t == ANNOTATED_WITHIN_POINTCUT) {
      r = annotatedWithinPointcut(b, 0);
    }
    else if (t == ARGUMENT_LIST) {
      r = argumentList(b, 0);
    }
    else if (t == BRAKED_EXPRESSION) {
      r = brakedExpression(b, 0);
    }
    else if (t == CFLOWBELOW_POINTCUT) {
      r = cflowbelowPointcut(b, 0);
    }
    else if (t == CLASS_FILTER) {
      r = classFilter(b, 0);
    }
    else if (t == CONJUGATED_EXPRESSION) {
      r = conjugatedExpression(b, 0);
    }
    else if (t == DYNAMIC_EXECUTION_POINTCUT) {
      r = dynamicExecutionPointcut(b, 0);
    }
    else if (t == EXECUTION_POINTCUT) {
      r = executionPointcut(b, 0);
    }
    else if (t == FUNCTION_EXECUTION_REFERENCE) {
      r = functionExecutionReference(b, 0);
    }
    else if (t == INITIALIZATION_POINTCUT) {
      r = initializationPointcut(b, 0);
    }
    else if (t == MATCH_INHERITED_POINTCUT) {
      r = matchInheritedPointcut(b, 0);
    }
    else if (t == MEMBER_ACCESS_TYPE) {
      r = memberAccessType(b, 0);
    }
    else if (t == MEMBER_MODIFIER) {
      r = memberModifier(b, 0);
    }
    else if (t == MEMBER_MODIFIERS) {
      r = memberModifiers(b, 0);
    }
    else if (t == MEMBER_REFERENCE) {
      r = memberReference(b, 0);
    }
    else if (t == METHOD_EXECUTION_REFERENCE) {
      r = methodExecutionReference(b, 0);
    }
    else if (t == NAME_PATTERN) {
      r = namePattern(b, 0);
    }
    else if (t == NAME_PATTERN_PART) {
      r = namePatternPart(b, 0);
    }
    else if (t == NAMESPACE_NAME) {
      r = namespaceName(b, 0);
    }
    else if (t == NAMESPACE_PATTERN) {
      r = namespacePattern(b, 0);
    }
    else if (t == NAMESPACE_PATTERN_PART) {
      r = namespacePatternPart(b, 0);
    }
    else if (t == NEGATED_EXPRESSION) {
      r = negatedExpression(b, 0);
    }
    else if (t == POINTCUT_EXPRESSION) {
      r = pointcutExpression(b, 0);
    }
    else if (t == POINTCUT_REFERENCE) {
      r = pointcutReference(b, 0);
    }
    else if (t == SINGLE_POINTCUT) {
      r = singlePointcut(b, 0);
    }
    else if (t == STATIC_INITIALIZATION_POINTCUT) {
      r = staticInitializationPointcut(b, 0);
    }
    else if (t == WITHIN_POINTCUT) {
      r = withinPointcut(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return pointcutFile(b, l + 1);
  }

  /* ********************************************************** */
  // access '(' memberReference ')'
  public static boolean accessPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessPointcut")) return false;
    if (!nextTokenIs(b, ACCESS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ACCESS);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && memberReference(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, ACCESS_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // '@' access '(' namespaceName ')'
  public static boolean annotatedAccessPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotatedAccessPointcut")) return false;
    if (!nextTokenIs(b, T_ANNOTATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_ANNOTATION);
    r = r && consumeToken(b, ACCESS);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && namespaceName(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, ANNOTATED_ACCESS_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // '@' execution '(' namespaceName ')'
  public static boolean annotatedExecutionPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotatedExecutionPointcut")) return false;
    if (!nextTokenIs(b, T_ANNOTATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_ANNOTATION);
    r = r && consumeToken(b, EXECUTION);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && namespaceName(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, ANNOTATED_EXECUTION_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // '@' within '(' namespaceName ')'
  public static boolean annotatedWithinPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotatedWithinPointcut")) return false;
    if (!nextTokenIs(b, T_ANNOTATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_ANNOTATION);
    r = r && consumeToken(b, WITHIN);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && namespaceName(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, ANNOTATED_WITHIN_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // '*'
  public static boolean argumentList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argumentList")) return false;
    if (!nextTokenIs(b, T_ASTERISK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_ASTERISK);
    exit_section_(b, m, ARGUMENT_LIST, r);
    return r;
  }

  /* ********************************************************** */
  // singlePointcut
  //   | '(' pointcutExpression ')'
  public static boolean brakedExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "brakedExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<braked expression>");
    r = singlePointcut(b, l + 1);
    if (!r) r = brakedExpression_1(b, l + 1);
    exit_section_(b, l, m, BRAKED_EXPRESSION, r, false, null);
    return r;
  }

  // '(' pointcutExpression ')'
  private static boolean brakedExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "brakedExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_LEFT_PAREN);
    r = r && pointcutExpression(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // cflowbelow '(' executionPointcut ')'
  public static boolean cflowbelowPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cflowbelowPointcut")) return false;
    if (!nextTokenIs(b, CFLOWBELOW)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CFLOWBELOW);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && executionPointcut(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, CFLOWBELOW_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // namespacePattern '+'?
  public static boolean classFilter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classFilter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<class filter>");
    r = namespacePattern(b, l + 1);
    r = r && classFilter_1(b, l + 1);
    exit_section_(b, l, m, CLASS_FILTER, r, false, null);
    return r;
  }

  // '+'?
  private static boolean classFilter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classFilter_1")) return false;
    consumeToken(b, T_SUBNAMESPACE_SIGN);
    return true;
  }

  /* ********************************************************** */
  // (negatedExpression '&&' conjugatedExpression)
  //   | negatedExpression
  public static boolean conjugatedExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "conjugatedExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<conjugated expression>");
    r = conjugatedExpression_0(b, l + 1);
    if (!r) r = negatedExpression(b, l + 1);
    exit_section_(b, l, m, CONJUGATED_EXPRESSION, r, false, null);
    return r;
  }

  // negatedExpression '&&' conjugatedExpression
  private static boolean conjugatedExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "conjugatedExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = negatedExpression(b, l + 1);
    r = r && consumeToken(b, T_LOGICAL_AND);
    r = r && conjugatedExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // dynamic '(' memberReference '(' argumentList ')' ')'
  public static boolean dynamicExecutionPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamicExecutionPointcut")) return false;
    if (!nextTokenIs(b, DYNAMIC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DYNAMIC);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && memberReference(b, l + 1);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && argumentList(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, DYNAMIC_EXECUTION_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // execution '(' methodExecutionReference ')'
  //   | execution '(' functionExecutionReference ')'
  public static boolean executionPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "executionPointcut")) return false;
    if (!nextTokenIs(b, EXECUTION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = executionPointcut_0(b, l + 1);
    if (!r) r = executionPointcut_1(b, l + 1);
    exit_section_(b, m, EXECUTION_POINTCUT, r);
    return r;
  }

  // execution '(' methodExecutionReference ')'
  private static boolean executionPointcut_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "executionPointcut_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXECUTION);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && methodExecutionReference(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // execution '(' functionExecutionReference ')'
  private static boolean executionPointcut_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "executionPointcut_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXECUTION);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && functionExecutionReference(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namespacePattern '(' argumentList ')'
  public static boolean functionExecutionReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionExecutionReference")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<function execution reference>");
    r = namespacePattern(b, l + 1);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && argumentList(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, l, m, FUNCTION_EXECUTION_REFERENCE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // initialization '(' classFilter ')'
  public static boolean initializationPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializationPointcut")) return false;
    if (!nextTokenIs(b, INITIALIZATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INITIALIZATION);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && classFilter(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, INITIALIZATION_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // matchInherited '(' ')'
  public static boolean matchInheritedPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "matchInheritedPointcut")) return false;
    if (!nextTokenIs(b, MATCHINHERITED)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, MATCHINHERITED);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, MATCH_INHERITED_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // ['::'|'->']
  public static boolean memberAccessType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberAccessType")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<member access type>");
    memberAccessType_0(b, l + 1);
    exit_section_(b, l, m, MEMBER_ACCESS_TYPE, true, false, null);
    return true;
  }

  // '::'|'->'
  private static boolean memberAccessType_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberAccessType_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_STATIC_ACCESS);
    if (!r) r = consumeToken(b, T_OBJECT_ACCESS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // private|protected|public|final
  public static boolean memberModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberModifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<member modifier>");
    r = consumeToken(b, PRIVATE);
    if (!r) r = consumeToken(b, PROTECTED);
    if (!r) r = consumeToken(b, PUBLIC);
    if (!r) r = consumeToken(b, FINAL);
    exit_section_(b, l, m, MEMBER_MODIFIER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // memberModifier {'|' memberModifier}*
  public static boolean memberModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberModifiers")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<member modifiers>");
    r = memberModifier(b, l + 1);
    r = r && memberModifiers_1(b, l + 1);
    exit_section_(b, l, m, MEMBER_MODIFIERS, r, false, null);
    return r;
  }

  // {'|' memberModifier}*
  private static boolean memberModifiers_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberModifiers_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!memberModifiers_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "memberModifiers_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '|' memberModifier
  private static boolean memberModifiers_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberModifiers_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_ALTERNATION);
    r = r && memberModifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // memberModifiers classFilter memberAccessType namePattern
  public static boolean memberReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberReference")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<member reference>");
    r = memberModifiers(b, l + 1);
    r = r && classFilter(b, l + 1);
    r = r && memberAccessType(b, l + 1);
    r = r && namePattern(b, l + 1);
    exit_section_(b, l, m, MEMBER_REFERENCE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // memberReference '(' argumentList ')'
  public static boolean methodExecutionReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "methodExecutionReference")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<method execution reference>");
    r = memberReference(b, l + 1);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && argumentList(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, l, m, METHOD_EXECUTION_REFERENCE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // namePatternPart {'|' namePatternPart}*
  public static boolean namePattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePattern")) return false;
    if (!nextTokenIs(b, "<name pattern>", T_ASTERISK, T_NAME_PART)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<name pattern>");
    r = namePatternPart(b, l + 1);
    r = r && namePattern_1(b, l + 1);
    exit_section_(b, l, m, NAME_PATTERN, r, false, null);
    return r;
  }

  // {'|' namePatternPart}*
  private static boolean namePattern_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePattern_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!namePattern_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namePattern_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '|' namePatternPart
  private static boolean namePattern_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePattern_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_ALTERNATION);
    r = r && namePatternPart(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '*'|T_NAME_PART
  static boolean namePatternItem(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePatternItem")) return false;
    if (!nextTokenIs(b, "", T_ASTERISK, T_NAME_PART)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_ASTERISK);
    if (!r) r = consumeToken(b, T_NAME_PART);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namePatternItem+
  public static boolean namePatternPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePatternPart")) return false;
    if (!nextTokenIs(b, "<name pattern part>", T_ASTERISK, T_NAME_PART)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<name pattern part>");
    r = namePatternItem(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!namePatternItem(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namePatternPart", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, NAME_PATTERN_PART, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // T_NAME_PART {'\' T_NAME_PART}*
  public static boolean namespaceName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceName")) return false;
    if (!nextTokenIs(b, T_NAME_PART)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_NAME_PART);
    r = r && namespaceName_1(b, l + 1);
    exit_section_(b, m, NAMESPACE_NAME, r);
    return r;
  }

  // {'\' T_NAME_PART}*
  private static boolean namespaceName_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceName_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!namespaceName_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namespaceName_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '\' T_NAME_PART
  private static boolean namespaceName_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceName_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_NS_SEPARATOR);
    r = r && consumeToken(b, T_NAME_PART);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namespacePatternPart {'\' namespacePatternPart}*
  public static boolean namespacePattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespacePattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<namespace pattern>");
    r = namespacePatternPart(b, l + 1);
    r = r && namespacePattern_1(b, l + 1);
    exit_section_(b, l, m, NAMESPACE_PATTERN, r, false, null);
    return r;
  }

  // {'\' namespacePatternPart}*
  private static boolean namespacePattern_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespacePattern_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!namespacePattern_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namespacePattern_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '\' namespacePatternPart
  private static boolean namespacePattern_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespacePattern_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_NS_SEPARATOR);
    r = r && namespacePatternPart(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namePatternPart|'**'
  public static boolean namespacePatternPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespacePatternPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<namespace pattern part>");
    r = namePatternPart(b, l + 1);
    if (!r) r = consumeToken(b, T_DOUBLE_ASTERISK);
    exit_section_(b, l, m, NAMESPACE_PATTERN_PART, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // brakedExpression
  //   | '!' brakedExpression
  public static boolean negatedExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "negatedExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<negated expression>");
    r = brakedExpression(b, l + 1);
    if (!r) r = negatedExpression_1(b, l + 1);
    exit_section_(b, l, m, NEGATED_EXPRESSION, r, false, null);
    return r;
  }

  // '!' brakedExpression
  private static boolean negatedExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "negatedExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, T_NEGATION);
    r = r && brakedExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (conjugatedExpression '||' pointcutExpression)
  //   | conjugatedExpression
  public static boolean pointcutExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcutExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<pointcut expression>");
    r = pointcutExpression_0(b, l + 1);
    if (!r) r = conjugatedExpression(b, l + 1);
    exit_section_(b, l, m, POINTCUT_EXPRESSION, r, false, null);
    return r;
  }

  // conjugatedExpression '||' pointcutExpression
  private static boolean pointcutExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcutExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = conjugatedExpression(b, l + 1);
    r = r && consumeToken(b, T_LOGICAL_OR);
    r = r && pointcutExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // pointcutExpression
  static boolean pointcutFile(PsiBuilder b, int l) {
    return pointcutExpression(b, l + 1);
  }

  /* ********************************************************** */
  // [namespaceName|'$this'] '->' namePatternPart
  public static boolean pointcutReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcutReference")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<pointcut reference>");
    r = pointcutReference_0(b, l + 1);
    r = r && consumeToken(b, T_OBJECT_ACCESS);
    r = r && namePatternPart(b, l + 1);
    exit_section_(b, l, m, POINTCUT_REFERENCE, r, false, null);
    return r;
  }

  // [namespaceName|'$this']
  private static boolean pointcutReference_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcutReference_0")) return false;
    pointcutReference_0_0(b, l + 1);
    return true;
  }

  // namespaceName|'$this'
  private static boolean pointcutReference_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcutReference_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceName(b, l + 1);
    if (!r) r = consumeToken(b, T_THIS);
    exit_section_(b, m, null, r);
    return r;
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
  public static boolean singlePointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "singlePointcut")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<single pointcut>");
    r = accessPointcut(b, l + 1);
    if (!r) r = annotatedAccessPointcut(b, l + 1);
    if (!r) r = executionPointcut(b, l + 1);
    if (!r) r = annotatedExecutionPointcut(b, l + 1);
    if (!r) r = withinPointcut(b, l + 1);
    if (!r) r = annotatedWithinPointcut(b, l + 1);
    if (!r) r = initializationPointcut(b, l + 1);
    if (!r) r = staticInitializationPointcut(b, l + 1);
    if (!r) r = cflowbelowPointcut(b, l + 1);
    if (!r) r = dynamicExecutionPointcut(b, l + 1);
    if (!r) r = matchInheritedPointcut(b, l + 1);
    if (!r) r = pointcutReference(b, l + 1);
    exit_section_(b, l, m, SINGLE_POINTCUT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // staticinitialization '(' classFilter ')'
  public static boolean staticInitializationPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "staticInitializationPointcut")) return false;
    if (!nextTokenIs(b, STATICINITIALIZATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STATICINITIALIZATION);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && classFilter(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, STATIC_INITIALIZATION_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // within '(' classFilter ')'
  public static boolean withinPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "withinPointcut")) return false;
    if (!nextTokenIs(b, WITHIN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WITHIN);
    r = r && consumeToken(b, T_LEFT_PAREN);
    r = r && classFilter(b, l + 1);
    r = r && consumeToken(b, T_RIGHT_PAREN);
    exit_section_(b, m, WITHIN_POINTCUT, r);
    return r;
  }

}
