package com.vbforge.practice.practice_18_initialization_order;

class InitOrderParent {
    static String parentStaticField = initStatic("Parent static field");
    String parentInstanceField      = initInstance("Parent instance field");

    static { System.out.println("[Parent] static block"); }
    { System.out.println("[Parent] instance block"); }

    public InitOrderParent() {
        System.out.println("[Parent] constructor");
    }

    static String initStatic(String label) {
        System.out.println("[Static]   " + label + " initialized");
        return label;
    }

    static String initInstance(String label) {
        System.out.println("[Instance] " + label + " initialized");
        return label;
    }
}
