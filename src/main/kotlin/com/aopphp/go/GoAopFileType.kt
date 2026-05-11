package com.aopphp.go

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object GoAopFileType : LanguageFileType(PointcutQueryLanguage) {
    override fun getName() = "Go! AOP Pointcut"
    override fun getDescription() = "Go! AOP Pointcut Expression Syntax"
    override fun getDefaultExtension() = "goaop"
    override fun getIcon(): Icon = GoAopIcons.FILE
}
