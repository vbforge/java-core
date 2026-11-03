package com.vbforge.practice.practice_4_array_copying;

import java.util.Arrays;

/**
 * {@code Arrays.copyOf} gives an independent copy for primitives (value semantics)
 * but a SHALLOW copy for objects: the references are copied, the objects are shared.
 * That is why mutating an object through the copy also changes the original.
 */
public class ShallowVsDeepCopyDemo {

    public static void main(String[] args) {
        System.out.println("--- primitives: independent copy ---");
        int[] original = {1, 2, 3};
        int[] copy = Arrays.copyOf(original, original.length);
        copy[0] = 99;
        System.out.println("original: " + Arrays.toString(original)); // [1, 2, 3]
        System.out.println("copy    : " + Arrays.toString(copy));     // [99, 2, 3]

        System.out.println("\n--- objects: shared references ---");
        StringBuilder[] objs = {new StringBuilder("A"), new StringBuilder("B")};
        StringBuilder[] shallow = Arrays.copyOf(objs, objs.length);
        shallow[0].append("_modified");
        System.out.println("original[0]: " + objs[0]);     // A_modified
        System.out.println("shallow[0] : " + shallow[0]);  // A_modified - same object

        System.out.println("\n--- true deep copy: rebuild each element ---");
        StringBuilder[] deep = new StringBuilder[objs.length];
        for (int i = 0; i < objs.length; i++) deep[i] = new StringBuilder(objs[i].toString());
        deep[0].append("_independent");
        System.out.println("original[0]: " + objs[0]);     // A_modified
        System.out.println("deep[0]    : " + deep[0]);     // A_modified_independent
    }
}
