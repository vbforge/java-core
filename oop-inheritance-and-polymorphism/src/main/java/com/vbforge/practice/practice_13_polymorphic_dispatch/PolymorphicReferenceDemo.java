package com.vbforge.practice.practice_13_polymorphic_dispatch;

/**
 * Polymorphic reference: Parent reference, Child object.
 *
 *   - the overridden method runs the child's version (runtime dispatch)
 *   - child-specific methods are NOT visible through the parent reference
 */
public class PolymorphicReferenceDemo {

    static class Parent {
        public void speak() {
            System.out.println("Parent.speak();");
        }
    }

    static class Child extends Parent {
        public void jump() {
            System.out.println("Child.jump();");
        }

        @Override
        public void speak() {
            System.out.println("child.speak");
        }
    }

    static class Child2 extends Parent {
        @Override
        public void speak() {
            System.out.println("child2.speak();");
        }
        public void run() {
            System.out.println("child2.run();");
        }
    }

    public static void main(String[] args) {
        Parent w = new Parent();
        w.speak();

        Child c = new Child();
        c.jump();
        c.speak();

        Parent w2 = new Child();
        w2.speak();       // run-time dispatch -> Child.speak
        // w2.jump();     // compile error - jump() is declared on Child, not on Parent

        System.out.println("--------");
        Child2 child2 = new Child2();
        child2.speak();
        child2.run();
    }
}
