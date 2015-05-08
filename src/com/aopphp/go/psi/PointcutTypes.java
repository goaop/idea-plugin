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
  IElementType CFLOWBELOW_POINTCUT = new PointcutElementType("CFLOWBELOW_POINTCUT");
  IElementType CLASS_FILTER = new PointcutElementType("CLASS_FILTER");
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
  IElementType NAME_PATTERN_PART = new PointcutElementType("NAME_PATTERN_PART");
  IElementType POINTCUT_REFERENCE = new PointcutElementType("POINTCUT_REFERENCE");
  IElementType PROPERTY_ACCESS_REFERENCE = new PointcutElementType("PROPERTY_ACCESS_REFERENCE");
  IElementType STATIC_INITIALIZATION_POINTCUT = new PointcutElementType("STATIC_INITIALIZATION_POINTCUT");
  IElementType WITHIN_POINTCUT = new PointcutElementType("WITHIN_POINTCUT");

  IElementType ACCESS = new PointcutTokenType("access");
  IElementType ANNOTATION = new PointcutTokenType("@");
  IElementType CFLOWBELOW = new PointcutTokenType("cflowbelow");
  IElementType COMMENT = new PointcutTokenType("comment");
  IElementType DYNAMIC = new PointcutTokenType("dynamic");
  IElementType EXECUTION = new PointcutTokenType("execution");
  IElementType FINAL = new PointcutTokenType("final");
  IElementType INITIALIZATION = new PointcutTokenType("initialization");
  IElementType NAMEPART = new PointcutTokenType("namePart");
  IElementType NSSEPARATOR = new PointcutTokenType("\\");
  IElementType PRIVATE = new PointcutTokenType("private");
  IElementType PROTECTED = new PointcutTokenType("protected");
  IElementType PUBLIC = new PointcutTokenType("public");
  IElementType STATICINITIALIZATION = new PointcutTokenType("staticinitialization");
  IElementType STRING = new PointcutTokenType("string");
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
      else if (type == CFLOWBELOW_POINTCUT) {
        return new CflowbelowPointcutImpl(node);
      }
      else if (type == CLASS_FILTER) {
        return new ClassFilterImpl(node);
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
      else if (type == NAME_PATTERN_PART) {
        return new NamePatternPartImpl(node);
      }
      else if (type == POINTCUT_REFERENCE) {
        return new PointcutReferenceImpl(node);
      }
      else if (type == PROPERTY_ACCESS_REFERENCE) {
        return new PropertyAccessReferenceImpl(node);
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
