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
import com.aopphp.go.psiutil.PointcutPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpModifier.State;

public class MemberAccessTypeImpl extends ASTWrapperPsiElement implements MemberAccessType {

  public MemberAccessTypeImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull Visitor visitor) {
    visitor.visitMemberAccessType(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) accept((Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  public State getMemberAccessType() {
    return PointcutPsiUtil.getMemberAccessType(this);
  }

}
