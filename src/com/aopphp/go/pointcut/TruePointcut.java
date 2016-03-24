package com.aopphp.go.pointcut;

/**
 * Canonical pointcut instance that matches all points.
 */
public class TruePointcut extends TruePointFilter implements Pointcut
{
    private static final TruePointcut INSTANCE = new TruePointcut();

    public static PointFilter getInstance()
    {
        return INSTANCE;
    }

    @Override
    public PointFilter getClassFilter() {
        return TruePointFilter.getInstance();
    }
}
