// This is a generated file. Not intended for manual editing.
package com.aopphp.go.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.aopphp.go.psi.impl.*;

public interface PointcutTypes {

  IElementType ACCESS_POINTCUT = new PointcutElementType("ACCESS_POINTCUT");
  IElementType ANNOTATED_ACCESS_POINTCUT = new PointcutElementType("ANNOTATED_ACCESS_POINTCUT");
  IElementType ANNOTATED_EXECUTION_POINTCUT = new PointcutElementType("ANNOTATED_EXECUTION_POINTCUT");
  IElementType ANNOTATED_WITHIN_POINTCUT = new PointcutElementType("ANNOTATED_WITHIN_POINTCUT");
  IElementType ARGUMENT_LIST = new PointcutElementType("ARGUMENT_LIST");
  IElementType BRAKED_EXPRESSION = new PointcutElementType("BRAKED_EXPRESSION");
  IElementType CFLOWBELOW_POINTCUT = new PointcutElementType("CFLOWBELOW_POINTCUT");
  IElementType CLASS_FILTER = new PointcutElementType("CLASS_FILTER");
  IElementType CONJUGATED_EXPRESSION = new PointcutElementType("CONJUGATED_EXPRESSION");
  IElementType DYNAMIC_EXECUTION_POINTCUT = new PointcutElementType("DYNAMIC_EXECUTION_POINTCUT");
  IElementType EXECUTION_POINTCUT = new PointcutElementType("EXECUTION_POINTCUT");
  IElementType FUNCTION_EXECUTION_REFERENCE = new PointcutElementType("FUNCTION_EXECUTION_REFERENCE");
  IElementType INITIALIZATION_POINTCUT = new PointcutElementType("INITIALIZATION_POINTCUT");
  IElementType MEMBER_ACCESS_TYPE = new PointcutElementType("MEMBER_ACCESS_TYPE");
  IElementType MEMBER_MODIFIER = new PointcutElementType("MEMBER_MODIFIER");
  IElementType MEMBER_MODIFIERS = new PointcutElementType("MEMBER_MODIFIERS");
  IElementType MEMBER_REFERENCE = new PointcutElementType("MEMBER_REFERENCE");
  IElementType METHOD_EXECUTION_REFERENCE = new PointcutElementType("METHOD_EXECUTION_REFERENCE");
  IElementType NAMESPACE_NAME = new PointcutElementType("NAMESPACE_NAME");
  IElementType NAMESPACE_PATTERN = new PointcutElementType("NAMESPACE_PATTERN");
  IElementType NAMESPACE_PATTERN_PART = new PointcutElementType("NAMESPACE_PATTERN_PART");
  IElementType NAME_PATTERN = new PointcutElementType("NAME_PATTERN");
  IElementType NAME_PATTERN_PART = new PointcutElementType("NAME_PATTERN_PART");
  IElementType NEGATED_EXPRESSION = new PointcutElementType("NEGATED_EXPRESSION");
  IElementType POINTCUT_EXPRESSION = new PointcutElementType("POINTCUT_EXPRESSION");
  IElementType POINTCUT_REFERENCE = new PointcutElementType("POINTCUT_REFERENCE");
  IElementType SINGLE_POINTCUT = new PointcutElementType("SINGLE_POINTCUT");
  IElementType STATIC_INITIALIZATION_POINTCUT = new PointcutElementType("STATIC_INITIALIZATION_POINTCUT");
  IElementType WITHIN_POINTCUT = new PointcutElementType("WITHIN_POINTCUT");

