// This is a generated file. Not intended for manual editing.
package com.aopphp.go.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.aopphp.go.psi.PointcutTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.aopphp.go.psi.*;

public class MemberReferenceImpl extends ASTWrapperPsiElement implements MemberReference {

  public MemberReferenceImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitMemberReference(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public ClassFilter getClassFilter() {
    return findNotNullChildByClass(ClassFilter.class);
  }

  @Override
  @NotNull
  public MemberAccessType getMemberAccessType() {
    return findNotNullChildByClass(MemberAccessType.class);
  }

  @Override
  @NotNull
  public MemberModifiers getMemberModifiers() {
    return findNotNullChildByClass(MemberModifiers.class);
  }

  @Override
  @NotNull
  public NamePattern getNamePattern() {
    return findNotNullChildByClass(NamePattern.class);
  }

}
