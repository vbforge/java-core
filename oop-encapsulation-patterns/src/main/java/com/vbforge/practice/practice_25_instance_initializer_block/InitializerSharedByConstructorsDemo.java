package com.vbforge.practice.practice_25_instance_initializer_block;

/** Code common to all constructors can live in one instance initializer block: it runs before each of them. */
public class InitializerSharedByConstructorsDemo {

    static class Widget {
        {
            System.out.println("Common part of constructors invoked");
        }

        Widget() {
            System.out.println("Default constructor");
        }

        Widget(int x) {
            System.out.println("Parameterized constructor");
        }
    }

    public static void main(String[] args) {
        new Widget();
        new Widget(10);
    }
}
