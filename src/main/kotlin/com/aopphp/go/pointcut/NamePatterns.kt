package com.aopphp.go.pointcut

import java.util.regex.Pattern

/**
 * Translates a Go! AOP name pattern into a regular expression, the way the framework's `NamePointcut` does:
 *
 * - `*` matches any characters of one name part, including none, and never crosses a `\`
 * - `**` matches any characters, namespace separators included
 * - `a|b` are alternatives that stay inside the name part they are written in: `Ns\A|B` is `Ns\A` or `Ns\B`
 */
object NamePatterns {

    @JvmStatic
    fun compile(pattern: String): Pattern {
        val parts = pattern.split('\\').map { part ->
            val regexp = StringBuilder()
            var i = 0
            while (i < part.length) {
                val c = part[i]
                when {
                    c == '*' && part.getOrNull(i + 1) == '*' -> { regexp.append(".+?"); i++ }
                    c == '*' -> regexp.append("[^\\\\]*?")
                    c == '|' -> regexp.append('|')
                    c.isLetterOrDigit() || c == '_' -> regexp.append(c)
                    else -> regexp.append('\\').append(c)
                }
                i++
            }
            if (part.contains('|')) "(?:$regexp)" else regexp.toString()
        }
        return Pattern.compile("^" + parts.joinToString("\\\\") + "$")
    }

    /** Returns true when [name] equals the [pattern] or is matched by its compiled [regexp]. */
    @JvmStatic
    fun matches(pattern: String, regexp: Pattern, name: String): Boolean =
        name == pattern || regexp.matcher(name).matches()
}
