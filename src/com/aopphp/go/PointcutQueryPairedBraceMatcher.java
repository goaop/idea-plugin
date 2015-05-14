package com.aopphp.go;

import com.aopphp.go.psi.PointcutTypes;
import com.intellij.codeInsight.highlighting.PairedBraceMatcherAdapter;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PointcutQueryPairedBraceMatcher extends PairedBraceMatcherAdapter
{
    private static final BracePair[] PAIRS = { new BracePair(PointcutTypes.LP, PointcutTypes.RP, false)};

    public PointcutQueryPairedBraceMatcher() {
        super(new PointcutPairedBraceMatcher(), PointcutQueryLanguage.INSTANCE);
    }

    private static class PointcutPairedBraceMatcher implements PairedBraceMatcher {
        private PointcutPairedBraceMatcher() {}

        public BracePair[] getPairs() { return PointcutQueryPairedBraceMatcher.PAIRS; }

        public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType tokenType)
        {
            return true;
        }

        public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
            PsiElement element = file.findElementAt(openingBraceOffset);
            if ((element == null) || ((element instanceof PsiFile))) {
                return openingBraceOffset;
            }

            PsiElement parent = element.getParent();

            return parent.getTextRange().getStartOffset();
        }
    }
}
