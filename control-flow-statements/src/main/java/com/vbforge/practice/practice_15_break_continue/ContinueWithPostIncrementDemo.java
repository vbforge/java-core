package com.vbforge.practice.practice_15_break_continue;

/**
 * With the increment inside the while condition (i++ < 10), the variable is already updated
 * when continue runs, so continue is safe without a manual increment.
 * Inside the body i is one greater than the value that was compared.
 */
public class ContinueWithPostIncrementDemo {

    public static void main(String[] args) {
        int i = 0;
        while (i++ < 10) {
            if (i == 4 || i == 6 || i == 9) continue;
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
