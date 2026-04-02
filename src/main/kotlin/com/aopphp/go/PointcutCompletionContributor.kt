package com.aopphp.go

import com.aopphp.go.completion.AttributeClassCompletionProvider
import com.aopphp.go.completion.MemberModifierCompletionProvider
import com.aopphp.go.completion.PointcutKeywordCompletionProvider
import com.aopphp.go.completion.SelfPointcutReferenceCompletionProvider
import com.aopphp.go.pattern.CodePattern
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType

class PointcutCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC, CodePattern.insideAnnotationPointcut(),    AttributeClassCompletionProvider())
        extend(CompletionType.BASIC, CodePattern.insidePointcutLanguage(),      PointcutKeywordCompletionProvider())
        extend(CompletionType.BASIC, CodePattern.startOfMemberModifiers(),      MemberModifierCompletionProvider())
        extend(CompletionType.BASIC, CodePattern.insidePointcutSelfReference(), SelfPointcutReferenceCompletionProvider())
    }
}
