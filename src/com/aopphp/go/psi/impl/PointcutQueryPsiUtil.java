package com.aopphp.go.psi.impl;

import com.aopphp.go.pointcut.*;
import com.aopphp.go.psi.*;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class with miscellaneous PSI methods implementation
 */
public class PointcutQueryPsiUtil {

    /**
     * Returns an absolute FQN for namespace name node
     *
     * @param namespaceName Namespace holder
     * @return Fully-qualified namespace
     */
    public static String getFQN(NamespaceName namespaceName) {
        String fqn = "";
        if (namespaceName.getText().charAt(0) != '\\') {
            fqn = "\\";
        }
        fqn += namespaceName.getText();

        return fqn;
    }

    public static Pointcut compile(PointcutExpression element) {
        PointcutExpression innerExpression = element.getPointcutExpression();
        Pointcut pointcut = resolveConjugatedExpression(element.getConjugatedExpression());

        if (innerExpression != null) {
            Pointcut secondPointcut = compile(innerExpression);
            pointcut = new OrPointcut(pointcut, secondPointcut);
        }

        return pointcut;
    }

    public static Pointcut resolveConjugatedExpression(ConjugatedExpression element) {
        ConjugatedExpression innerExpression = element.getConjugatedExpression();
        Pointcut pointcut = resolveNegatedExpression(element.getNegatedExpression());

        if (innerExpression != null) {
            Pointcut secondPointcut = resolveConjugatedExpression(innerExpression);
            pointcut = new AndPointcut(pointcut, secondPointcut);
        }

        return pointcut;
    }

    public static Pointcut resolveNegatedExpression(NegatedExpression element) {
        Pointcut pointcut = resolveBrakedExpression(element.getBrakedExpression());

        if (element.getFirstChild().getText().equals("!")) {
            pointcut = new NotPointcut(pointcut);
        }

        return pointcut;
    }

    public static Pointcut resolveBrakedExpression(BrakedExpression element) {
        PointcutExpression pointcutExpression = element.getPointcutExpression();
        if (pointcutExpression != null) {
            return compile(pointcutExpression);
        }

        return resolveSinglePointcut(element.getSinglePointcut());
    }

    public static Pointcut resolveSinglePointcut(SinglePointcut element) {
        AccessPointcut accessPointcut = element.getAccessPointcut();
        final Set<KindFilter> kindProperty = Collections.singleton(KindFilter.KIND_PROPERTY);
        final Set<KindFilter> kindMethod   = Collections.singleton(KindFilter.KIND_METHOD);
        final Set<KindFilter> kindClass    = Collections.singleton(KindFilter.KIND_CLASS);

        if (accessPointcut != null) {
            ClassMemberReference memberReference = accessPointcut.getMemberReference().getClassMemberReference();

            return getSignaturePointcut(memberReference, kindProperty);
        }

        ExecutionPointcut executionPointcut = element.getExecutionPointcut();
        if (executionPointcut != null) {
            MethodExecutionReference methodExecutionReference = executionPointcut.getMethodExecutionReference();
            if (methodExecutionReference != null) {
                ClassMemberReference memberReference = methodExecutionReference.getMemberReference().getClassMemberReference();

                return getSignaturePointcut(memberReference, kindMethod);
            }
            // TODO: process function reference
        }

        WithinPointcut withinPointcut = element.getWithinPointcut();
        if (withinPointcut != null) {
            TruePointcut pointcut = new TruePointcut();
            pointcut.setClassFilter(withinPointcut.getClassFilter().getClassFilterMatcher());

            return pointcut;
        }

        AnnotatedExecutionPointcut annotatedExecutionPointcut = element.getAnnotatedExecutionPointcut();
        if (annotatedExecutionPointcut != null) {
            String annotationName = annotatedExecutionPointcut.getNamespaceName().getFQN();
            return new AnnotationPointcut(kindMethod, annotationName);
        }

        AnnotatedAccessPointcut annotatedAccessPointcut = element.getAnnotatedAccessPointcut();
        if (annotatedAccessPointcut != null) {
            String annotationName = annotatedAccessPointcut.getNamespaceName().getFQN();
            return new AnnotationPointcut(kindProperty, annotationName);
        }

        AnnotatedWithinPointcut annotatedWithinPointcut = element.getAnnotatedWithinPointcut();
        if (annotatedWithinPointcut != null) {
            TruePointcut pointcut = new TruePointcut();

            String annotationName     = annotatedWithinPointcut.getNamespaceName().getFQN();
            AnnotationPointcut classFilter = new AnnotationPointcut(kindClass, annotationName);
            pointcut.setClassFilter(classFilter);

            return pointcut;
        }

        InitializationPointcut initializationPointcut = element.getInitializationPointcut();
        if (initializationPointcut != null) {
            TruePointcut pointcut = new TruePointcut(kindClass);
            pointcut.setClassFilter(initializationPointcut.getClassFilter().getClassFilterMatcher());

            return pointcut;
        }

        StaticInitializationPointcut staticInitializationPointcut = element.getStaticInitializationPointcut();
        if (staticInitializationPointcut != null) {
            TruePointcut pointcut = new TruePointcut(kindClass);
            pointcut.setClassFilter(staticInitializationPointcut.getClassFilter().getClassFilterMatcher());

            return pointcut;
        }

        // Pointcut that never matches
        return new Pointcut() {
            @Override
            public PointFilter getClassFilter() {
                return TruePointFilter.getInstance();
            }

            @Override
            public boolean matches(PhpNamedElement element) {
                return false;
            }

            @Override
            public Set<KindFilter> getKind() {
                return Collections.emptySet();
            }
        };
    }

