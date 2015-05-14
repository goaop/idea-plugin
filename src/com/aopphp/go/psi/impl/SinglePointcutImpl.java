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

public class SinglePointcutImpl extends ASTWrapperPsiElement implements SinglePointcut {

  public SinglePointcutImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof Visitor) ((Visitor)visitor).visitSinglePointcut(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AccessPointcut getAccessPointcut() {
    return findChildByClass(AccessPointcut.class);
  }

  @Override
  @Nullable
  public AnnotatedAccessPointcut getAnnotatedAccessPointcut() {
    return findChildByClass(AnnotatedAccessPointcut.class);
  }

  @Override
  @Nullable
  public AnnotatedExecutionPointcut getAnnotatedExecutionPointcut() {
    return findChildByClass(AnnotatedExecutionPointcut.class);
  }

  @Override
  @Nullable
  public AnnotatedWithinPointcut getAnnotatedWithinPointcut() {
    return findChildByClass(AnnotatedWithinPointcut.class);
  }

  @Override
  @Nullable
  public CflowbelowPointcut getCflowbelowPointcut() {
    return findChildByClass(CflowbelowPointcut.class);
  }

  @Override
  @Nullable
  public DynamicExecutionPointcut getDynamicExecutionPointcut() {
    return findChildByClass(DynamicExecutionPointcut.class);
  }

  @Override
  @Nullable
  public ExecutionPointcut getExecutionPointcut() {
    return findChildByClass(ExecutionPointcut.class);
  }

  @Override
  @Nullable
  public InitializationPointcut getInitializationPointcut() {
    return findChildByClass(InitializationPointcut.class);
  }

  @Override
  @Nullable
  public PointcutReference getPointcutReference() {
    return findChildByClass(PointcutReference.class);
  }

  @Override
  @Nullable
  public StaticInitializationPointcut getStaticInitializationPointcut() {
    return findChildByClass(StaticInitializationPointcut.class);
  }

  @Override
  @Nullable
  public WithinPointcut getWithinPointcut() {
    return findChildByClass(WithinPointcut.class);
  }

}
