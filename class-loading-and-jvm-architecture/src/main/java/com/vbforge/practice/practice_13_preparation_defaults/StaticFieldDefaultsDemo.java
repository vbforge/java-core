package com.vbforge.practice.practice_13_preparation_defaults;

/**
 * The first static initialiser takes a snapshot of every other static field: it sees
 * the preparation defaults (0, 0L, false, 0.0, '\u0000', null), not the values assigned below.
 */
public class StaticFieldDefaultsDemo {

    static final String SNAPSHOT = snapshot();

    static int i = 7;
    static long l = 9L;
    static boolean b = true;
    static double d = 1.5;
    static char c = 'q';
    static String s = "x";

    static String snapshot() {
        return "i=" + i + " l=" + l + " b=" + b + " d=" + d + " c=" + (int) c + " s=" + s;
    }

    public static void main(String[] args) {
        System.out.println("during <clinit>: " + SNAPSHOT);
        System.out.println("after <clinit>:  " + snapshot());
    }
}
