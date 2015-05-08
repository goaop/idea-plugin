package com.aopphp.go.psi;


import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.aopphp.go.GoAopFileType;
import com.aopphp.go.PointcutQueryLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PointcutFile extends PsiFileBase {
    public PointcutFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, PointcutQueryLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return GoAopFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Go! AOP Pointcut File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
