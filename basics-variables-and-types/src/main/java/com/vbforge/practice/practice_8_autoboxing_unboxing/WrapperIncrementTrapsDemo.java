package com.vbforge.practice.practice_8_autoboxing_unboxing;

/**
 * `++` on a wrapper does: unbox -> increment -> rebox.
 * This is fine numerically, but the resulting reference is a NEW object
 * (relevant for identity comparisons - see practice_9_wrapper_identity).
 */
public class WrapperIncrementTrapsDemo {

    public static void main(String[] args) {
        Short s = 300;
        System.out.println("before: s = " + s);

        Short before = s;
        s++;                                        // unbox -> 301 -> rebox

        System.out.println("after : s = " + s);
        System.out.println("same object after ++ ? " + (before == s)); // false
    }
}
