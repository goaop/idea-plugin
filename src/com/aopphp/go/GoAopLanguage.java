package com.aopphp.go;

import com.intellij.lang.Language;

public class GoAopLanguage extends Language {
    public static final GoAopLanguage INSTANCE = new GoAopLanguage();

    private GoAopLanguage() {
        super("Go! AOP Language");
    }
}
