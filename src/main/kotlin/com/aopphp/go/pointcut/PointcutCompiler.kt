package com.aopphp.go.pointcut

import com.aopphp.go.psi.BrakedExpression
import com.aopphp.go.psi.ClassFilter
import com.aopphp.go.psi.ConjugatedExpression
import com.aopphp.go.psi.MemberAccessType
import com.aopphp.go.psi.MemberModifiers
import com.aopphp.go.psi.MemberReference
import com.aopphp.go.psi.NegatedExpression
import com.aopphp.go.psi.PointcutExpression
import com.aopphp.go.psi.PointcutReference
import com.aopphp.go.psi.PointcutTypes
import com.aopphp.go.psi.ReturnTypePattern
import com.aopphp.go.psi.SinglePointcut
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Compiles the PSI of a pointcut expression into a [Pointcut] that matches PHP elements,
 * following the semantics of the Go! AOP 4.0 `PointcutGrammar`.
 */
object PointcutCompiler {

    private val NEVER_MATCHES = NeverMatchesPointcut()

    private class NeverMatchesPointcut : Pointcut {
        override fun getClassFilter(): PointFilter = TruePointFilter
        override fun matches(element: PhpNamedElement) = false
        override fun getKind() = emptySet<KindFilter>()
        override fun hashCode() = 0
        override fun equals(other: Any?) = other is NeverMatchesPointcut
        private fun readResolve(): Any = NEVER_MATCHES
    }

    /**
     * Compiles [element]. [aspectClassFqn] is the FQN of the aspect that declares the expression,
     * used to resolve `$this->pointcutName` references.
     */
    @JvmStatic
    @JvmOverloads
    fun compile(element: PointcutExpression, aspectClassFqn: String? = null): Pointcut {
        val innerExpression = element.pointcutExpression
        val pointcut = resolveConjugatedExpression(element.conjugatedExpression, aspectClassFqn)

        if (innerExpression != null) {
            return OrPointcut(pointcut, compile(innerExpression, aspectClassFqn))
        }

        return pointcut
    }

    private fun resolveConjugatedExpression(element: ConjugatedExpression, aspectClassFqn: String?): Pointcut {
        val innerExpression = element.conjugatedExpression
        val pointcut = resolveNegatedExpression(element.negatedExpression, aspectClassFqn)

        if (innerExpression != null) {
            return AndPointcut(pointcut, resolveConjugatedExpression(innerExpression, aspectClassFqn))
        }

        return pointcut
    }

    private fun resolveNegatedExpression(element: NegatedExpression, aspectClassFqn: String?): Pointcut {
        val pointcut = resolveBrakedExpression(element.brakedExpression, aspectClassFqn)

        if (element.firstChild.node.elementType == PointcutTypes.T_NEGATION) {
            return NotPointcut(pointcut)
        }

        return pointcut
    }

    private fun resolveBrakedExpression(element: BrakedExpression, aspectClassFqn: String?): Pointcut {
        element.pointcutExpression?.let { return compile(it, aspectClassFqn) }
        val singlePointcut = element.singlePointcut ?: return NEVER_MATCHES

        return resolveSinglePointcut(singlePointcut, aspectClassFqn)
    }

