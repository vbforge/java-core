package com.vbforge.practice.practice_7_overload_vs_override;

/**
 * A subclass that overrides one method, inherits another overload and adds a third.
 *
 *   Parent: doAction(), doAction(int)
 *   Child:  doAction()        <- override of Parent.doAction()
 *           doAction(String)  <- NEW overload (does not exist in Parent)
 *           doAction(int)     <- inherited unchanged from Parent
 *           doActionParent()  <- helper that reaches the overridden Parent.doAction() via super
 */
public class SubclassAddsOverloadsDemo {

    static class Parent {
        public void doAction() {
            System.out.println("Parent-doAction()");
        }
        public void doAction(int repeatCount) {
            for (int i = 0; i < repeatCount; i++) {
                System.out.println("Parent-doAction(repeatCount)");
            }
        }
    }

    static class Child extends Parent {
        public void doAction(String text) {
            System.out.println("Child-doAction(text) " + text);
        }

        @Override
        public void doAction() {
            System.out.println("Child-doAction()");
        }

        public void doActionParent() {
            super.doAction();
        }
    }

    public static void main(String[] args) {
        Parent parent = new Parent();
        parent.doAction(2);       // Parent.doAction(int)
        System.out.println();

        Child child = new Child();
        child.doAction();         // Child.doAction() override
        child.doAction(2);        // inherited Parent.doAction(int)
        child.doAction("apple");  // Child.doAction(String) - new overload
        child.doActionParent();   // helper -> super.doAction()
    }
}
