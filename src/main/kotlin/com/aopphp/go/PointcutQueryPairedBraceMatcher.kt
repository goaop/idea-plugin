package com.aopphp.go

import com.aopphp.go.psi.PointcutTypes
import com.intellij.codeInsight.highlighting.PairedBraceMatcherAdapter
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class PointcutQueryPairedBraceMatcher : PairedBraceMatcherAdapter(PointcutBraceMatcher(), PointcutQueryLanguage) {

    private class PointcutBraceMatcher : PairedBraceMatcher {
        private val pairs = arrayOf(BracePair(PointcutTypes.T_LEFT_PAREN, PointcutTypes.T_RIGHT_PAREN, false))

        override fun getPairs() = pairs

        override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, tokenType: IElementType?) = true

        override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int {
            val element = file.findElementAt(openingBraceOffset) ?: return openingBraceOffset
            if (element is PsiFile) return openingBraceOffset
            return element.parent.textRange.startOffset
        }
    }
}
