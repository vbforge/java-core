package com.vbforge.practice.practice_7_var_type_inference;

import java.util.List;

/**
 * `var` (Java 10+) infers the static type of a local variable from its initializer.
 */
public class VarKeywordDemo {

    public static void main(String[] args) {
        var i       = 1;                       // int
        var integer = Integer.valueOf(2);      // Integer
        var str     = "java";                  // String
        System.out.println(i + ", " + integer + ", " + str);

        // legal, but bad practice - hides the inferred type
        var mixed = List.of(1, "2", 5.5, new StringBuilder("Spring"));
        System.out.println(mixed);
    }
}
