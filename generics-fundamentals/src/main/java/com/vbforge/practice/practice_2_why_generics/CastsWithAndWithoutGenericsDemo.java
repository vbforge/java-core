package com.vbforge.practice.practice_2_why_generics;

import java.util.ArrayList;
import java.util.List;

/**
 * What generics buy at the call site: no casts when reading, and the element type
 * is known to the compiler. A raw (pre-Java 5) list forces a cast on every read -
 * and the cast is only the programmer's guess about what is inside.
 *
 * What happens when that guess is wrong: see practice_1_raw_type_class_cast_exception.
 */
public class CastsWithAndWithoutGenericsDemo {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {

        // ================== Scene 1: Java 5 style - raw list, casts everywhere ==================
        System.out.println("--- Java 5 (no generics) ---");
        List noGenerics = new ArrayList();
        noGenerics.add("one");
        noGenerics.add("two");
        noGenerics.add("three");
        noGenerics.add(4);
        noGenerics.add(5);

        String two = (String) noGenerics.get(1);
        int four = (int) noGenerics.get(3);
        System.out.println(two);
        System.out.println(four);

        // ================== Scene 2: List<String> - no cast needed ==================
        System.out.println("---<Generic>---");
        List<String> animals = new ArrayList<>();
        animals.add("cat");
        animals.add("dog");
        animals.add("bird");

        String dog = animals.get(1);   // the compiler knows it is a String
        System.out.println(dog);
    }
}
