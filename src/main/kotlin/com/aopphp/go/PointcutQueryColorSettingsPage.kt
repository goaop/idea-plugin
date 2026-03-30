package com.aopphp.go

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class PointcutQueryColorSettingsPage : ColorSettingsPage {

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Keyword",         PointcutQuerySyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Separator",       PointcutQuerySyntaxHighlighter.SEPARATOR),
            AttributesDescriptor("Comment",         PointcutQuerySyntaxHighlighter.COMMENT),
            AttributesDescriptor("Member modifier", PointcutQuerySyntaxHighlighter.MEMBER_MODIFIER),
            AttributesDescriptor("Parenthesis",     PointcutQuerySyntaxHighlighter.PARENTHESIS),
        )
    }

    override fun getIcon(): Icon = GoAopIcons.FILE
    override fun getHighlighter() = PointcutQuerySyntaxHighlighter()
    override fun getDemoText() = "execution (public|protected Some\\Name*\\**->methodName(*))"
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null
    override fun getAttributeDescriptors() = DESCRIPTORS
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getDisplayName() = "Go! AOP Pointcut query"
}
