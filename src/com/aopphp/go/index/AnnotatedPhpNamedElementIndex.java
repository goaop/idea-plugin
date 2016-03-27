package com.aopphp.go.index;

import com.aopphp.go.util.PluginUtil;
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
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This index collects all annotated elements in the files
 */
public class AnnotatedPhpNamedElementIndex extends FileBasedIndexExtension<String, List<String>> {

    public static final ID<String, List<String>> KEY = ID.create("com.aopphp.go.annotated.elements");
    private final KeyDescriptor<String> keyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, List<String>> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, List<String>, FileContent> getIndexer() {
        return new DataIndexer<String, List<String>, FileContent>() {
            @NotNull
            @Override
            public Map<String, List<String>> map(@NotNull FileContent inputData) {
                final Map<String, List<String>> map = new THashMap<>();

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
    public DataExternalizer<List<String>> getValueExternalizer() {
        return new StringCollectionExternalizer();
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

        private final Map<String, List<String>> map;
        private Map<String, String> fileImports;

        public DocTagRecursiveElementVisitor(Map<String, List<String>> map) {
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
                this.fileImports = PluginUtil.getFileUseImports(phpDocTag.getContainingFile());
            }

            String annotationFqnName = PluginUtil.getClassNameReference(phpDocTag, this.fileImports);
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
                List<String> strings = map.get(annotationFqnName);
                strings.add(elementFQN);
            } else {
                ArrayList<String> strings = new ArrayList<>();
                strings.add(elementFQN);
                map.put(annotationFqnName, strings);
            }
        }
    }

    /**
     * Internal class to store and load the list of strings
     */
    private static class StringCollectionExternalizer implements DataExternalizer<List<String>> {

        @Override
        public void save(@NotNull DataOutput dataOutput, List<String> strings) throws IOException {
            dataOutput.writeInt(strings.size());
            for (String element : strings) {
                dataOutput.writeUTF(element);
            }
        }

        @Override
        public List<String> read(@NotNull DataInput dataInput) throws IOException {
            int length = dataInput.readInt();
            List<String> strings = new ArrayList<>();
            for (int i=0; i<length; i++) {
                strings.add(dataInput.readUTF());
            }

            return strings;
        }
    }
}

