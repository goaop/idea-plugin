package com.aopphp.go.util

import com.aopphp.go.psi.AnnotatedAccessPointcut
import com.aopphp.go.psi.AnnotatedExecutionPointcut
import com.aopphp.go.psi.AnnotatedWithinPointcut
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpClass

/**
 * Utilities for matching PHP 8 Attribute target flags against Go! AOP pointcut types.
 *
 * PHP's \Attribute class defines these bit constants:
 *   TARGET_CLASS          = 1
 *   TARGET_FUNCTION       = 2
 *   TARGET_METHOD         = 4
 *   TARGET_PROPERTY       = 8
 *   TARGET_CLASS_CONSTANT = 16
 *   TARGET_PARAMETER      = 32
 *   TARGET_ALL            = 63
 *
 * - @execution → attribute must target methods or functions (TARGET_METHOD | TARGET_FUNCTION)
 * - @access    → attribute must target properties (TARGET_PROPERTY)
 * - @within    → attribute must target classes (TARGET_CLASS)
 */
object AttributeTargetUtil {
    const val TARGET_CLASS          = 1
    const val TARGET_FUNCTION       = 2
    const val TARGET_METHOD         = 4
    const val TARGET_PROPERTY       = 8
    const val TARGET_CLASS_CONSTANT = 16
    const val TARGET_PARAMETER      = 32
    const val TARGET_ALL            = 63

    private val CONSTANT_MAP = mapOf(
        "TARGET_ALL"            to TARGET_ALL,
        "TARGET_CLASS_CONSTANT" to TARGET_CLASS_CONSTANT,
        "TARGET_CLASS"          to TARGET_CLASS,
        "TARGET_FUNCTION"       to TARGET_FUNCTION,
        "TARGET_METHOD"         to TARGET_METHOD,
        "TARGET_PROPERTY"       to TARGET_PROPERTY,
        "TARGET_PARAMETER"      to TARGET_PARAMETER,
    )

    /**
     * Returns the required target bit-mask for the given annotation pointcut parent PSI node.
     * Returns 0 if the parent is not a recognized annotation pointcut.
     */
    fun requiredBitsFor(pointcutParent: PsiElement?): Int = when (pointcutParent) {
        is AnnotatedExecutionPointcut -> TARGET_METHOD or TARGET_FUNCTION
        is AnnotatedAccessPointcut    -> TARGET_PROPERTY
        is AnnotatedWithinPointcut    -> TARGET_CLASS
        else                          -> 0
    }

    /**
     * Human-readable description of the required targets for use in error messages.
     */
    fun requiredDescription(pointcutParent: PsiElement?): String = when (pointcutParent) {
        is AnnotatedExecutionPointcut -> "TARGET_METHOD or TARGET_FUNCTION"
        is AnnotatedAccessPointcut    -> "TARGET_PROPERTY"
        is AnnotatedWithinPointcut    -> "TARGET_CLASS"
        else                          -> "unknown"
    }

    /**
     * Returns true if [phpClass] has a #[\Attribute(...)] decorator whose target mask
     * is compatible with [requiredBits].
     *
     * A class with no #[\Attribute] is not an attribute at all — returns false.
     * A class with #[\Attribute] (no args, i.e. TARGET_ALL) is compatible with everything.
     */
    fun isCompatible(phpClass: PhpClass, requiredBits: Int): Boolean {
        if (requiredBits == 0) return true
        val mask = getTargetMask(phpClass)
        return (mask and requiredBits) != 0
    }

    /**
     * Reads the target bitmask from the #[\Attribute(...)] decorator on [phpClass].
     * Returns 0 if the class has no \Attribute decorator.
     * Returns TARGET_ALL if the decorator has no arguments (bare #[\Attribute]).
     */
    fun getTargetMask(phpClass: PhpClass): Int {
        val attribute = phpClass.getAttributes("\\Attribute").firstOrNull() ?: return 0
        val paramList = attribute.parameterList ?: return TARGET_ALL
        val params = paramList.parameters
        if (params.isEmpty()) return TARGET_ALL
        return parseTargetMaskFromText(params[0].text)
    }

    /**
     * Parses a PHP expression like `Attribute::TARGET_METHOD | Attribute::TARGET_FUNCTION`
     * or an integer literal `6` into a bitmask integer.
     *
     * Falls back to TARGET_ALL for unrecognized expressions (permissive — avoids false errors).
     */
    private fun parseTargetMaskFromText(text: String): Int {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return TARGET_ALL

        // Plain integer literal
        trimmed.toIntOrNull()?.let { return it }

        var mask = 0
        for (part in trimmed.split('|')) {
            // Extract the constant name: last token after '::' (handles `Attribute::TARGET_X` and bare `TARGET_X`)
            val token = part.trim().substringAfterLast(':').trim()
            CONSTANT_MAP[token]?.let { mask = mask or it }
        }
        // If we couldn't parse anything meaningful, be permissive
        return if (mask == 0) TARGET_ALL else mask
    }
}
