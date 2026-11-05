package com.vbforge.practice.practice_8_classic_switch;

import java.util.Random;

/**
 * Classic switch on a char - grouping cases by leaving out break, with break ending each group.
 * Random lowercase letters are produced with (char) ('a' + random.nextInt(26));
 * the Random is seeded so the output is the same on every run.
 */
public class SwitchOnCharDemo {

    public static void main(String[] args) {
        Random random = new Random(42);
        for (int i = 0; i < 20; i++) {
            char c = (char) ('a' + random.nextInt(26));
            System.out.print(c + " -> ");
            switch (c) {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                    System.out.println("vowel");
                    break;
                case 'y':
                case 'w':
                    System.out.println("sometimes a vowel");
                    break;
                default:
                    System.out.println("consonant");
            }
        }
    }
}
