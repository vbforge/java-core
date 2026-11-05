package com.vbforge.practice.practice_2_else_if_chain_and_system_exit;

/**
 * Branch selection in if / else-if, and System.exit terminates
 * the JVM immediately - nothing after it runs.
 *
 * With i = 10:
 *   i < 10  -> false
 *   i <= 10 -> true   -> prints "2"
 *   System.exit(10)   -> JVM halts here (exit code 10); the final print never executes
 */
public class ElseIfChainAndSystemExitDemo {

    public static void main(String[] args) {
        int i = 10;

        if (i < 10) {
            System.out.print(1);
        } else if (i <= 10) {
            System.out.print(2);
        }

        System.exit(i);
        System.out.print(3); // never runs - System.exit never returns
    }
}
