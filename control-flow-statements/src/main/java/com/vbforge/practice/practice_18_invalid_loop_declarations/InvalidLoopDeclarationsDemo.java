package com.vbforge.practice.practice_18_invalid_loop_declarations;

/**
 * Common compile-time errors around loop declarations:
 *   - a variable declared inside for (...) is NOT visible after the loop
 *   - while (int y = 2) is illegal - a declaration is not allowed in a while condition
 *   - for (...; cond1, cond2; ...) is illegal - Java has no comma operator
 *
 * Kept as documentation; uncomment a line to see the compile error.
 */
public class InvalidLoopDeclarationsDemo {

    public static void main(String[] args) {
        System.out.println("--- for-loop variable is block-scoped ---");
        for (int i = 0; i < 4; i += 2) {
            System.out.print(i + " ");
        }
        System.out.println();
        // System.out.println(i);   // compile error: i out of scope

        // while (int y = 2) { }    // compile error: declaration in while condition

        // for (int y = 0; (y > 7), (z < 3); y++) { } // compile error: comma is not a conditional operator

        System.out.println("all valid constructs above; illegal ones commented");
    }
}
