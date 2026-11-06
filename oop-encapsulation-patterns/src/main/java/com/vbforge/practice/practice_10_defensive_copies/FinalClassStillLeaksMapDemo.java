package com.vbforge.practice.practice_10_defensive_copies;

import java.util.HashMap;
import java.util.Map;

/**
 * 'final' on the CLASS only stops subclassing. It does nothing for a mutable field:
 * the same copy-in / copy-out rule applies, here with a Map.
 */
public class FinalClassStillLeaksMapDemo {

    /** BAD: final class, but the map is stored and returned by reference. */
    static final class MutableClass {
        private final String field;
        private final Map<String, String> fieldMap;

        MutableClass(String field, Map<String, String> fieldMap) {
            this.field = field;
            this.fieldMap = fieldMap;            // stored by reference
        }

        String getField() { return field; }

        Map<String, String> getFieldMap() {
            return fieldMap;                     // returned by reference
        }
    }

    /** GOOD: copies on both sides. */
    static final class AlmostMutableClass {
        private final String field;
        private final Map<String, String> fieldMap;

        AlmostMutableClass(String field, Map<String, String> fieldMap) {
            this.field = field;
            this.fieldMap = new HashMap<>(fieldMap);   // copy IN
        }

        String getField() { return field; }

        Map<String, String> getFieldMap() {
            return new HashMap<>(fieldMap);            // copy OUT
        }
    }

    public static void main(String[] args) {
        System.out.println("-- BAD: final class, leaking map --");
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");

        MutableClass leaky = new MutableClass("mutable", map);
        leaky.getFieldMap().put("unwanted", "new value");
        System.out.println("internal keys after getFieldMap().put(...): " + new java.util.TreeSet<>(leaky.getFieldMap().keySet()));

        System.out.println("\n-- GOOD: copies in constructor and getter --");
        Map<String, String> goodMap = new HashMap<>();
        goodMap.put("good key", "value");

        AlmostMutableClass safe = new AlmostMutableClass("mutable2", goodMap);

        safe.getFieldMap().put("bad key", "another value");
        System.out.println("after put through the getter:  " + safe.getFieldMap().keySet());

        goodMap.put("bad key", "another value");
        System.out.println("after put on the original map: " + safe.getFieldMap().keySet());
    }
}
