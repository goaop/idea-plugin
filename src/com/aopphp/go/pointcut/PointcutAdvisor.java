package com.aopphp.go.pointcut;

import com.aopphp.go.index.AnnotationPointcutExpressionIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Pointcut advisor class matches all available pointcuts for given element and returns a links to advices
 */
public class PointcutAdvisor {


    public static List<PhpNamedElement> getMatchedAdvices(PhpNamedElement element) {
        final FileBasedIndex fileBasedIndex = FileBasedIndex.getInstance();
        final ID<String, Pointcut> index    = AnnotationPointcutExpressionIndex.KEY;
        ArrayList<PhpNamedElement> result   = new ArrayList<>();

        Project project   = element.getProject();
        PhpIndex phpIndex = PhpIndex.getInstance(project);

        GlobalSearchScope globalSearchScope    = GlobalSearchScope.allScope(project);
        Collection<String> allPointcutElements = fileBasedIndex.getAllKeys(index, project);

        for (String signature : allPointcutElements) {
            List<Pointcut> values = fileBasedIndex.getValues(index, signature, globalSearchScope);
            if (values.size() > 0 && values.get(0).matches(element)) {
                int dot = signature.lastIndexOf('.');
                String className  = signature.substring(0, dot);
                String methodName = signature.substring(dot + 1);

                Collection<PhpClass> adviceClasses = phpIndex.getClassesByFQN(className);
                if (adviceClasses.size() == 0) {
                    continue;
                }

                PhpClass adviceClass = adviceClasses.iterator().next();
                Method adviceMethod  = adviceClass.findMethodByName(methodName);

                if (adviceMethod != null) {
                    result.add(adviceMethod);
                };
            }
        }

        return result;
    }
}
