package com.vbforge.practice.practice_13_polymorphic_dispatch;

/**
 * A method that accepts a supertype accepts every subtype.
 *
 *   conversation(Parent)  - any Parent, Child or Child2
 *   conversation2(Child)  - only Child (or its subclasses)
 */
public class SupertypeParameterDemo {

    static class Parent {
        public void speak() { System.out.println("Parent.speak();"); }
    }

    static class Child extends Parent {
        public void jump() { System.out.println("Child.jump();"); }
        @Override public void speak() { System.out.println("child.speak"); }
    }

    static class Child2 extends Parent {
        @Override public void speak() { System.out.println("child2.speak();"); }
    }

    private static void conversation(Parent parent) {  // any Parent or subtype
        parent.speak();
    }

    private static void conversation2(Child child) {   // only Child or subclass
        child.speak();
        child.jump();
    }

    public static void main(String[] args) {
        conversation(new Parent());
        conversation(new Child());     // ok - Child IS-A Parent
        conversation(new Child2());    // ok - Child2 IS-A Parent

        conversation2(new Child());
        // conversation2(new Parent());   // compile error - a Parent is not a Child
    }
}
