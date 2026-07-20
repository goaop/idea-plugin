package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import java.io.Serializable

enum class KindFilter : Serializable {
    KIND_METHOD,
    KIND_PROPERTY,
    KIND_CLASS,
    KIND_TRAIT,
    KIND_FUNCTION,
    KIND_INIT,
    KIND_STATIC_INIT,
    KIND_DYNAMIC
}

/**
 * Checks whether this set of kinds can match the given element's join point kind.
 *
 * This is the matching "context" from goaop/framework#274: without it, each part of a
 * combined pointcut (e.g. `execution(...) || access(...)`) would be consulted for every
 * element kind and could match by name pattern alone.
 */
fun Set<KindFilter>.supports(element: PhpNamedElement): Boolean = when (element) {
    is Method   -> KindFilter.KIND_METHOD in this
    is Field    -> KindFilter.KIND_PROPERTY in this
    is PhpClass -> KindFilter.KIND_CLASS in this
    else        -> false
}
