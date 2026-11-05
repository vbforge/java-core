package com.vbforge.practice.practice_14_variable_scope;

/**
 * Java has no true "global" variables - the equivalent is a `static` field,
 * visible to every method of the class and living as long as the class.
 */
public class StaticFieldScopeDemo {

    // static field = "global" to this class
    private static int globalCounter = 0;

    public static void main(String[] args) {
        System.out.println("initial: " + globalCounter);
        increaseCounter();
        System.out.println("after increaseCounter(): " + globalCounter);
        doubleCounter();
        System.out.println("after doubleCounter():   " + globalCounter);
    }

    private static void increaseCounter() {
        globalCounter += 10;
    }

    private static void doubleCounter() {
        globalCounter *= 2;
    }
}
