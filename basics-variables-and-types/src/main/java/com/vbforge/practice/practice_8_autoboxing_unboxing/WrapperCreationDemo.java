package com.vbforge.practice.practice_8_autoboxing_unboxing;

/**
 * The legal ways to create wrapper instances.
 * The `new Wrapper(...)` constructors are deprecated for removal (Java 9+) - do not use them.
 */
public class WrapperCreationDemo {

    public static void main(String[] args) {
        System.out.println("--- autoboxing (recommended) ---");
        Integer intFromLiteral = 100;
        Double  doubleFromLiteral = 0.01;
        System.out.println(intFromLiteral + ", " + doubleFromLiteral);

        System.out.println("\n--- valueOf(primitive) ---");
        Integer intFromValueOf = Integer.valueOf(11);
        Double  doubleFromValueOf = Double.valueOf(11.1);
        System.out.println(intFromValueOf + ", " + doubleFromValueOf);

        System.out.println("\n--- valueOf(String) ---");
        Integer intFromString = Integer.valueOf("11");
        Double  doubleFromString = Double.valueOf("11.1");
        System.out.println(intFromString + ", " + doubleFromString);

        System.out.println("\n--- parseInt(String) - returns primitive, not wrapper ---");
        int primitive = Integer.parseInt("11");
        System.out.println(primitive);

        System.out.println("\n--- Boolean quirks ---");
        Boolean trueAuto  = true;
        Boolean falseAuto = false;
        Boolean fromString = Boolean.valueOf("no true");   // only "true" (any case) is true
        Boolean constant  = Boolean.TRUE;
        System.out.println(trueAuto + ", " + falseAuto + ", " + fromString + ", " + constant);
    }
}
