package com.vbforge.practice.practice_8_documented;

import java.lang.annotation.Documented;

/** Runs Tester and shows which annotation types carry the @Documented meta-annotation. */
public class DocumentedDemo {

    public static void main(String[] args) {
        Tester tester = new Tester();
        tester.doSomeTestDocument();
        tester.doSomeTestNoDocument();

        // @Documented itself has RUNTIME retention, so it can be read from the annotation types
        System.out.println("@TestDocumented is @Documented    : "
                + TestDocumented.class.isAnnotationPresent(Documented.class));
        System.out.println("@TestNotDocumented is @Documented : "
                + TestNotDocumented.class.isAnnotationPresent(Documented.class));
    }
}
