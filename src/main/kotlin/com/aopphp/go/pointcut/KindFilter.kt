package com.aopphp.go.pointcut

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
