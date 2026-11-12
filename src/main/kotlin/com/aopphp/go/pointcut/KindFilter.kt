package com.aopphp.go.pointcut

import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import java.io.Serializable

enum class KindFilter : Serializable {
    KIND_METHOD,
    KIND_PROPERTY,
    KIND_CLASS,
    KIND_FUNCTION,
    KIND_INIT,
    KIND_STATIC_INIT,
    KIND_INTRODUCTION
}

/** Every kind of joinpoint a `within(...)` pointcut selects, like the framework's `KIND_ALL` */
val KIND_ALL: Set<KindFilter> = KindFilter.entries.toSet() - KindFilter.KIND_INTRODUCTION

/** Kinds of joinpoints that belong to a class as a whole, not to one of its members. */
val CLASS_LEVEL_KINDS: Set<KindFilter> = setOf(KindFilter.KIND_INIT, KindFilter.KIND_STATIC_INIT, KindFilter.KIND_INTRODUCTION)

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

/**
 * Returns the class in which [element] is matched: the declaring class of a member, or the class itself.
 */
fun contextClassOf(element: PhpNamedElement): PhpClass? = when (element) {
    is PhpClassMember -> element.containingClass
    is PhpClass       -> element
    else              -> null
}
