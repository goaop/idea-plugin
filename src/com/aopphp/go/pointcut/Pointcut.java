package com.aopphp.go.pointcut;

/**
 * Pointcut realization
 *
 * Pointcuts are defined as a predicate over the syntax-tree of the program, and define an interface that constrains
 * which elements of the base program are exposed by the pointcut. A pointcut picks out certain join points and values
 * at those points
 */
public interface Pointcut extends PointFilter
{
    /**
     * Return the class filter for this pointcut.
     *
     * @return PointFilter
     */
    PointFilter getClassFilter();
}
