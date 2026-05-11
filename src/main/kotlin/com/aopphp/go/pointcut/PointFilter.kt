package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import java.io.Serializable

interface PointFilter : Serializable {
    fun matches(element: PhpNamedElement): Boolean
    fun getKind(): Set<KindFilter>
}
