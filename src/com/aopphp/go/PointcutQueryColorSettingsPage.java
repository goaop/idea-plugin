package com.aopphp.go;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class PointcutQueryColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Keyword", PointcutQuerySyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Separator", PointcutQuerySyntaxHighlighter.SEPARATOR),
            new AttributesDescriptor("Comment", PointcutQuerySyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Member modifier", PointcutQuerySyntaxHighlighter.MEMBER_MODIFIER),
            new AttributesDescriptor("Parenthesis", PointcutQuerySyntaxHighlighter.PARENTHESIS),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return GoAopIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new PointcutQuerySyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "execution (public|protected Some\\Name*\\**->methodName(*))";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Go! AOP Pointcut query";
    }
}