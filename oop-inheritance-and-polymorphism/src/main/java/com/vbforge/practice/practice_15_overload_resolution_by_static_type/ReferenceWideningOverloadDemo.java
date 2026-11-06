package com.vbforge.practice.practice_15_overload_resolution_by_static_type;

/**
 * Overloads are chosen at COMPILE time from the STATIC (declared) type of the argument.
 *
 * Reference widening: the nearest matching supertype wins.
 *   - a String variable -> doJobRef(String)
 *   - an Object variable holding the same String -> doJobRef(Object): the compiler
 *     only knows the declared type, not what the variable holds at runtime
 */
public class ReferenceWideningOverloadDemo {

    static void doJobRef(String s) { System.out.println("String"); }
    static void doJobRef(Object o) { System.out.println("Object"); }

    public static void main(String[] args) {
        String str = "abcd";
        Object obj = str;
        doJobRef(str);   // String
        doJobRef(obj);   // Object - the declared type decides
    }
}
