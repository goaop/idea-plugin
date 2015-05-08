package com.aopphp.go.psi;

import com.aopphp.go.PointcutQueryLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class PointcutTokenType extends IElementType {
    public PointcutTokenType(@NotNull @NonNls String debugName) {
        super(debugName, PointcutQueryLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "PointcutTokenType." + super.toString();
    }
}

