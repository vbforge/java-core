package com.vbforge.practice.practice_14_do_while_loop;

/**
 * do-while ALWAYS executes the body at least once, then checks the condition;
 * while checks the condition first. This matters when the condition can be false from the start.
 */
public class DoWhileRunsAtLeastOnceDemo {

    public static void main(String[] args) {
        System.out.println("--- while: condition false from the start ---");
        int a = 2;
        while (a < 2) {
            System.out.println("this never prints");
            a++;
        }
        System.out.println("while: body ran 0 times");

        System.out.println("\n--- do-while: same condition, body runs once ---");
        int b = 2;
        do {
            System.out.println("this prints once");
            b++;
        } while (b < 2);
        System.out.println("do-while: body ran 1 time");
    }
}
