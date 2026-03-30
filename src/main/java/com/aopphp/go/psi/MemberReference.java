// This is a generated file. Not intended for manual editing.
package com.aopphp.go.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.aopphp.go.pointcut.ClassMemberReference;

public interface MemberReference extends PsiElement {

  @NotNull
  ClassFilter getClassFilter();

  @NotNull
  MemberAccessType getMemberAccessType();

  @NotNull
  MemberModifiers getMemberModifiers();

  @NotNull
  NamePattern getNamePattern();

  ClassMemberReference getClassMemberReference();

}
