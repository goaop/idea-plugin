package com.aopphp.go.pointcut

import com.aopphp.go.psi.BrakedExpression
import com.aopphp.go.psi.ClassFilter
import com.aopphp.go.psi.ConjugatedExpression
import com.aopphp.go.psi.MemberAccessType
import com.aopphp.go.psi.MemberModifier
import com.aopphp.go.psi.MemberModifiers
import com.aopphp.go.psi.MemberReference
import com.aopphp.go.psi.NegatedExpression
import com.aopphp.go.psi.PointcutExpression
import com.aopphp.go.psi.PointcutTypes
import com.aopphp.go.psi.SinglePointcut
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

object PointcutCompiler {

    private val NEVER_MATCHES = NeverMatchesPointcut()

    private class NeverMatchesPointcut : Pointcut {
        override fun getClassFilter(): PointFilter = TruePointFilter
        override fun matches(element: PhpNamedElement) = false
        override fun getKind() = emptySet<KindFilter>()
        override fun hashCode() = 0
        override fun equals(other: Any?) = other is NeverMatchesPointcut
    }

    fun compile(element: PointcutExpression): Pointcut {
        val innerExpression = element.pointcutExpression
        val pointcut = resolveConjugatedExpression(element.conjugatedExpression)

        if (innerExpression != null) {
            val secondPointcut = compile(innerExpression)
            return OrPointcut(pointcut, secondPointcut)
        }

        return pointcut
    }

    fun resolveConjugatedExpression(element: ConjugatedExpression): Pointcut {
        val innerExpression = element.conjugatedExpression
        val pointcut = resolveNegatedExpression(element.negatedExpression)

        if (innerExpression != null) {
            val secondPointcut = resolveConjugatedExpression(innerExpression)
            return AndPointcut(pointcut, secondPointcut)
        }

        return pointcut
    }

    fun resolveNegatedExpression(element: NegatedExpression): Pointcut {
        val pointcut = resolveBrakedExpression(element.brakedExpression)

        if (element.firstChild.text == "!") {
            return NotPointcut(pointcut)
        }

        return pointcut
    }

    fun resolveBrakedExpression(element: BrakedExpression): Pointcut {
        val pointcutExpression = element.pointcutExpression
        if (pointcutExpression != null) {
            return compile(pointcutExpression)
        }

        return resolveSinglePointcut(element.singlePointcut!!)
    }

    fun resolveSinglePointcut(element: SinglePointcut): Pointcut {
        val kindProperty = setOf(KindFilter.KIND_PROPERTY)
        val kindMethod = setOf(KindFilter.KIND_METHOD)
        val kindClass = setOf(KindFilter.KIND_CLASS)

        element.accessPointcut?.let { accessPointcut ->
            val memberReference = getClassMemberReference(accessPointcut.memberReference!!)
            return getSignaturePointcut(memberReference, kindProperty)
        }

        element.executionPointcut?.let { executionPointcut ->
            val methodExecRef = executionPointcut.methodExecutionReference
            if (methodExecRef != null) {
                val memberReference = getClassMemberReference(methodExecRef.memberReference!!)
                val returnTypePattern = methodExecRef.returnTypePattern
                if (returnTypePattern != null) {
                    val nullable =
                        returnTypePattern.firstChild.node.elementType == PointcutTypes.T_QUESTION_MARK
                    val patternText = returnTypePattern.text
                    val typeText = if (nullable) patternText.substring(1) else patternText
                    val modifierFilter = AndPointFilter(
                        AndPointFilter(memberReference.visibilityFilter, memberReference.accessTypeFilter),
                        ReturnTypeFilter(typeText, nullable)
                    )
                    val signaturePointcut =
                        SignaturePointcut(kindMethod, memberReference.memberNamePattern, modifierFilter)
                    signaturePointcut._classFilter = memberReference.classFilter
                    return signaturePointcut
                }
                return getSignaturePointcut(memberReference, kindMethod)
            }
        }

        element.withinPointcut?.let { withinPointcut ->
            val pointcut = TruePointcut()
            pointcut._classFilter = getClassFilterMatcher(withinPointcut.classFilter!!)
            return pointcut
        }

        element.annotatedExecutionPointcut?.let {
            val annotationName = it.namespaceName!!.getFQN()
            return AttributePointcut(kindMethod, annotationName)
        }

        element.annotatedAccessPointcut?.let {
            val annotationName = it.namespaceName!!.getFQN()
            return AttributePointcut(kindProperty, annotationName)
        }

        element.annotatedWithinPointcut?.let {
            val pointcut = TruePointcut()
            pointcut._classFilter = AttributePointcut(kindClass, it.namespaceName!!.getFQN())
            return pointcut
        }

        element.initializationPointcut?.let {
            val pointcut = TruePointcut(kindClass)
            pointcut._classFilter = getClassFilterMatcher(it.classFilter!!)
            return pointcut
        }

        element.staticInitializationPointcut?.let {
            val pointcut = TruePointcut(kindClass)
            pointcut._classFilter = getClassFilterMatcher(it.classFilter!!)
            return pointcut
        }

        return NEVER_MATCHES
    }

    private fun getSignaturePointcut(
        memberReference: ClassMemberReference,
        propertyKind: Set<KindFilter>
    ): SignaturePointcut {
        val signaturePointcut = SignaturePointcut(
            propertyKind,
            memberReference.memberNamePattern,
            AndPointFilter(memberReference.visibilityFilter, memberReference.accessTypeFilter)
        )
        signaturePointcut._classFilter = memberReference.classFilter
        return signaturePointcut
    }

    fun getClassMemberReference(element: MemberReference): ClassMemberReference {
        return ClassMemberReference(
            getClassFilterMatcher(element.classFilter!!),
            getMemberAccessMatcher(element.memberModifiers!!),
            getMemberAccessTypeMatcher(element.memberAccessType!!),
            element.namePattern!!.text
        )
    }

    fun getClassFilterMatcher(element: ClassFilter): PointFilter {
        val namespacePattern = element.namespacePattern!!.text
        if (namespacePattern == "**") {
            return TruePointFilter
        }
        if (element.lastChild.text == "+") {
            return InheritanceClassFilter(namespacePattern)
        }
        return SignaturePointcut(setOf(KindFilter.KIND_CLASS), namespacePattern, TruePointFilter)
    }

    fun getMemberAccessMatcher(element: MemberModifiers): MemberAccessMatcherFilter {
        val allowedAccess = HashSet<PhpModifier.Access>()
        for (memberModifier in element.memberModifierList) {
            allowedAccess.add(memberModifier.memberAccess)
        }
        return MemberAccessMatcherFilter(allowedAccess)
    }

    fun getMemberAccessTypeMatcher(element: MemberAccessType): MemberStateMatcherFilter {
        return MemberStateMatcherFilter(element.memberAccessType)
    }
}
