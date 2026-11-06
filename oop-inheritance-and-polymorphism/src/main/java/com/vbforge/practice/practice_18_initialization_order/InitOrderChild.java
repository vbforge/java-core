package com.vbforge.practice.practice_18_initialization_order;

import com.vbforge.practice.practice_18_initialization_order.InitOrderParent;

class InitOrderChild extends InitOrderParent {
    static String childStaticField = InitOrderParent.initStatic("Child static field");
    String childInstanceField      = InitOrderParent.initInstance("Child instance field");

    static { System.out.println("[Child] static block"); }
    { System.out.println("[Child] instance block"); }

    public InitOrderChild() {
        // super() is called implicitly first
        System.out.println("[Child] constructor");
    }
}
