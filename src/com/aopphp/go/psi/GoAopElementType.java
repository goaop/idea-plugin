package com.aopphp.go.psi;

import com.aopphp.go.GoAopLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GoAopElementType extends IElementType {
    public GoAopElementType(@NotNull @NonNls String debugName) {
        super(debugName, GoAopLanguage.INSTANCE);
    }
}
