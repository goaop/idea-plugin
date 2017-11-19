package com.aopphp.go.index;

import com.aopphp.go.util.PluginUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ReflectionUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex;
import com.jetbrains.php.lang.psi.stubs.indexes.StringSetDataExternalizer;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This index collects all annotated elements in the files
 */
public class AnnotatedPhpNamedElementIndex extends FileBasedIndexExtension<String, Set<String>> {

    public static final ID<String, Set<String>> KEY = ID.create("com.aopphp.go.annotated.elements");
    private static final KeyDescriptor<String> ENUMERATOR_STRING_DESCRIPTOR = new EnumeratorStringDescriptor();
    private static final StringSetDataExternalizer STRING_SET_DATA_EXTERNALIZER = ReflectionUtil.newInstance(StringSetDataExternalizer.class);

    @NotNull
    @Override
    public ID<String, Set<String>> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Set<String>, FileContent> getIndexer() {
        return new DataIndexer<String, Set<String>, FileContent>() {
            @NotNull
            @Override
            public Map<String, Set<String>> map(@NotNull FileContent inputData) {
                PsiFile psiFile = inputData.getPsiFile();
                if(!(psiFile instanceof PhpFile)) {
                    return Collections.emptyMap();
                }

                return new AbstractDataIndexer<Set<String>>((PhpFile) psiFile) {

                    private Map<String, String> fileImports;

                    @Override
                    protected void visitPhpDocTag(final PhpDocTag phpDocTag, final Map<String, Set<String>> map) {
                        if (fileImports == null) {
                            fileImports = PluginUtil.getFileUseImports(phpDocTag.getContainingFile());
                        }
                        AnnotatedPhpNamedElementIndex.visitPhpDocTag(phpDocTag, fileImports, map);
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
    public DataExternalizer<Set<String>> getValueExternalizer() {
        return STRING_SET_DATA_EXTERNALIZER;
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

    private static void visitPhpDocTag(final PhpDocTag phpDocTag, final Map<String, String> fileImports,
                                       final Map<String, Set<String>> map) {
        String annotationFqnName = PluginUtil.getClassNameReference(phpDocTag, fileImports);
        if(annotationFqnName == null) {
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
        String elementFQN = ((PhpNamedElement) phpNamedElement).getFQN();
        if (map.containsKey(annotationFqnName)) {
            Set<String> strings = map.get(annotationFqnName);
            strings.add(elementFQN);
        } else {
            Set<String> strings = new THashSet<>();
            strings.add(elementFQN);
            map.put(annotationFqnName, strings);
        }
    }
}
