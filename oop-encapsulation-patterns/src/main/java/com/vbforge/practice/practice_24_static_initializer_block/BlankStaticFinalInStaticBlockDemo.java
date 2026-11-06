package com.vbforge.practice.practice_24_static_initializer_block;

import java.util.Arrays;

/** A blank 'static final' field can be assigned exactly once, inside a static block. */
public class BlankStaticFinalInStaticBlockDemo {

    private static final char[] alpha;

    static {
        alpha = new char[26];
        int i = 0;
        for (char c = 'a'; i < alpha.length; c++, i++) {
            alpha[i] = c;
        }
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(alpha));
    }
}
