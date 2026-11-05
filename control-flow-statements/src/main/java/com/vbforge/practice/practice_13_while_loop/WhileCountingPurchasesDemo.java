package com.vbforge.practice.practice_13_while_loop;

/** while with a counter: keep buying while the remaining money covers one more item. */
public class WhileCountingPurchasesDemo {

    public static void main(String[] args) {
        System.out.println("$7 with $2 candies  -> " + candiesBought(7, 2));
        System.out.println("$10 with $2 candies -> " + candiesBought(10, 2));
        System.out.println("$1 with $2 candies  -> " + candiesBought(1, 2));
    }

    static int candiesBought(int money, int price) {
        int count = 0;
        int spent = 0;
        while (money - spent >= price) {
            count++;
            spent += price;
        }
        return count;
    }
}
