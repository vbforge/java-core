package com.vbforge.practice.practice_15_overload_resolution_by_static_type;

/**
 * A variable that holds an Integer but is declared as Object or Number still
 * resolves by its declared type. A cast changes the static type and so changes
 * the chosen overload.
 */
public class ObjectHoldingIntegerOverloadDemo {

    static void doJobObj(Integer num) { System.out.println("Matched: Integer"); }
    static void doJobObj(Object obj)  { System.out.println("Matched: Object");  }

    public static void main(String[] args) {
        Object object = 10;            // autoboxed to Integer, held by an Object reference
        doJobObj(object);              // Object  - declared type is Object
        Integer num = 10;
        doJobObj(num);                 // Integer
        doJobObj((Object) 10);         // Object  - cast sets the static type
        doJobObj((Integer) 10);        // Integer
        Number n = 10;
        doJobObj(n);                   // Object  - no Number overload; nearest supertype of Number is Object
        doJobObj((double) 10);         // Object  - autoboxed Double, there is no Double overload
    }
}
