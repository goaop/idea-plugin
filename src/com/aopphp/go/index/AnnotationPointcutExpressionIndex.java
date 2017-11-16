package com.aopphp.go.index;

import com.aopphp.go.pointcut.Pointcut;
import com.aopphp.go.psi.PointcutElementFactory;
import com.aopphp.go.psi.PointcutExpression;
import com.aopphp.go.util.PluginUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Collections;
import java.util.Map;

/**
 * This index collects all pointcuts from the annotations in PHP files
 */
public class AnnotationPointcutExpressionIndex extends FileBasedIndexExtension<String, Pointcut> {

    public static final ID<String, Pointcut> KEY = ID.create("com.aopphp.go.annotation.pointcuts");
    private static final KeyDescriptor<String> ENUMERATOR_STRING_DESCRIPTOR = new EnumeratorStringDescriptor();

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
                PsiFile psiFile = inputData.getPsiFile();
                if(!(psiFile instanceof PhpFile)) {
                    return Collections.emptyMap();
                }

                return new AbstractDataIndexer<Pointcut>((PhpFile) psiFile) {
                    @Override
                    protected void visitPhpDocTag(final PhpDocTag phpDocTag, final Map<String, Pointcut> map) {
                        AnnotationPointcutExpressionIndex.visitPhpDocTag(phpDocTag, map);
                    }
                }.map();
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return ENUMERATOR_STRING_DESCRIPTOR;
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
        return 2;
    }

    private static void visitPhpDocTag(final PhpDocTag phpDocTag, final Map<String, Pointcut> map) {
        String annotationFqnName = PluginUtil.getClassNameReference(phpDocTag);
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

        PhpDocComment docComment = PsiTreeUtil.getParentOfType(phpDocTag, PhpDocComment.class);
        if (docComment == null) {
            return;
        }

        PsiElement phpNamedElement = docComment.getOwner();
        if (phpNamedElement == null || !(phpNamedElement instanceof PhpNamedElement)) {
            return;
        }

        Pointcut pointcut = pointcutExpression.compile();
        map.put(((PhpNamedElement)phpNamedElement).getFQN(), pointcut);
    }
}
