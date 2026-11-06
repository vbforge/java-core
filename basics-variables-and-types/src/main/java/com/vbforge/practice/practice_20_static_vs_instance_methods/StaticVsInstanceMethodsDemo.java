package com.vbforge.practice.practice_20_static_vs_instance_methods;

/**
 * Static vs instance methods.
 *
 *   - a static method belongs to the class and is called without an object
 *   - an instance method needs an object and can read that object's fields
 */
public class StaticVsInstanceMethodsDemo {

    private final String label;

    public StaticVsInstanceMethodsDemo(String label) {
        this.label = label;
    }

    static int add(int a, int b) {
        return a + b;
    }

    public String greet() {
        return "Hello from " + label;
    }

    public static void main(String[] args) {
        System.out.println("static add(2,3): " + add(2, 3) + " <- call without instance");
        StaticVsInstanceMethodsDemo demo = new StaticVsInstanceMethodsDemo("Java Core");
        System.out.println(demo.greet() + " <- needs instance");
    }
}
