package com.vbforge.practice.practice_25_instance_initializer_block;

/**
 * Who writes last wins. Static block runs once at class load (before main's body),
 * then per 'new': field initializer -> instance block -> constructor body.
 */
public class InitializerAndConstructorOrderDemo {

    private int a = 5;
    private static int b = 100;

    {
        a = -5;
        System.out.println("Dynamic initialization section");
    }

    public InitializerAndConstructorOrderDemo() {
        a = 10;
        b = 10;
        System.out.println("Constructor");
    }

    static {
        b = -5;
        System.out.println("Static initialization section");
    }

    public static void main(String[] args) {
        System.out.println("Main");
        InitializerAndConstructorOrderDemo obj = new InitializerAndConstructorOrderDemo();
        System.out.println("a=" + obj.a + " b=" + b);   // a=10 b=10
    }
}
