package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import java.util.regex.Pattern

class ReturnTypeFilter(
    private val typePattern: String,
    private val isNullable: Boolean
) : PointFilter {

    private val regexp: Pattern = run {
        val escaped = typePattern.replace(Regex("([\\\\.\\[{(*+?^\$|])"), "\\\\$1")
        Pattern.compile(
            escaped
                .replace("\\*\\*", ".+?")
                .replace("\\*", "[^\\\\]+?")
                .replace("\\?", ".")
                .replace("\\|", "|")
        )
    }

    override fun getKind() = setOf(KindFilter.KIND_METHOD)

    override fun matches(element: PhpNamedElement): Boolean {
        if (element !is Method) return false
        val types = element.type.types
        if (types.isEmpty()) return false

        val hasNull = types.any { it.equals("null", ignoreCase = true) }
        if (!isNullable && hasNull) return false

        val typesToMatch = if (isNullable) types else types.filter { !it.equals("null", ignoreCase = true) }
        return typesToMatch.any { typeName ->
            val normalized = typeName.trimStart('\\')
            typeName == typePattern || normalized == typePattern
                || regexp.matcher(typeName).matches()
                || regexp.matcher(normalized).matches()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReturnTypeFilter) return false
        return typePattern == other.typePattern && isNullable == other.isNullable
    }

    override fun hashCode() = 31 * typePattern.hashCode() + isNullable.hashCode()
}
