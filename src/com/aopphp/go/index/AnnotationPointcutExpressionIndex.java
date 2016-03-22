package com.aopphp.go.index;

import com.aopphp.go.pointcut.Pointcut;
import com.aopphp.go.psi.PointcutElementFactory;
import com.aopphp.go.psi.PointcutExpression;
import com.aopphp.go.util.PointcutQueryUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Map;

/**
 * This index collects all pointcuts from the annotations in PHP files
 */
public class AnnotationPointcutExpressionIndex extends FileBasedIndexExtension<String, Pointcut> {

    public static final ID<String, Pointcut> KEY = ID.create("com.aopphp.go.annotation.pointcuts");
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, Pointcut> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Pointcut, FileContent> getIndexer() {
        return new DataIndexer<String, Pointcut, FileContent>() {
            @NotNull
            @Override
            public Map<String, Pointcut> map(@NotNull FileContent inputData) {
                final Map<String, Pointcut> map = new THashMap<String, Pointcut>();

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
        return this.keyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Pointcut> getValueExternalizer() {
        return new DataExternalizer<Pointcut>() {
            @Override
            public void save(@NotNull DataOutput out, Pointcut value) throws IOException {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ObjectOutput output          = new ObjectOutputStream(stream);
                output.writeObject(value);

                out.writeInt(stream.size());
                out.write(stream.toByteArray());
            }

            @Override
            public Pointcut read(@NotNull DataInput in) throws IOException {
                int bufferSize = in.readInt();
                byte[] buffer  = new byte[bufferSize];
                in.readFully(buffer, 0, bufferSize);

                ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
                ObjectInput input           = new ObjectInputStream(stream);

                Pointcut pointcut = null;
                try {
                    pointcut = (Pointcut) input.readObject();
                } catch (ClassNotFoundException e) {
                    // nothing here, just ignore
                }

                return pointcut;
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

        private final Map<String, Pointcut> map;

        public DocTagRecursiveElementVisitor(Map<String, Pointcut> map) {
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

            String annotationFqnName = PointcutQueryUtil.getClassNameReference(phpDocTag);
            if(annotationFqnName == null || !annotationFqnName.startsWith("\\Go\\Lang\\Annotation\\")) {
                return;
            }

            // searching for the concrete textual pointcut definition
            StringLiteralExpression expression = PsiTreeUtil.findChildOfType(phpDocTag, StringLiteralExpression.class);
            if (expression == null) {
                return;
            }
            String unquotedExpression = StringUtil.trimEnd(expression.getText(), "\"").substring(1);

            PointcutExpression pointcutExpression = PointcutElementFactory.createPointcut(
                phpDocTag.getProject(),
                unquotedExpression
            );

            if (pointcutExpression == null) {
                return;
            }

            // | - PhpDocComment
            // |   | - PhpDocTag
            // |
            // | - PhpNamedElement

            PsiElement docComment = PsiTreeUtil.getParentOfType(phpDocTag, PhpDocComment.class);
            if (docComment == null) {
                return;
            }
            PhpNamedElement phpNamedElement = PsiTreeUtil.getNextSiblingOfType(docComment, PhpNamedElement.class);
            if (phpNamedElement == null) {
                return;
            }

            Pointcut pointcut = pointcutExpression.compile();
            map.put(phpNamedElement.getFQN(), pointcut);
        }
    }
}

