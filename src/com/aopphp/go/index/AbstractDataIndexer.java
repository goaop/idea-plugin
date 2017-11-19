package com.aopphp.go.index;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import gnu.trove.THashMap;

import java.util.Map;

abstract class AbstractDataIndexer<T> {

    private final Map<String, T> map = new THashMap<>();
    private final PhpFile phpFile;

    AbstractDataIndexer(final PhpFile phpFile) {
        this.phpFile = phpFile;
    }

    Map<String, T> map() {
        for (final PhpNamedElement element : phpFile.getTopLevelDefs().values()) {
            if (element instanceof Function) {
                visitPhpNamedElement(element, map);
            } else if (element instanceof PhpClass) {
                for (PhpClassMember member : PsiTreeUtil.getChildrenOfTypeAsList(element, PhpClassMember.class)) {
                    visitPhpNamedElement(member, map);
                }
            }
        }
        return map;
    }

    private void visitPhpNamedElement(final PhpNamedElement element, final Map<String, T> map) {
        final PhpDocComment docComment = element.getDocComment();
        if (docComment != null) {
            for (final PhpDocTag phpDocTag : PsiTreeUtil.getChildrenOfTypeAsList(docComment, PhpDocTag.class)) {
                visitPhpDocTag(phpDocTag, map);
            }
        }
    }

    protected abstract void visitPhpDocTag(final PhpDocTag phpDocTag, final Map<String, T> map);
}
