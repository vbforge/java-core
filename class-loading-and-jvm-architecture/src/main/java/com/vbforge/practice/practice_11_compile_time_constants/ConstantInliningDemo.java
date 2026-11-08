package com.vbforge.practice.practice_11_compile_time_constants;

/**
 * A static final primitive or String with a constant initialiser is a compile-time constant:
 * javac copies its value into the user, so reading it never triggers class initialisation.
 * A boxed value or a computed value is not a constant and does trigger it.
 */
public class ConstantInliningDemo {

    static class Constants {
        static final int ANSWER = 42;
        static final String NAME = "vbforge";
        static final Integer BOXED = 7;
        static final int PARSED = Integer.parseInt("5");

        static {
            System.out.println("   [Constants.<clinit>] runs");
        }
    }

    public static void main(String[] args) {
        System.out.println("read ANSWER (int constant):    " + Constants.ANSWER);
        System.out.println("read NAME   (String constant): " + Constants.NAME);
        System.out.println("-- no <clinit> output so far: javac inlined both values into this class --");
        System.out.println("read BOXED  (Integer, not a constant):");
        System.out.println("   BOXED = " + Constants.BOXED);
        System.out.println("read PARSED (computed, not a constant): " + Constants.PARSED);
    }
}
