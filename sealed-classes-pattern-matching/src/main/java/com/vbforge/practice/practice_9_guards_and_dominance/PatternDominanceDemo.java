package com.vbforge.practice.practice_9_guards_and_dominance;

/**
 * Dominance: a general pattern placed BEFORE a specific one makes the specific one unreachable,
 * which is a compile error. Order cases from specific/guarded to general.
 *
 * Does NOT compile (kept as comments):
 *   case CharSequence cs -> ...
 *   case String s        -> ...        // dominated by CharSequence
 *   case Integer i       -> ...
 *   case Integer i when i < 0 -> ...   // dominated by the unguarded Integer case
 */
public class PatternDominanceDemo {

    static String kind(Object o) {
        return switch (o) {
            case String s             -> "String: " + s;
            case CharSequence cs      -> "other CharSequence: " + cs;   // after String, so it is reachable
            case Integer i when i < 0 -> "negative Integer";            // guarded first
            case Integer i            -> "Integer";                     // general last
            default                   -> "other";
        };
    }

    public static void main(String[] args) {
        System.out.println(kind("text"));
        System.out.println(kind(new StringBuilder("builder")));
        System.out.println(kind(-1));
        System.out.println(kind(1));
        System.out.println(kind(1.5));
    }
}
