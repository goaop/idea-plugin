package com.aopphp.go.index;

import com.aopphp.go.psi.PointcutElementFactory;
import com.aopphp.go.psi.PointcutExpression;
import com.aopphp.go.psi.impl.PointcutQueryUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

/**
 * This index collects all pointcuts from the annotations in PHP files
 */
public class AnnotationPointcutExpressionIndex extends FileBasedIndexExtension<String, String> {

    public static final ID<String, String> KEY = ID.create("com.aopphp.go.annotation.pointcuts");
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, String> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, String, FileContent> getIndexer() {
        return new DataIndexer<String, String, FileContent>() {
            @NotNull
            @Override
            public Map<String, String> map(@NotNull FileContent inputData) {
                final Map<String, String> map = new THashMap<String, String>();

                PsiFile psiFile = inputData.getPsiFile();
                if(!(psiFile instanceof PhpFile)) {
                    return map;
                }

                psiFile.accept(new DocTagRecursiveElementVisitor(map));

                return map;
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<String> getValueExternalizer() {
        return new DataExternalizer<String>() {
            @Override
            public void save(@NotNull DataOutput out, String value) throws IOException {
                out.writeUTF(value);
            }

            @Override
            public String read(@NotNull DataInput in) throws IOException {
                return in.readUTF();
            }
        };
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return PhpConstantNameIndex.PHP_INPUT_FILTER;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    private static class DocTagRecursiveElementVisitor extends PsiRecursiveElementWalkingVisitor {

        private final Map<String, String> map;
        private Map<String, String> fileImports;

        public DocTagRecursiveElementVisitor(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public void visitElement(PsiElement element) {
            if ((element instanceof PhpDocTag)) {
                visitPhpDocTag((PhpDocTag) element);
            }
            super.visitElement(element);
        }

        public void visitPhpDocTag(PhpDocTag phpDocTag) {

            // init file imports
            if(this.fileImports == null) {
                this.fileImports = PointcutQueryUtil.getFileUseImports(phpDocTag.getContainingFile());
            }

            if(this.fileImports.size() == 0) {
                return;
            }

            String annotationFqnName = PointcutQueryUtil.getClassNameReference(phpDocTag, this.fileImports);
            if(annotationFqnName == null || !annotationFqnName.startsWith("\\Go\\Lang\\Annotation\\")) {
                return;
            }

            StringLiteralExpression expression = PsiTreeUtil.findChildOfType(phpDocTag, StringLiteralExpression.class);
            if (expression == null) {
                return;
            }
            String unquotedExpression = StringUtil.trimEnd(expression.getText(), "\"").substring(1);

            PointcutExpression pointcutExpression = PointcutElementFactory.createPointcut(
                phpDocTag.getProject(),
                unquotedExpression
            );

            if (pointcutExpression != null) {
                map.put(phpDocTag.getText(), annotationFqnName);
            }
        }
    }
}

