package com.vbforge.practice.practice_12_static_nested_classes;

/**
 * Static nested classes, like top-level classes, are used outside the outer body through the outer
 * name: {@code Outer.Nested n = new Outer.Nested();}
 * They can read and write the outer's STATIC fields (even private ones); outer INSTANCE fields
 * need an outer object.
 */
public class StaticNestedAccessToOuterDemo {

    public static void main(String[] args) {
        CallOuterAccess.InnerStatic innerStatic = new CallOuterAccess.InnerStatic();
        innerStatic.test();
    }
}

class CallOuterAccess {
    public int x = 10;
    private static int y = 10;

    public static class InnerStatic {   // nested class marked as static
        public void test() {
            System.out.println("static y (read directly) = " + y);
            // x = 20;   // does not compile: an instance field needs an outer instance
            CallOuterAccess outerAccess = new CallOuterAccess();
            outerAccess.x = 20;
            y = 20;
            System.out.println("CallOuterAccess.test() " + outerAccess.x + ", " + y);
        }
    }
}
