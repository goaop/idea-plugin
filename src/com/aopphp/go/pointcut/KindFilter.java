package com.aopphp.go.pointcut;

import java.io.Serializable;

/**
 * Enumeration of all possible PointFilter kinds
 */
public enum KindFilter implements Serializable {
    KIND_METHOD,
    KIND_PROPERTY,
    KIND_CLASS,
    KIND_TRAIT,
    KIND_FUNCTION,
    KIND_INIT,
    KIND_STATIC_INIT,
    KIND_DYNAMIC
}
