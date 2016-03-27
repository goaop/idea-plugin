package com.aopphp.go.pointcut;

import com.aopphp.go.index.AnnotationPointcutExpressionIndex;
import com.aopphp.go.util.PluginUtil;
import com.intellij.openapi.application.ReadActionProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.stubs.indexes.PhpClassIndex;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Pointcut advisor class matches all available pointcuts for given element and returns a links to advices
 */
public class PointcutAdvisor {
    private static final FileBasedIndex fileBasedIndex = FileBasedIndex.getInstance();
    private static final ID<String, Pointcut> index    = AnnotationPointcutExpressionIndex.KEY;


    public static List<PhpNamedElement> getMatchedAdvices(PhpNamedElement element) {
        ArrayList<PhpNamedElement> result = new ArrayList<>();

        Project project   = element.getProject();
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        GlobalSearchScope globalSearchScope    = GlobalSearchScope.allScope(project);
        Collection<String> allPointcutElements = fileBasedIndex.getAllKeys(index, project);

        for (String signature : allPointcutElements) {
            List<Pointcut> pointcuts = fileBasedIndex.getValues(index, signature, globalSearchScope);
            if (pointcuts.size() == 0) {
                continue;
            }
            Pointcut pointcut = pointcuts.get(0);
            if (isPointcutMatches(element, pointcut)) {
                int dot = signature.lastIndexOf('.');
                String className   = signature.substring(0, dot);
                String elementName = signature.substring(dot + 1);
                boolean isField    = elementName.startsWith("$");

                Collection<PhpClass> adviceClasses = phpIndex.getClassesByFQN(className);
                if (adviceClasses.size() == 0) {
                    continue;
                }

                PhpClass adviceClass = adviceClasses.iterator().next();
                PhpNamedElement adviceElement = null;
                if (!isField) {
                    adviceElement = adviceClass.findMethodByName(elementName);
                } else {
                    adviceElement = adviceClass.findOwnFieldByName(elementName.substring(1), false);
                }
                if (adviceElement != null) {
                    result.add(adviceElement);
                };
            }
        }

        return result;
    }

    public static List<PhpNamedElement> getMatchedElements(PhpNamedElement aspectMember) {
        final ArrayList<PhpNamedElement> result = new ArrayList<>();
        final Project project = aspectMember.getProject();
        final GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);

        String pointcutFQN = aspectMember.getFQN();
        if (pointcutFQN == null) {
            return null;
        }

        List<Pointcut> pointcuts = fileBasedIndex.getValues(index, pointcutFQN, projectScope);
        if (pointcuts.size() == 0) {
            return null;
        }
        final Pointcut pointcut = pointcuts.get(0);
        final THashSet<PhpClass> classList = new THashSet<>();

        Collection<String> allClassNames = StubIndex.getInstance().getAllKeys(PhpClassIndex.KEY, project);
        for (String classKey : allClassNames) {
            StubIndex.getInstance().processElements(
                PhpClassIndex.KEY,
                classKey,
                project,
                projectScope,
                PhpClass.class,
                new Processor<PhpClass>() {
                    @Override
                    public boolean process(PhpClass instance) {
                    if (pointcut.getClassFilter().matches(instance)) {
                        classList.add(instance);
                    }

                    return false;
                    }
                });
        }

        if (pointcut.getKind().contains(KindFilter.KIND_METHOD)) {
            ContainerUtil.process(classList.iterator(), new ReadActionProcessor<PhpClass>() {
                public boolean processInReadAction(PhpClass phpClass) {
                    Method[] methods = phpClass.getOwnMethods();

                    for (Method phpMethod : methods) {
                        if (isPointcutMatches(phpMethod, pointcut)) {
                            result.add(phpMethod);
                        }
                    }

                    return true;
                }
            });
        }

        if (pointcut.getKind().contains(KindFilter.KIND_PROPERTY)) {
            ContainerUtil.process(classList.iterator(), new ReadActionProcessor<PhpClass>() {
                public boolean processInReadAction(PhpClass phpClass) {
                    Field[] fields = phpClass.getOwnFields();

                    for (Field phpField : fields) {
                        if (isPointcutMatches(phpField, pointcut)) {
                            result.add(phpField);
                        }
                    }

                    return true;
                }
            });
        }

        return result;
    }

    private static boolean isPointcutMatches(PhpNamedElement element, Pointcut pointcut) {
        if (!canMatchElement(element, pointcut.getKind())) {
            return false;
        }

        PhpNamedElement parentElement = null;
        // For class members we should also check the pointcut class filter first
        if (element instanceof PhpClassMember) {
            parentElement = ((PhpClassMember) element).getContainingClass();
        } else if (element instanceof PhpClass) {
            parentElement = element;
        }
        // Pre-check for parent element (typically, this will be a class for methods and properties)
        PointFilter classFilter = pointcut.getClassFilter();
        if (!canMatchElement(parentElement, classFilter.getKind()) || !classFilter.matches(parentElement)) {
            return false;
        }

        return pointcut.matches(element);
    }

    /**
     * Checks if filter kind matches instance of concrete element
     *
     * @param element PHP element to check
     * @param filterKind Pointcut filter kind
     *
     * @return true if pointcut can match this named element
     */
    private static boolean canMatchElement(PhpNamedElement element, Set<KindFilter> filterKind) {
        if (element instanceof Method) {
            return filterKind.contains(KindFilter.KIND_METHOD);
        }

        if (element instanceof Field) {
            return filterKind.contains(KindFilter.KIND_PROPERTY);
        }

        if (element instanceof PhpClass) {
            PhpClass instance = (PhpClass) element;
            if (instance.isInterface() || PluginUtil.isAspect(instance)) {
                return false;
            }
            return filterKind.contains(KindFilter.KIND_CLASS);
        }

        return false;
    }
}
