package com.vbforge.practice.practice_13_polymorphic_dispatch;

/**
 * A factory method with a supertype return type hides which subtype it creates;
 * the caller only sees Parent and dispatch does the rest.
 */
public class FactoryReturnsSupertypeDemo {

    static class Parent {
        public void speak() { System.out.println("Parent.speak();"); }
    }

    static class Child extends Parent {
        @Override public void speak() { System.out.println("child.speak"); }
    }

    static class Child2 extends Parent {
        @Override public void speak() { System.out.println("child2.speak();"); }
    }

    private static Parent getParent(int type) {
        Parent x = null;
        if (type == 0) x = new Parent();
        if (type == 1) x = new Child();
        if (type == 2) x = new Child2();
        return x;
    }

    public static void main(String[] args) {
        for (int type = 0; type <= 2; type++) {
            Parent p = getParent(type);
            System.out.print("type " + type + " -> ");
            p.speak();
        }
    }
}
