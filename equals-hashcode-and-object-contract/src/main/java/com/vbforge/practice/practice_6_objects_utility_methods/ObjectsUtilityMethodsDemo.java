package com.vbforge.practice.practice_6_objects_utility_methods;

import java.util.Objects;

/** java.util.Objects: null-safe helpers used when writing equals / hashCode / toString. */
public class ObjectsUtilityMethodsDemo {

    public static void main(String[] args) {
        String s = null;
        System.out.println("Objects.isNull(null):        " + Objects.isNull(s));
        System.out.println("Objects.nonNull(null):       " + Objects.nonNull(s));
        System.out.println("Objects.toString(null, '?'): " + Objects.toString(s, "?"));
        try {
            Objects.requireNonNull(s, "value must not be null");
        } catch (NullPointerException e) {
            System.out.println("requireNonNull threw:        " + e.getMessage());
        }
        System.out.println("Objects.hash(1,2,3):         " + Objects.hash(1, 2, 3));

        // addition: the null-safe equals every equals() implementation relies on
        System.out.println("Objects.equals(null, null):  " + Objects.equals(null, null));
        System.out.println("Objects.equals(\"a\", null):   " + Objects.equals("a", null));
    }
}
