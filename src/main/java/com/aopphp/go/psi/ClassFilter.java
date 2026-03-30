// This is a generated file. Not intended for manual editing.
package com.aopphp.go.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.aopphp.go.pointcut.PointFilter;

public interface ClassFilter extends PsiElement {

  @NotNull
  NamespacePattern getNamespacePattern();

  PointFilter getClassFilterMatcher();

}
