package com.vbforge.practice.practice_9_switch_expression;

/**
 * yield returns the value of a switch expression from a block with several statements.
 * Here the default block prints a note (a side effect) and then yields -1.
 */
public class SwitchExpressionYieldDemo {

    public static void main(String[] args) {
        System.out.println("guest  -> level " + levelOf("guest"));
        System.out.println("admin  -> level " + levelOf("admin"));
        System.out.println("hacker -> level " + levelOf("hacker"));
    }

    static int levelOf(String role) {
        return switch (role) {
            case "guest"     -> 0;
            case "client"    -> 1;
            case "moderator" -> 2;
            case "admin"     -> 3;
            default -> {
                System.out.println("  (unknown role: " + role + ")");
                yield -1;
            }
        };
    }
}
