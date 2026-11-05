package com.vbforge.practice.practice_11_inner_classes;

/** An inner (non-static) class needs an outer instance: {@code outer.new Inner()} or {@code new Outer().new Inner()}. */
public class InnerClassCreationDemo {

    public static void main(String[] args) {
        Outer outer = new Outer();
        outer.printInfo();
        Outer.Inner inner = new Outer().new Inner();
        Outer.Inner inner2 = outer.new Inner();
        inner.doAction();
        inner2.doAction();
    }
}

class Outer {

    public int type = 1;

    public void printInfo() {
        System.out.println("Outer.type = " + type);
    }

    public class Inner {
        public String title = "Test";

        public void doAction() {
            System.out.println("Inner.title = " + title);
        }
    }
}
