package com.vbforge.practice.practice_18_initialization_order;

/**
 * Exact initialization order.
 *
 * Once, when a class is first used (parent class first):
 *   1. static fields and static blocks, in textual order
 *
 * Every time 'new' is called, per class from the top of the hierarchy down:
 *   2. super() (implicit or explicit) finishes first
 *   3. instance fields and instance blocks, in textual order
 *   4. constructor body
 *
 * If a constructor starts with this(...), only the delegated constructor
 * runs the instance initialisation (it is not done twice).
 */
public class InitializationOrderDemo {

    public static void main(String[] args) {
        System.out.println("--- First 'new InitOrderChild()' ---");
        new InitOrderChild();

        System.out.println("\n--- Second 'new InitOrderChild()' (static parts are already done) ---");
        new InitOrderChild();
    }
}
