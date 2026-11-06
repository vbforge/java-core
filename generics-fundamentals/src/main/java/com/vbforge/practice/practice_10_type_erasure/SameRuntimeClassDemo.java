package com.vbforge.practice.practice_10_type_erasure;

import java.util.ArrayList;
import java.util.List;

/**
 * Runtime evidence of erasure: List&lt;String&gt; and List&lt;Integer&gt; are the same class
 * at runtime, so the runtime can only test against the unbounded List&lt;?&gt;.
 */
public class SameRuntimeClassDemo {

    public static void main(String[] args) {

        // ================== Scene 1: one runtime class for every parameterization ==================
        System.out.println("=== Type Erasure ===");
        List<String> strings = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();
        System.out.println("Same class? " + (strings.getClass() == integers.getClass())); // true
        System.out.println(strings.getClass().getName());                                  // java.util.ArrayList

        // ================== Scene 2: instanceof can only use the unbounded wildcard ==================
        Object unknown = strings;
        System.out.println("instanceof List<?>: " + (unknown instanceof List<?>));  // true
        // unknown instanceof List<String>  -> COMPILE ERROR: Object cannot be safely cast to List<String>
        //                                     (the String part does not exist at runtime)
    }
}
