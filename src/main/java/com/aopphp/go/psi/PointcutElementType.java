package com.aopphp.go.psi;

import com.aopphp.go.PointcutQueryLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class PointcutElementType extends IElementType {
    public PointcutElementType(@NotNull @NonNls String debugName) {
        super(debugName, PointcutQueryLanguage.INSTANCE);
    }
}
