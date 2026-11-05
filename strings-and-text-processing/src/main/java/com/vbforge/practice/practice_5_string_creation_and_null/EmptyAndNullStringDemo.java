package com.vbforge.practice.practice_5_string_creation_and_null;

/** An empty String is an object with length 0; null is no object at all. */
public class EmptyAndNullStringDemo {

    public static void main(String[] args) {
        String empty = new String();
        String nullString = null;
        System.out.println("empty.isEmpty():  " + empty.isEmpty());   // true
        System.out.println("empty.length():   " + empty.length());    // 0
        System.out.println("\"\".isEmpty():     " + "".isEmpty());      // true
        if (nullString != null) {
            System.out.println("nullString = " + nullString);
        } else {
            System.out.println("nullString = null");
        }
    }
}
