package com.aopphp.go;

import com.intellij.lang.Language;

public class PointcutQueryLanguage extends Language {
    public static final PointcutQueryLanguage INSTANCE = new PointcutQueryLanguage();

    private PointcutQueryLanguage() {
        super("Go! AOP Pointcut query");
    }
}
