package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpElementWithModifier
import com.jetbrains.php.lang.psi.elements.PhpModifier
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Matches the modifiers of a class member, mirroring the framework's `ModifierPointcut`:
 * every bit of [andMask] must be present, and at least one bit of [orMask] when alternatives are given.
 *
 * `final public|protected` is final AND (public OR protected): space-separated modifiers must all be present,
 * `|` alternatives bind tighter.
 */
class ModifierMatcherFilter(
    private val andMask: Int = 0,
    private val orMask: Int = 0,
) : PointFilter {

    companion object {
        const val PUBLIC = 1
        const val PROTECTED = 2
        const val PRIVATE = 4
        const val FINAL = 8
        const val READONLY = 16
        const val PRIVATE_SET = 32
        const val PROTECTED_SET = 64

        /** Maps the text of a pointcut modifier to its bit */
        @JvmStatic
        fun bitOf(modifier: String): Int = when (modifier) {
            "public" -> PUBLIC
            "protected" -> PROTECTED
            "private" -> PRIVATE
            "final" -> FINAL
            "readonly" -> READONLY
            "private(set)" -> PRIVATE_SET
            "protected(set)" -> PROTECTED_SET
            else -> 0
        }

        /** Returns the modifier bits of a PHP class member */
        @JvmStatic
        fun modifiersOf(element: PhpElementWithModifier): Int {
            val modifier = element.modifier
            var bits = when (modifier.access) {
                PhpModifier.Access.PUBLIC -> PUBLIC
                PhpModifier.Access.PROTECTED -> PROTECTED
                PhpModifier.Access.PRIVATE -> PRIVATE
                null -> 0
            }
            if (modifier.isFinal) bits = bits or FINAL
            if (element is Field) {
                if (element.isFinal) bits = bits or FINAL
                if (element.isReadonly) bits = bits or READONLY
                when (modifier.setAccess) {
                    PhpModifier.Access.PRIVATE -> bits = bits or PRIVATE_SET
                    PhpModifier.Access.PROTECTED -> bits = bits or PROTECTED_SET
                    else -> {}
                }
            }
            return bits
        }
    }

    private val _kind = KindFilter.entries.toSet()

    override fun getKind() = _kind

    /** True when this filter has `|` alternatives */
    fun hasAlternatives() = orMask != 0

    /** Returns a filter that also requires all bits of [bitMask] */
    fun andMatch(bitMask: Int) = ModifierMatcherFilter(andMask or bitMask, orMask)

    /** Returns a filter that also accepts any bit of [bitMask] */
    fun orMatch(bitMask: Int) = ModifierMatcherFilter(andMask, orMask or bitMask)

    /** True when [bit] is required or accepted by this filter */
    fun mentions(bit: Int) = ((andMask or orMask) and bit) != 0

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is PhpElementWithModifier) return false
        val modifiers = modifiersOf(element)
        return (andMask and modifiers) == andMask && (orMask == 0 || (orMask and modifiers) != 0)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ModifierMatcherFilter) return false
        return andMask == other.andMask && orMask == other.orMask
    }

    override fun hashCode() = 31 * andMask + orMask

    override fun toString() = "ModifierMatcherFilter(andMask=$andMask, orMask=$orMask)"
}
