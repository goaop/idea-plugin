package com.aopphp.go.pointcut

interface Pointcut : PointFilter {
    fun getClassFilter(): PointFilter
}
