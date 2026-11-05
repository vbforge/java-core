package com.vbforge.practice.practice_1_string_pool;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * String literals live in the JVM String pool, so the SAME reference is shared
 * everywhere - here across a list, a map and a local variable. {@code ==} on those
 * values works for the wrong reason.
 *
 * <p>This is not a reason to use {@code ==} on Strings: use {@code equals()}.
 * It only explains why two collections holding "the same" literal show equal references,
 * and why {@code new String(...)} breaks that.
 */
public class StringPoolSharedReferencesDemo {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("I'm Dynamic");
        list.add("I'm Growing");

        HashMap<Integer, String> map = new HashMap<>();
        map.put(10, "I'm Dynamic");
        map.put(25, "I'm Growing");

        System.out.println("list.get(0) == map.get(10): " + (list.get(0) == map.get(10))); // true
        System.out.println("list.get(1) == map.get(25): " + (list.get(1) == map.get(25))); // true

        String literal = "I'm Growing";
        System.out.println("literal == list.get(1)    : " + (literal == list.get(1)));     // true

        String constructed = new String("I'm Growing");
        System.out.println("new String == list.get(1) : " + (constructed == list.get(1))); // false
        System.out.println("new String.equals(...)    : " + constructed.equals(list.get(1))); // true
    }
}
