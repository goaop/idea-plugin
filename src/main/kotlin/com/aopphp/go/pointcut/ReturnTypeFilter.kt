package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.PhpReturnType
import com.intellij.psi.PsiElement
import java.util.regex.Pattern

/**
 * Matches the declared return type of a method or function, mirroring the framework's `ReturnTypePointcut`.
 *
 * The pattern supports union (`|`) and intersection (`&`) members, DNF groups written without parentheses
 * (`A&B|C` equals `(A&B)|C`), and the nullable marker `?Foo` (equal to `Foo|null`).
 * A single-type pattern matches when any member of the declared type matches it; a composite pattern
 * must correspond one-to-one to the declared type. Members without a declared return type never match.
 */
class ReturnTypeFilter(typePattern: String) : PointFilter {

    private val typePattern: String = typePattern.trim().trim('\\')
    private val patternGroups: List<List<String>> = normalizeTypeExpression(this.typePattern)
    private val isSingleAtomicPattern = patternGroups.size == 1 && patternGroups[0].size == 1
    private val atomRegexps: Map<String, Pattern> = patternGroups.flatten().associateWith { atom ->
        Pattern.compile("^(" + atom.split('*').joinToString("[^\\\\]*") { Pattern.quote(it) } + ")$")
    }

    override fun getKind() = setOf(KindFilter.KIND_METHOD, KindFilter.KIND_FUNCTION)

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is Function) return false
        val declaredType = declaredTypeOf(element.typeDeclaration ?: return false) ?: return false
        return matchesType(declaredType)
    }

    /** Matches a declared type written as in PHP source, with fully-qualified class names */
    fun matchesType(declaredType: String): Boolean {
        val actualGroups = normalizeTypeExpression(declaredType)
        if (actualGroups.isEmpty()) return false

        if (isSingleAtomicPattern) {
            val atomicPattern = patternGroups[0][0]
            return actualGroups.any { group -> group.any { atomMatches(atomicPattern, it) } }
        }

        return matchOneToOne(patternGroups, actualGroups) { patternGroup, actualGroup ->
            matchOneToOne(patternGroup, actualGroup) { patternAtom, actualAtom -> atomMatches(patternAtom, actualAtom) }
        }
    }

    private fun atomMatches(pattern: String, actual: String): Boolean =
        pattern == actual || atomRegexps[pattern]?.matcher(actual)?.matches() == true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReturnTypeFilter) return false
        return typePattern == other.typePattern
    }

    override fun hashCode() = typePattern.hashCode()

    companion object {

        /** Types that a declaration names without resolving them against the namespace */
        private val BUILT_IN_TYPES = setOf(
            "array", "callable", "bool", "float", "int", "string", "iterable", "object", "mixed",
            "void", "null", "never", "false", "true", "self", "static", "parent"
        )

        /**
         * Returns the declared type of [returnType] with every class name resolved to its FQN
         * (without the leading backslash), e.g. `?Foo` with `use App\Foo` becomes `?App\Foo`.
         */
        @JvmStatic
        fun declaredTypeOf(returnType: PhpReturnType): String? {
            val result = StringBuilder()
            appendResolved(returnType, result)
            return result.toString().trim().takeIf { it.isNotEmpty() }
        }

        private fun appendResolved(element: PsiElement, out: StringBuilder) {
            if (element is ClassReference) {
                val name = element.text
                val isBuiltIn = name.lowercase() in BUILT_IN_TYPES
                out.append(if (isBuiltIn) name else (element.fqn ?: name).trimStart('\\'))
                return
            }
            if (element.firstChild == null) {
                out.append(element.text)
                return
            }
            var child = element.firstChild
            while (child != null) {
                if (child !is com.intellij.psi.PsiWhiteSpace && child !is com.intellij.psi.PsiComment) {
                    appendResolved(child, out)
                }
                child = child.nextSibling
            }
        }

        /** Splits a type into its DNF groups of intersected atoms, both sorted, like the framework does */
        @JvmStatic
        fun normalizeTypeExpression(type: String): List<List<String>> {
            var text = type.trim()
            if (text.startsWith("?")) text = text.substring(1) + "|null"

            val groups = splitAtDepthZero(text).mapNotNull { rawMember ->
                var member = rawMember.trim()
                if (member.startsWith("(") && member.endsWith(")")) member = member.substring(1, member.length - 1)
                member.split('&')
                    .map { it.trim().trimStart('\\') }
                    .filter { it.isNotEmpty() }
                    .sorted()
                    .takeIf { it.isNotEmpty() }
            }
            return groups.sortedBy { it.joinToString("&") }
        }

        private fun splitAtDepthZero(type: String): List<String> {
            val members = mutableListOf<String>()
            val current = StringBuilder()
            var depth = 0
            for (c in type) {
                when (c) {
                    '(' -> { depth++; current.append(c) }
                    ')' -> { depth--; current.append(c) }
                    '|' -> if (depth == 0) { members.add(current.toString()); current.clear() } else current.append(c)
                    else -> current.append(c)
                }
            }
            members.add(current.toString())
            return members
        }

        private fun <P, A> matchOneToOne(patternItems: List<P>, actualItems: List<A>, matcher: (P, A) -> Boolean): Boolean {
            if (patternItems.size != actualItems.size) return false
            if (patternItems.isEmpty()) return true
            val patternItem = patternItems.first()
            val remainingPatterns = patternItems.drop(1)
            return actualItems.indices.any { index ->
                matcher(patternItem, actualItems[index]) &&
                    matchOneToOne(remainingPatterns, actualItems.filterIndexed { i, _ -> i != index }, matcher)
            }
        }
    }
}
