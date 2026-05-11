package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import java.util.regex.Pattern

class SignaturePointcut(
    private val filterKind: Set<KindFilter>,
    private val name: String,
    private val modifierFilter: PointFilter
) : Pointcut {

    private val regexp: Pattern = run {
        val escaped = name.replace(Regex("([\\\\.\\[{(*+?^\$|])"), "\\\\$1")
        Pattern.compile(
            escaped
                .replace("\\*\\*", ".+?")
                .replace("\\*", "[^\\\\]+?")
                .replace("\\?", ".")
                .replace("\\|", "|")
        )
    }

    @set:JvmName("setClassFilter")
    var _classFilter: PointFilter = TruePointFilter

    override fun getClassFilter() = _classFilter

    override fun matches(element: PhpNamedElement): Boolean {
        if (element is PhpClassMember && !modifierFilter.matches(element)) return false

        val elementName = when (element) {
            is PhpClassMember -> element.name
            is PhpClass -> element.fqn?.removePrefix("\\") ?: ""
            else -> ""
        }

        return elementName == name || regexp.matcher(elementName).matches()
    }

    override fun getKind() = filterKind

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SignaturePointcut) return false
        return filterKind == other.filterKind && name == other.name
            && modifierFilter == other.modifierFilter && _classFilter == other._classFilter
    }

    override fun hashCode(): Int {
        var result = filterKind.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + modifierFilter.hashCode()
        result = 31 * result + _classFilter.hashCode()
        return result
    }
}
