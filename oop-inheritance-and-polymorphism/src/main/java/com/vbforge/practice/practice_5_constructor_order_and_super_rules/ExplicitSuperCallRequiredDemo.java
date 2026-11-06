package com.vbforge.practice.practice_5_constructor_order_and_super_rules;

/**
 * Explicit super(args) call.
 *
 * If the superclass has NO no-arg constructor, every subclass constructor must
 * call super(args) itself. Without it the implicit super() has nothing to call
 * and the code does not compile.
 */
public class ExplicitSuperCallRequiredDemo {

    static class Cat2 {
        Cat2(String name) { System.out.println("Cat2 ctor - name " + name); }
    }

    static class British2 extends Cat2 {
        British2(String name) {
            super(name);   // REQUIRED - Cat2 has no no-arg ctor
            System.out.println("British2 ctor");
        }
    }

    // static class Broken extends Cat2 {
    //     Broken(String name) { }   // compile error: constructor Cat2 in class Cat2 cannot be applied to given types
    // }

    public static void main(String[] args) {
        new British2("Mulberry");
    }
}
