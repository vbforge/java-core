package com.vbforge.practice.practice_5_logical_operators_and_shortcircuit;

/**
 * Short-circuit && and || with side-effecting operands (`x++`, `y++`).
 *
 * Key insight: when the left operand already determines the result,
 * the right operand is NOT evaluated — so its side effects do not happen.
 */
public class ShortCircuitWithSideEffectsDemo {

    public static void main(String[] args) {
        System.out.println("--- && stops at the first false ---");
        int x = 1, y = 1;
        // First sub-expression: x++ == y++ → both get 1, both become 2; result true.
        // Second sub-expression: x++ != y++ → both get 2, both become 3; 2 != 2 is false.
        // Total: true && false = false. Both increments happened.
        System.out.println(x++ == y++ && x++ != y++);   // false
        System.out.println("after && : x = " + x + ", y = " + y); // 3, 3

        System.out.println("\n--- || stops at the first true ---");
        x = 1; y = 1;
        // First sub-expression: x++ == y++ → both 1 → both 2; result true.
        // Because of short-circuit, the RIGHT side is not evaluated.
        System.out.println(x++ == y++ || x++ != y++);   // true
        System.out.println("after || : x = " + x + ", y = " + y); // 2, 2
    }
}