  IElementType ACCESS = new PointcutTokenType("access");
  IElementType CFLOWBELOW = new PointcutTokenType("cflowbelow");
  IElementType DYNAMIC = new PointcutTokenType("dynamic");
  IElementType EXECUTION = new PointcutTokenType("execution");
  IElementType FINAL = new PointcutTokenType("final");
  IElementType INITIALIZATION = new PointcutTokenType("initialization");
  IElementType PRIVATE = new PointcutTokenType("private");
  IElementType PROTECTED = new PointcutTokenType("protected");
  IElementType PUBLIC = new PointcutTokenType("public");
  IElementType STATICINITIALIZATION = new PointcutTokenType("staticinitialization");
  IElementType T_ALTERNATION = new PointcutTokenType("|");
  IElementType T_ANNOTATION = new PointcutTokenType("@");
  IElementType T_ASTERISK = new PointcutTokenType("*");
  IElementType T_COMMENT = new PointcutTokenType("T_COMMENT");
  IElementType T_DOUBLE_ASTERISK = new PointcutTokenType("**");
  IElementType T_LEFT_PAREN = new PointcutTokenType("(");
  IElementType T_LOGICAL_AND = new PointcutTokenType("&&");
  IElementType T_LOGICAL_OR = new PointcutTokenType("||");
  IElementType T_NAME_PART = new PointcutTokenType("T_NAME_PART");
  IElementType T_NEGATION = new PointcutTokenType("!");
  IElementType T_NS_SEPARATOR = new PointcutTokenType("\\");
  IElementType T_OBJECT_ACCESS = new PointcutTokenType("->");
  IElementType T_RIGHT_PAREN = new PointcutTokenType(")");
  IElementType T_STATIC_ACCESS = new PointcutTokenType("::");
  IElementType T_SUBNAMESPACE_SIGN = new PointcutTokenType("+");
  IElementType T_THIS = new PointcutTokenType("$this");
  IElementType WITHIN = new PointcutTokenType("within");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ACCESS_POINTCUT) {
        return new AccessPointcutImpl(node);
      }
      else if (type == ANNOTATED_ACCESS_POINTCUT) {
        return new AnnotatedAccessPointcutImpl(node);
      }
      else if (type == ANNOTATED_EXECUTION_POINTCUT) {
        return new AnnotatedExecutionPointcutImpl(node);
      }
      else if (type == ANNOTATED_WITHIN_POINTCUT) {
        return new AnnotatedWithinPointcutImpl(node);
      }
      else if (type == ARGUMENT_LIST) {
        return new ArgumentListImpl(node);
      }
      else if (type == BRAKED_EXPRESSION) {
        return new BrakedExpressionImpl(node);
      }
      else if (type == CFLOWBELOW_POINTCUT) {
        return new CflowbelowPointcutImpl(node);
      }
      else if (type == CLASS_FILTER) {
        return new ClassFilterImpl(node);
      }
      else if (type == CONJUGATED_EXPRESSION) {
        return new ConjugatedExpressionImpl(node);
      }
      else if (type == DYNAMIC_EXECUTION_POINTCUT) {
        return new DynamicExecutionPointcutImpl(node);
      }
      else if (type == EXECUTION_POINTCUT) {
        return new ExecutionPointcutImpl(node);
      }
      else if (type == FUNCTION_EXECUTION_REFERENCE) {
        return new FunctionExecutionReferenceImpl(node);
      }
      else if (type == INITIALIZATION_POINTCUT) {
        return new InitializationPointcutImpl(node);
      }
      else if (type == MEMBER_ACCESS_TYPE) {
        return new MemberAccessTypeImpl(node);
      }
      else if (type == MEMBER_MODIFIER) {
        return new MemberModifierImpl(node);
      }
      else if (type == MEMBER_MODIFIERS) {
        return new MemberModifiersImpl(node);
      }
      else if (type == MEMBER_REFERENCE) {
        return new MemberReferenceImpl(node);
      }
      else if (type == METHOD_EXECUTION_REFERENCE) {
        return new MethodExecutionReferenceImpl(node);
      }
      else if (type == NAMESPACE_NAME) {
        return new NamespaceNameImpl(node);
      }
      else if (type == NAMESPACE_PATTERN) {
        return new NamespacePatternImpl(node);
      }
      else if (type == NAMESPACE_PATTERN_PART) {
        return new NamespacePatternPartImpl(node);
      }
      else if (type == NAME_PATTERN) {
        return new NamePatternImpl(node);
      }
      else if (type == NAME_PATTERN_PART) {
        return new NamePatternPartImpl(node);
      }
      else if (type == NEGATED_EXPRESSION) {
        return new NegatedExpressionImpl(node);
      }
      else if (type == POINTCUT_EXPRESSION) {
        return new PointcutExpressionImpl(node);
      }
      else if (type == POINTCUT_REFERENCE) {
        return new PointcutReferenceImpl(node);
      }
      else if (type == SINGLE_POINTCUT) {
        return new SinglePointcutImpl(node);
      }
      else if (type == STATIC_INITIALIZATION_POINTCUT) {
        return new StaticInitializationPointcutImpl(node);
      }
      else if (type == WITHIN_POINTCUT) {
        return new WithinPointcutImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
