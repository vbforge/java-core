package com.vbforge.practice.practice_9_exception_propagation;

/**
 * Exception propagation through a three-method call chain.
 *
 * The exception is thrown in method3, propagates up to method2, then method1.
 * Each method either catches it or declares it in `throws`. A static String
 * accumulates a visible trace so we can see exactly how far execution got
 * before the throw.
 *
 * The key insight: statements AFTER the throwing call in a method are SKIPPED.
 */
public class MethodChainDemo {

    private static String trace = "a";

    public static void main(String[] args) {
        new MethodChainDemo().method1();
        System.out.println(trace);    // expect "ac" — method3 throws before method2 can append "2"
    }

    void method1() {
        try {
            method2();
        } catch (Exception e) {
            trace += "c";
        }
    }

    void method2() throws Exception {
        method3();        // throws immediately
        trace += "2";     // never reached
        method3();
        trace += "2b";    // never reached
    }

    void method3() throws Exception {
        throw new Exception();
    }
}
