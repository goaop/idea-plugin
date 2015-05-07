package com.aopphp.go.psi;

import com.aopphp.go.GoAopLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class GoAopTokenType extends IElementType {
    public GoAopTokenType(@NotNull @NonNls String debugName) {
        super(debugName, GoAopLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "GoAopTokenType." + super.toString();
    }
}

