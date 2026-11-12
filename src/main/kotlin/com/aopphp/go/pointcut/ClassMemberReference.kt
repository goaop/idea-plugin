package com.aopphp.go.pointcut

import java.io.Serializable

data class ClassMemberReference(
    val classFilter: PointFilter,
    val visibilityFilter: ModifierMatcherFilter,
    val accessTypeFilter: MemberStateMatcherFilter,
    val memberNamePattern: String
) : Serializable
