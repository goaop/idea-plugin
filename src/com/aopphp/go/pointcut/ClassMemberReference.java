package com.aopphp.go.pointcut;

import java.io.Serializable;

/**
 * Data transfer object for storing a reference to the class member (property or method)
 */
public class ClassMemberReference implements Serializable {

    private final PointFilter classFilter;
    private final MemberAccessMatcherFilter visibilityFilter;
    private final MemberStateMatcherFilter accessTypeFilter;
    private final String memberNamePattern;

    public ClassMemberReference(
            PointFilter classFilter,
            MemberAccessMatcherFilter visibilityFilter,
            MemberStateMatcherFilter accessTypeFilter,
            String memberNamePattern
    ) {

        this.classFilter       = classFilter;
        this.visibilityFilter  = visibilityFilter;
        this.accessTypeFilter  = accessTypeFilter;
        this.memberNamePattern = memberNamePattern;
    }

    public PointFilter getClassFilter() {
        return classFilter;
    }

    public MemberAccessMatcherFilter getVisibilityFilter() {
        return visibilityFilter;
    }

    public MemberStateMatcherFilter getAccessTypeFilter() {
        return accessTypeFilter;
    }

    public String getMemberNamePattern() {
        return memberNamePattern;
    }
}
