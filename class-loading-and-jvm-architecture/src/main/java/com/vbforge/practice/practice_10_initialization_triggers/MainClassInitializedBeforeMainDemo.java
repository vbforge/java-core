package com.vbforge.practice.practice_10_initialization_triggers;

/**
 * The JVM initialises the class that contains main() before it calls main():
 * the static initializer is the first thing you see. (Kept from your InitOrderDemo;
 * the instance-block half of that file lives in oop-encapsulation-patterns.)
 */
public class MainClassInitializedBeforeMainDemo {

    private static int staticField = 100;
    private static int staticBlockResult;

    static {
        staticBlockResult = -5;
        System.out.println("[static block] runs before main()");
    }

    public static void main(String[] args) {
        System.out.println("[main] starts");
        System.out.println("staticField       = " + staticField);
        System.out.println("staticBlockResult = " + staticBlockResult);
    }
}
