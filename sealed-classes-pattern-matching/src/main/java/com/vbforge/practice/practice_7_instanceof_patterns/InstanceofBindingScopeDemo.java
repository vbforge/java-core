package com.vbforge.practice.practice_7_instanceof_patterns;

/**
 * Where the binding variable is in scope: only where the compiler can prove the match succeeded.
 *   - after && on the same condition
 *   - after a negated check that exits early (the binding survives the if)
 *   - on the right of || when the left side was a negated pattern
 * Using s after a plain || (e.g. o instanceof String s || s.isEmpty()) does not compile.
 */
public class InstanceofBindingScopeDemo {

    static String andScope(Object o) {
        if (o instanceof String s && s.length() > 3) {
            return "long string: " + s;
        }
        return "something else";
    }

    static int negatedEarlyReturn(Object o) {
        if (!(o instanceof String s)) {
            return -1;
        }
        return s.length();                   // s is in scope here
    }

    static String orWithNegation(Object o) {
        if (!(o instanceof String s) || s.isEmpty()) {   // right side runs only when o IS a String
            return "not a non-empty string";
        }
        return s;
    }

    public static void main(String[] args) {
        System.out.println(andScope("hello") + " | " + andScope("hi") + " | " + andScope(5));
        System.out.println(negatedEarlyReturn("abcd") + " | " + negatedEarlyReturn(5));
        System.out.println(orWithNegation("x") + " | " + orWithNegation("") + " | " + orWithNegation(null));
    }
}
