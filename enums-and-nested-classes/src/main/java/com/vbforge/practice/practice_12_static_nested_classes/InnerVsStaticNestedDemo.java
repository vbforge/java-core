package com.vbforge.practice.practice_12_static_nested_classes;

/**
 * Non-static inner class (InnerB) vs static nested class (InnerC).
 * InnerB needs an enclosing OuterA instance and can call instance and static members of the outer
 * (OuterA.this.n reaches the outer field hidden by its own n).
 * InnerC needs no instance and can only use static members of the outer.
 */
public class InnerVsStaticNestedDemo {

    public static void main(String[] args) {
        OuterA outerA = new OuterA();
        OuterA.InnerB innerB = outerA.new InnerB();            // b + a: outer instance required
        OuterA.InnerB innerB2 = new OuterA().new InnerB();     // one-liner form
        OuterA.InnerC innerC = new OuterA.InnerC();            // c: no outer instance needed

        innerB.doAction();
        innerB2.doAction();

        System.out.println();
        innerC.doAction();
    }
}

class OuterA {

    private int n = 10;

    public void print() {
        System.out.println("OuterA.print()");
    }

    public static void printStatic() {
        System.out.println("OuterA.printStatic()");
    }

    public class InnerB {

        private int n = 20;

        public void doAction() {
            print();
            printStatic();
            System.out.println("InnerB: n = " + n);
            System.out.println("OuterA: n = " + OuterA.this.n);
        }
    }

    public static class InnerC {
        public void doAction() {
            // print();   // does not compile: an instance method needs an outer instance
            printStatic();
        }
    }

    // inner classes can use variables and methods of the outer class;
    // a static nested class can only use the outer's static variables and methods
}
