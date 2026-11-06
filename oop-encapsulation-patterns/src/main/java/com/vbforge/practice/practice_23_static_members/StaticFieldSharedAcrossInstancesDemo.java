package com.vbforge.practice.practice_23_static_members;

/**
 * One static field per class, one instance field per object.
 * Writing the static field through any reference (or the class name) changes it for everyone.
 */
public class StaticFieldSharedAcrossInstancesDemo {

    private static int staticField;
    private int nonStaticField;

    public StaticFieldSharedAcrossInstancesDemo(int staticValue, int instanceValue) {
        staticField    = staticValue;
        nonStaticField = instanceValue;
    }

    public void show(String label) {
        System.out.println(label + " -> static = " + staticField + ", instance = " + nonStaticField);
    }

    public static void main(String[] args) {
        StaticFieldSharedAcrossInstancesDemo a = new StaticFieldSharedAcrossInstancesDemo(10, 200);
        a.show("first object");

        StaticFieldSharedAcrossInstancesDemo.staticField = -10;
        a.show("after class-level update");

        StaticFieldSharedAcrossInstancesDemo b = new StaticFieldSharedAcrossInstancesDemo(1, 2);
        a.show("first object, after second created");
        b.show("second object");

        b.staticField = -20;          // legal, but misleading: it is the CLASS field
        b.nonStaticField = -300;
        a.show("first object, after b changed the static field");
        b.show("second object, after b changed both");
    }
}
