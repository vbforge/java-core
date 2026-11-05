package com.vbforge.practice.practice_18_pass_by_value_reference;

/**
 * String is immutable: a method cannot change the caller's String,
 * so it looks like pass-by-value of a primitive (it is the reassignment case again).
 */
public class StringParameterDemo {

    private static void tryModifyString(String s) {
        s = s + " modified";
    }

    public static void main(String[] args) {
        String s = "hello";
        tryModifyString(s);
        System.out.println("after tryModifyString: \"" + s + "\"");
    }
}
