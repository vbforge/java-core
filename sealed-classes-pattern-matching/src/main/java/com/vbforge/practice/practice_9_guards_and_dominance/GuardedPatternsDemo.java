package com.vbforge.practice.practice_9_guards_and_dominance;

/**
 * A guard adds a boolean condition to a pattern: case Integer i when i < 0.
 * The label matches only if the type matches AND the guard is true.
 */
public class GuardedPatternsDemo {

    static String classify(Object o) {
        return switch (o) {
            case Integer i when i < 0      -> "negative int";
            case Integer i when i == 0     -> "zero";
            case Integer i                 -> "positive int";
            case String s when s.isBlank() -> "blank string";
            case String s                  -> "string '" + s + "'";
            default                        -> "other";
        };
    }

    public static void main(String[] args) {
        Object[] values = {-5, 0, 12, "  ", "java", 2.5};
        for (Object v : values) {
            System.out.println("'" + v + "' -> " + classify(v));
        }
    }
}
