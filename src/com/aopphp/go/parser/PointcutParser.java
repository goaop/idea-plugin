// This is a generated file. Not intended for manual editing.
package com.aopphp.go.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.aopphp.go.psi.PointcutTypes.*;
import static com.aopphp.go.parser.PointcutParserUtil.*;
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
    else if (t == CFLOWBELOW_POINTCUT) {
      r = cflowbelowPointcut(b, 0);
    }
    else if (t == CLASS_FILTER) {
      r = classFilter(b, 0);
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
    else if (t == NAME_PATTERN_PART) {
      r = namePatternPart(b, 0);
    }
    else if (t == NAMESPACE_NAME) {
      r = namespaceName(b, 0);
    }
    else if (t == NAMESPACE_PATTERN) {
      r = namespacePattern(b, 0);
    }
    else if (t == POINTCUT_REFERENCE) {
      r = pointcutReference(b, 0);
    }
    else if (t == PROPERTY_ACCESS_REFERENCE) {
      r = propertyAccessReference(b, 0);
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
    return singlePointcut(b, l + 1);
  }

  /* ********************************************************** */
  // access '(' propertyAccessReference ')'
  public static boolean accessPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "accessPointcut")) return false;
    if (!nextTokenIs(b, ACCESS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ACCESS);
    r = r && consumeToken(b, "(");
    r = r && propertyAccessReference(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, ACCESS_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // annotation access '(' namespaceName ')'
  public static boolean annotatedAccessPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotatedAccessPointcut")) return false;
    if (!nextTokenIs(b, ANNOTATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ANNOTATION, ACCESS);
    r = r && consumeToken(b, "(");
    r = r && namespaceName(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, ANNOTATED_ACCESS_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // annotation execution '(' namespaceName ')'
  public static boolean annotatedExecutionPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotatedExecutionPointcut")) return false;
    if (!nextTokenIs(b, ANNOTATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ANNOTATION, EXECUTION);
    r = r && consumeToken(b, "(");
    r = r && namespaceName(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, ANNOTATED_EXECUTION_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // annotation within '(' namespaceName ')'
  public static boolean annotatedWithinPointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "annotatedWithinPointcut")) return false;
    if (!nextTokenIs(b, ANNOTATION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ANNOTATION, WITHIN);
    r = r && consumeToken(b, "(");
    r = r && namespaceName(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, ANNOTATED_WITHIN_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // '*'
  public static boolean argumentList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argumentList")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<argument list>");
    r = consumeToken(b, "*");
    exit_section_(b, l, m, ARGUMENT_LIST, r, false, null);
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
    r = r && consumeToken(b, "(");
    r = r && executionPointcut(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, CFLOWBELOW_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // namespacePattern
  //   | namespacePattern '+'
  public static boolean classFilter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classFilter")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<class filter>");
    r = namespacePattern(b, l + 1);
    if (!r) r = classFilter_1(b, l + 1);
    exit_section_(b, l, m, CLASS_FILTER, r, false, null);
    return r;
  }

  // namespacePattern '+'
  private static boolean classFilter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classFilter_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespacePattern(b, l + 1);
    r = r && consumeToken(b, "+");
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
    r = r && consumeToken(b, "(");
    r = r && memberReference(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && argumentList(b, l + 1);
    r = r && consumeToken(b, ")");
    r = r && consumeToken(b, ")");
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
    r = r && consumeToken(b, "(");
    r = r && methodExecutionReference(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  // execution '(' functionExecutionReference ')'
  private static boolean executionPointcut_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "executionPointcut_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXECUTION);
    r = r && consumeToken(b, "(");
    r = r && functionExecutionReference(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namespacePattern nsSeparator namePatternPart '(' argumentList ')'
  public static boolean functionExecutionReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functionExecutionReference")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<function execution reference>");
    r = namespacePattern(b, l + 1);
    r = r && consumeToken(b, NSSEPARATOR);
    r = r && namePatternPart(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && argumentList(b, l + 1);
    r = r && consumeToken(b, ")");
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
    r = r && consumeToken(b, "(");
    r = r && classFilter(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, INITIALIZATION_POINTCUT, r);
    return r;
  }

  /* ********************************************************** */
  // '::'|'->'
  public static boolean memberAccessType(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberAccessType")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<member access type>");
    r = consumeToken(b, "::");
    if (!r) r = consumeToken(b, "->");
    exit_section_(b, l, m, MEMBER_ACCESS_TYPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // public|protected|private|final
  public static boolean memberModifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberModifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<member modifier>");
    r = consumeToken(b, PUBLIC);
    if (!r) r = consumeToken(b, PROTECTED);
    if (!r) r = consumeToken(b, PRIVATE);
    if (!r) r = consumeToken(b, FINAL);
    exit_section_(b, l, m, MEMBER_MODIFIER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // memberModifier '|' memberModifiers
  //   | memberModifier ' ' memberModifiers
  //   | memberModifier
  public static boolean memberModifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberModifiers")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<member modifiers>");
    r = memberModifiers_0(b, l + 1);
    if (!r) r = memberModifiers_1(b, l + 1);
    if (!r) r = memberModifier(b, l + 1);
    exit_section_(b, l, m, MEMBER_MODIFIERS, r, false, null);
    return r;
  }

  // memberModifier '|' memberModifiers
  private static boolean memberModifiers_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberModifiers_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = memberModifier(b, l + 1);
    r = r && consumeToken(b, "|");
    r = r && memberModifiers(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // memberModifier ' ' memberModifiers
  private static boolean memberModifiers_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberModifiers_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = memberModifier(b, l + 1);
    r = r && consumeToken(b, " ");
    r = r && memberModifiers(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // memberModifiers classFilter memberAccessType namePatternPart
  public static boolean memberReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memberReference")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<member reference>");
    r = memberModifiers(b, l + 1);
    r = r && classFilter(b, l + 1);
    r = r && memberAccessType(b, l + 1);
    r = r && namePatternPart(b, l + 1);
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
    r = r && consumeToken(b, "(");
    r = r && argumentList(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, METHOD_EXECUTION_REFERENCE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '*'
  //   | '*' namePatternPart
  //   | namePart
  //   | namePart namePatternPart
  //   | namePart '|' namePatternPart
  public static boolean namePatternPart(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePatternPart")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<name pattern part>");
    r = consumeToken(b, "*");
    if (!r) r = namePatternPart_1(b, l + 1);
    if (!r) r = consumeToken(b, NAMEPART);
    if (!r) r = namePatternPart_3(b, l + 1);
    if (!r) r = namePatternPart_4(b, l + 1);
    exit_section_(b, l, m, NAME_PATTERN_PART, r, false, null);
    return r;
  }

  // '*' namePatternPart
  private static boolean namePatternPart_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePatternPart_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "*");
    r = r && namePatternPart(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // namePart namePatternPart
  private static boolean namePatternPart_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePatternPart_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAMEPART);
    r = r && namePatternPart(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // namePart '|' namePatternPart
  private static boolean namePatternPart_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namePatternPart_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAMEPART);
    r = r && consumeToken(b, "|");
    r = r && namePatternPart(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namePart
  //   | namePart nsSeparator namespaceName
  public static boolean namespaceName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceName")) return false;
    if (!nextTokenIs(b, NAMEPART)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAMEPART);
    if (!r) r = namespaceName_1(b, l + 1);
    exit_section_(b, m, NAMESPACE_NAME, r);
    return r;
  }

  // namePart nsSeparator namespaceName
  private static boolean namespaceName_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespaceName_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NAMEPART, NSSEPARATOR);
    r = r && namespaceName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '**'
  //   | namePatternPart
  //   | namePatternPart nsSeparator namespacePattern
  //   | namePatternPart nsSeparator '**'
  public static boolean namespacePattern(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespacePattern")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<namespace pattern>");
    r = consumeToken(b, "**");
    if (!r) r = namePatternPart(b, l + 1);
    if (!r) r = namespacePattern_2(b, l + 1);
    if (!r) r = namespacePattern_3(b, l + 1);
    exit_section_(b, l, m, NAMESPACE_PATTERN, r, false, null);
    return r;
  }

  // namePatternPart nsSeparator namespacePattern
  private static boolean namespacePattern_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespacePattern_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namePatternPart(b, l + 1);
    r = r && consumeToken(b, NSSEPARATOR);
    r = r && namespacePattern(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // namePatternPart nsSeparator '**'
  private static boolean namespacePattern_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespacePattern_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namePatternPart(b, l + 1);
    r = r && consumeToken(b, NSSEPARATOR);
    r = r && consumeToken(b, "**");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // namespaceName '->' namePatternPart
  public static boolean pointcutReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pointcutReference")) return false;
    if (!nextTokenIs(b, NAMEPART)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namespaceName(b, l + 1);
    r = r && consumeToken(b, "->");
    r = r && namePatternPart(b, l + 1);
    exit_section_(b, m, POINTCUT_REFERENCE, r);
    return r;
  }

  /* ********************************************************** */
  // memberReference
  public static boolean propertyAccessReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "propertyAccessReference")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<property access reference>");
    r = memberReference(b, l + 1);
    exit_section_(b, l, m, PROPERTY_ACCESS_REFERENCE, r, false, null);
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
  //   | pointcutReference
  static boolean singlePointcut(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "singlePointcut")) return false;
    boolean r;
    Marker m = enter_section_(b);
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
    if (!r) r = pointcutReference(b, l + 1);
    exit_section_(b, m, null, r);
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
    r = r && consumeToken(b, "(");
    r = r && classFilter(b, l + 1);
    r = r && consumeToken(b, ")");
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
    r = r && consumeToken(b, "(");
    r = r && classFilter(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, WITHIN_POINTCUT, r);
    return r;
  }

}