    @NotNull
    private static SignaturePointcut getSignaturePointcut(ClassMemberReference memberReference, Set<KindFilter> propertyKind) {
        SignaturePointcut signaturePointcut = new SignaturePointcut(
            propertyKind,
            memberReference.getMemberNamePattern(),
            new AndPointFilter(memberReference.getVisibilityFilter(), memberReference.getAccessTypeFilter())
        );
        signaturePointcut.setClassFilter(memberReference.getClassFilter());
        return signaturePointcut;
    }

    /**
     * Prepares a value object from parts
     *
     * @param element Current element
     */
    public static ClassMemberReference getClassMemberReference(MemberReference element) {
        return new ClassMemberReference(
            element.getClassFilter().getClassFilterMatcher(),
            element.getMemberModifiers().getMemberAccessMatcher(),
            element.getMemberAccessType().getMemberAccessTypeMatcher(),
            element.getNamePattern().getText()
        );
    }

    /**
     * Returns a class filter matcher for given class pattern
     *
     * @param element Instance of ClassFilter PSI
     * @return instance of point filter for the class
     */
    public static PointFilter getClassFilterMatcher(ClassFilter element) {
        String namespacePattern     = element.getNamespacePattern().getText();
        PointFilter truePointFilter = TruePointFilter.getInstance();

        if (namespacePattern.equals("**")) {
            return truePointFilter;
        }
        if (element.getLastChild().getText().equals("+")) {
            return new InheritanceClassFilter(namespacePattern);
        }

        return new SignaturePointcut(Collections.singleton(KindFilter.KIND_CLASS), namespacePattern, truePointFilter);
    }

    /**
     * Returns a valid instance of access type by text name
     *
     * @param element Current element
     * @return concrete PhpModifier.Access value
     */
    public static PhpModifier.Access getMemberAccess(MemberModifier element) {
        return PhpModifier.Access.valueOf(element.getText().toUpperCase());
    }

    /**
     * Returns a member access matcher filter from the list of member access
     *
     * @param element Current element
     * @return filter for members, each item is OR-ed
     */
    public static MemberAccessMatcherFilter getMemberAccessMatcher(MemberModifiers element) {
        List<MemberModifier> memberModifierList = element.getMemberModifierList();
        HashSet<PhpModifier.Access> allowedAccess = new HashSet<>();
        for (MemberModifier memberModifier : memberModifierList) {
            allowedAccess.add(memberModifier.getMemberAccess());
        }

        return new MemberAccessMatcherFilter(allowedAccess);
    }

    /**
     * Returns a valid instance of member state (dynamic/static) by text name
     *
     * @param element Current element
     * @return concrete PhpModifier.State value
     */
    public static PhpModifier.State getMemberAccessType(MemberAccessType element) {
        if (element.getText().equals("->")) {
            return PhpModifier.State.DYNAMIC;
        };

        return PhpModifier.State.STATIC;
    }

    /**
     * Returns a member type/state matcher
     * @param element Current element
     * @return filter for element type (static/dynamic)
     */
    public static MemberStateMatcherFilter getMemberAccessTypeMatcher(MemberAccessType element)
    {
        PhpModifier.State memberAccessType = getMemberAccessType(element);

        return new MemberStateMatcherFilter(memberAccessType);
    }
}
