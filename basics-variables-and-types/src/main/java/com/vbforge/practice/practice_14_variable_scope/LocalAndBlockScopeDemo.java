package com.vbforge.practice.practice_14_variable_scope;

/**
 * Local variables live inside a method (or block) and are not visible outside it.
 * Two methods can each declare their own `x` - they are unrelated variables.
 */
public class LocalAndBlockScopeDemo {

    public static void main(String[] args) {
        functionA();
        System.out.println();
        functionB();
    }

    private static void functionA() {
        int x = 10;
        System.out.println("functionA x = " + x);

        {
            int y = 20;              // block-local
            System.out.println("inner block y = " + y);
        }
        // System.out.println(y);   // compile error - y not visible here
    }

    private static void functionB() {
        int x = 99;                  // different x - local to functionB
        System.out.println("functionB x = " + x);
    }
}
