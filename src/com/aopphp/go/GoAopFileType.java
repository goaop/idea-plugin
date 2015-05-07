package com.aopphp.go;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoAopFileType extends LanguageFileType {
    public static final GoAopFileType INSTANCE = new GoAopFileType();

    private GoAopFileType() {
        super(GoAopLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Go! AOP Pointcut";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Go! AOP Pointcut Expression Syntax";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return ".goaop";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GoAopIcons.FILE;
    }
}