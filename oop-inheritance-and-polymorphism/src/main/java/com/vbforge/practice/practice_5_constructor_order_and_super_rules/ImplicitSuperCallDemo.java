package com.vbforge.practice.practice_5_constructor_order_and_super_rules;

/**
 * Implicit super() call.
 *
 * If a subclass constructor does not call super(...) or this(...), Java inserts
 * super() as the first statement. That requires the superclass to have a
 * no-arg (or default) constructor - even when it also has other constructors.
 */
public class ImplicitSuperCallDemo {

    // ---- Case 1: superclass has a no-arg ctor -> implicit super() works ----
    static class Cat1 { Cat1() { System.out.println("Cat1 ctor (no-arg)"); } }
    static class British1 extends Cat1 { British1() { System.out.println("British1 ctor"); } }

    // ---- Case 2: superclass has several ctors -> implicit super() picks the no-arg one ----
    static class Cat3 {
        Cat3()            { System.out.println("Cat3 default ctor"); }
        Cat3(String name) { System.out.println("Cat3 ctor " + name); }
    }
    static class British3 extends Cat3 {
        British3(String name) {
            // implicit super() -> Cat3(), NOT Cat3(String): the name is not forwarded
            System.out.println("British3 ctor " + name);
        }
    }

    public static void main(String[] args) {
        System.out.println("-- Case 1: no-arg superclass ctor --");
        new British1();

        System.out.println("\n-- Case 2: implicit super() picks the no-arg ctor --");
        new British3("Mulberry");
    }
}
