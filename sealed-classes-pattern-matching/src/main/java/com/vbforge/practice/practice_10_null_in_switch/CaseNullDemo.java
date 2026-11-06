package com.vbforge.practice.practice_10_null_in_switch;

/**
 * Handling null inside the switch: a dedicated case null, or case null, default to cover
 * null together with everything else.
 */
public class CaseNullDemo {

    static String withCaseNull(Object o) {
        return switch (o) {
            case null     -> "null!";
            case String s -> "string " + s;
            default       -> "other";
        };
    }

    static String withNullAndDefault(Object o) {
        return switch (o) {
            case String s      -> "string " + s;
            case null, default -> "null or other";
        };
    }

    public static void main(String[] args) {
        System.out.println(withCaseNull(null) + " | " + withCaseNull("a") + " | " + withCaseNull(1));
        System.out.println(withNullAndDefault(null) + " | " + withNullAndDefault("a") + " | " + withNullAndDefault(1));
    }
}
