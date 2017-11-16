package com.aopphp.go.pointcut;

import com.aopphp.go.index.AnnotatedPhpNamedElementIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import de.espend.idea.php.annotation.util.AnnotationUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Annotation pointcut matches PHP named elements that have specific doctrine annotations in docBlock
 */
public class AnnotationPointcut implements Pointcut
{
    private PointFilter classFilter;
    private Set<KindFilter> filterKind;

    /**
     * Specifies name of the expected class to receive
     */
    private final String expectedClass;

    public AnnotationPointcut(Set<KindFilter> filterKind, String annotationName) {
        this.filterKind     = filterKind;
        this.expectedClass  = annotationName;
        this.classFilter    = new AnnotatedPhpNamedElementClassFilter(annotationName);
    }

    @Override
    public PointFilter getClassFilter() {
        return classFilter;
    }

    public void setClassFilter(PointFilter classFilter) {
        this.classFilter = classFilter;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        if (!canMatchElement(element)) {
            return false;
        }
        PhpDocComment docComment = element.getDocComment();
        if (docComment == null) {
            return false;
        }

        List<PhpDocTag> docTagList = PsiTreeUtil.getChildrenOfTypeAsList(docComment, PhpDocTag.class);
        for (PhpDocTag phpDocTag : docTagList) {
            PhpClass annotationReference = AnnotationUtil.getAnnotationReference(phpDocTag);
            if (annotationReference == null || annotationReference.getPresentableFQN() == null) {
                continue;
            }
            String fqn = annotationReference.getFQN();
            if (fqn != null && fqn.equals(expectedClass)) {
                return true;
            };
        }

        return false;
    }

    /**
     * Checks if filter kind matches instance of concrete element
     *
     * @param element PHP element to check
     *
     * @return true if pointcut can match this named element
     */
    private boolean canMatchElement(PhpNamedElement element) {
        if (element instanceof Method) {
            return filterKind.contains(KindFilter.KIND_METHOD);
        }

        if (element instanceof Field) {
            return filterKind.contains(KindFilter.KIND_PROPERTY);
        }

        if (element instanceof PhpClass) {
            return filterKind.contains(KindFilter.KIND_CLASS);
        }

        return false;
    }

    @Override
    public Set<KindFilter> getKind() {
        return filterKind;
    }

    private static class AnnotatedPhpNamedElementClassFilter implements PointFilter {

        private static final Set<KindFilter> KIND_CLASS = Collections.singleton(KindFilter.KIND_CLASS);
        private static final FileBasedIndex INDEX = FileBasedIndex.getInstance();
        private static final ID<String, Set<String>> KEY = AnnotatedPhpNamedElementIndex.KEY;


        private String annotationName;

        public AnnotatedPhpNamedElementClassFilter(String annotationName) {
            this.annotationName = annotationName;
        }

        @Override
        public boolean matches(PhpNamedElement element) {
            if (!(element instanceof PhpClass)) {
                return false;
            }

            String elementFQN = element.getFQN();
            if (elementFQN == null) {
                return false;
            }

            GlobalSearchScope searchScope = PhpIndex.getInstance(element.getProject()).getSearchScope();
            List<Set<String>> values     = INDEX.getValues(KEY, annotationName, searchScope);
            if (values.size() > 0) {
                Set<String> phpElementsFQN = values.get(0);
                for (String phpElementFQN : phpElementsFQN) {
                    if (phpElementFQN.startsWith(elementFQN)) {
                        return true;
                    }
                }
            }

            return false;
        }

        @Override
        public Set<KindFilter> getKind() {
            return KIND_CLASS;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnnotatedPhpNamedElementClassFilter)) return false;

            AnnotatedPhpNamedElementClassFilter that = (AnnotatedPhpNamedElementClassFilter) o;

            return annotationName.equals(that.annotationName);
        }

        @Override
        public int hashCode() {
            return annotationName.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotationPointcut)) return false;

        AnnotationPointcut that = (AnnotationPointcut) o;

        if (!classFilter.equals(that.classFilter)) return false;
        if (!filterKind.equals(that.filterKind)) return false;
        return expectedClass.equals(that.expectedClass);
    }

    @Override
    public int hashCode() {
        int result = classFilter.hashCode();
        result = 31 * result + filterKind.hashCode();
        result = 31 * result + expectedClass.hashCode();
        return result;
    }
}
