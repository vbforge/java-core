package com.vbforge.practice.practice_10_string_builder;

/**
 * String: immutable. StringBuilder: mutable, not synchronized. StringBuffer: mutable, synchronized.
 * "Modifying" a String gives a new object; the other two change in place.
 */
public class StringVsBuilderVsBufferDemo {

    public static void main(String[] args) {
        String immut = "Hello";
        StringBuilder mut = new StringBuilder("Hello");
        StringBuffer sync = new StringBuffer("Hello");
        immut = immut + " World";   // new String object created
        mut.append(" World");       // same object mutated
        sync.append(" World");      // same object mutated (synchronized)
        System.out.println("String (reassigned):      " + immut);
        System.out.println("StringBuilder (mutated):  " + mut);
        System.out.println("StringBuffer (mutated):   " + sync);
    }
}
