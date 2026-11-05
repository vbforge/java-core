package com.vbforge.practice.practice_16_methods_and_overloading;

/**
 * Overload: same name, different parameter list -> the compiler picks one at compile time.
 * (Override - same signature in a subclass, chosen at runtime - belongs to the OOP modules.)
 */
public class MethodOverloadsDemo {

    public static void main(String[] args) {
        System.out.println("greet()            = " + greet());
        System.out.println("greet(\"Alice\")     = " + greet("Alice"));
        System.out.println("greet(\"Alice\", 3)  = " + greet("Alice", 3));
    }

    static String greet()                        { return "Hello"; }
    static String greet(String name)             { return "Hello, " + name; }
    static String greet(String name, int times)  { return ("Hello, " + name + "! ").repeat(times).trim(); }
}
