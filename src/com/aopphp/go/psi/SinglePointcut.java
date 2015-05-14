// This is a generated file. Not intended for manual editing.
package com.aopphp.go.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SinglePointcut extends PsiElement {

  @Nullable
  AccessPointcut getAccessPointcut();

  @Nullable
  AnnotatedAccessPointcut getAnnotatedAccessPointcut();

  @Nullable
  AnnotatedExecutionPointcut getAnnotatedExecutionPointcut();

  @Nullable
  AnnotatedWithinPointcut getAnnotatedWithinPointcut();

  @Nullable
  CflowbelowPointcut getCflowbelowPointcut();

  @Nullable
  DynamicExecutionPointcut getDynamicExecutionPointcut();

  @Nullable
  ExecutionPointcut getExecutionPointcut();

  @Nullable
  InitializationPointcut getInitializationPointcut();

  @Nullable
  PointcutReference getPointcutReference();

  @Nullable
  StaticInitializationPointcut getStaticInitializationPointcut();

  @Nullable
  WithinPointcut getWithinPointcut();

}