    @JvmStatic
    @JvmOverloads
    fun resolveSinglePointcut(element: SinglePointcut, aspectClassFqn: String? = null): Pointcut {
        val kindProperty = setOf(KindFilter.KIND_PROPERTY)
        val kindMethod = setOf(KindFilter.KIND_METHOD)
        val kindClass = setOf(KindFilter.KIND_CLASS)

        element.accessPointcut?.let { accessPointcut ->
            return getSignaturePointcut(getClassMemberReference(accessPointcut.memberReference), kindProperty, null)
        }

        element.executionPointcut?.let { executionPointcut ->
            executionPointcut.methodExecutionReference?.let { methodExecRef ->
                val memberReference = methodExecRef.memberReference
                return getSignaturePointcut(
                    getClassMemberReference(memberReference),
                    kindMethod,
                    methodExecRef.returnTypePattern
                )
            }
            executionPointcut.functionExecutionReference?.let { functionExecRef ->
                // Function joinpoints are calls of system functions from a namespace, there is no PHP element to mark
                return SignaturePointcut(
                    setOf(KindFilter.KIND_FUNCTION),
                    functionExecRef.namespacePattern.text,
                    TruePointFilter
                )
            }
            return NEVER_MATCHES
        }

        element.withinPointcut?.let { withinPointcut ->
            return TruePointcut(KIND_ALL, getClassFilterMatcher(withinPointcut.classFilter))
        }

        element.annotatedExecutionPointcut?.let {
            return AttributePointcut(kindMethod, it.namespaceName.getFQN())
        }

        element.annotatedAccessPointcut?.let {
            return AttributePointcut(kindProperty, it.namespaceName.getFQN())
        }

        element.annotatedWithinPointcut?.let {
            return TruePointcut(KIND_ALL, AttributePointcut(kindClass, it.namespaceName.getFQN()))
        }

        element.initializationPointcut?.let {
            return TruePointcut(setOf(KindFilter.KIND_CLASS, KindFilter.KIND_INIT), getClassFilterMatcher(it.classFilter))
        }

        element.staticInitializationPointcut?.let {
            return TruePointcut(setOf(KindFilter.KIND_CLASS, KindFilter.KIND_STATIC_INIT), getClassFilterMatcher(it.classFilter))
        }

        element.matchInheritedPointcut?.let { return MatchInheritedPointcut() }

        element.matchReturningByReferencePointcut?.let { return MatchReturningByReferencePointcut() }

        element.pointcutReference?.let { return resolvePointcutReference(it, aspectClassFqn) }

        // cflowbelow() and dynamic() are removed in Go! AOP 4.0, the annotator reports them
        return NEVER_MATCHES
    }

    private fun resolvePointcutReference(reference: PointcutReference, aspectClassFqn: String?): Pointcut {
        val pointcutName = reference.namePatternPart.text
        val classFqn = if (reference.firstChild.node.elementType == PointcutTypes.T_THIS) {
            aspectClassFqn ?: return NEVER_MATCHES
        } else {
            reference.namespaceName?.getFQN() ?: return NEVER_MATCHES
        }
        return PointcutReferencePointcut(classFqn, pointcutName)
    }

    private fun getSignaturePointcut(
        memberReference: ClassMemberReference,
        kind: Set<KindFilter>,
        returnTypePattern: ReturnTypePattern?
    ): SignaturePointcut {
        var modifierFilter: PointFilter = AndPointFilter(memberReference.visibilityFilter, memberReference.accessTypeFilter)
        if (returnTypePattern != null) {
            modifierFilter = AndPointFilter(modifierFilter, ReturnTypeFilter(returnTypePattern.text))
        }
        val signaturePointcut = SignaturePointcut(kind, memberReference.memberNamePattern, modifierFilter)
        signaturePointcut._classFilter = memberReference.classFilter
        return signaturePointcut
    }

    @JvmStatic
    fun getClassMemberReference(element: MemberReference): ClassMemberReference {
        return ClassMemberReference(
            getClassFilterMatcher(element.classFilter),
            getModifierMatcher(element.memberModifiers),
            getMemberAccessTypeMatcher(element.memberAccessType),
            element.namePattern.text
        )
    }

    @JvmStatic
    fun getClassFilterMatcher(element: ClassFilter): PointFilter {
        val namespacePattern = element.namespacePattern.text
        if (element.lastChild.node.elementType == PointcutTypes.T_SUBNAMESPACE_SIGN) {
            return InheritanceClassFilter(namespacePattern)
        }
        if (namespacePattern == "**") {
            return TruePointFilter
        }
        return SignaturePointcut(setOf(KindFilter.KIND_CLASS), namespacePattern, TruePointFilter)
    }

    /**
     * Space-separated modifier groups must all be present, `|` alternatives inside a group bind tighter:
     * `final public|protected` is final AND (public OR protected).
     */
    @JvmStatic
    fun getModifierMatcher(element: MemberModifiers): ModifierMatcherFilter {
        var filter = ModifierMatcherFilter()
        for (group in element.modifierGroupList) {
            val modifiers = group.memberModifierList
            val bits = modifiers.fold(0) { mask, modifier -> mask or ModifierMatcherFilter.bitOf(modifier.text) }
            filter = if (modifiers.size == 1) filter.andMatch(bits) else filter.orMatch(bits)
        }
        return filter
    }

    @JvmStatic
    fun getMemberAccessTypeMatcher(element: MemberAccessType): MemberStateMatcherFilter {
        return MemberStateMatcherFilter(element.memberAccessType)
    }
}
