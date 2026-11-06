package com.vbforge.practice.practice_10_null_in_switch;

/**
 * A pattern switch throws NullPointerException for a null selector.
 * default does NOT match null.
 */
public class SwitchNullNpeDemo {

    static String describe(Object o) {
        return switch (o) {
            case String s -> "string";
            default       -> "other";
        };
    }

    public static void main(String[] args) {
        System.out.println(describe("x"));
        System.out.println(describe(5));
        try {
            describe(null);
        } catch (NullPointerException e) {
            System.out.println("describe(null) -> NullPointerException");
        }
    }
}
