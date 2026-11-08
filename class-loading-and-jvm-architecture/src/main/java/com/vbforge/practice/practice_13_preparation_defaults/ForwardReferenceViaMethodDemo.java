package com.vbforge.practice.practice_13_preparation_defaults;

/**
 * Preparation gives every static field its default value before any initialiser runs.
 * Initialisers then run top to bottom, so a method called early sees the default of a
 * field declared below it. `final` alone does not help; only a compile-time constant
 * already has its value (it is inlined, and carried as ConstantValue in the class file).
 */
public class ForwardReferenceViaMethodDemo {

    static int seenSecond = peekSecond();
    static int seenComputed = peekComputed();
    static int seenConstant = peekConstant();

    static int second = 5;
    static final int COMPUTED = Integer.parseInt("8");
    static final int CONSTANT = 8;

    static int peekSecond() {
        return second;
    }

    static int peekComputed() {
        return COMPUTED;
    }

    static int peekConstant() {
        return CONSTANT;
    }

    public static void main(String[] args) {
        System.out.println("second   seen early = " + seenSecond + "  (now " + second + ")");
        System.out.println("COMPUTED seen early = " + seenComputed + "  (now " + COMPUTED + ", final but computed)");
        System.out.println("CONSTANT seen early = " + seenConstant + "  (compile-time constant)");
    }
}
