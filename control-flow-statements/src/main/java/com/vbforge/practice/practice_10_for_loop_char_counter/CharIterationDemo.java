package com.vbforge.practice.practice_10_for_loop_char_counter;

/**
 * A for loop over a char counter: the variable is a numeric code that can be incremented
 * and compared like an int. Iterates ASCII 0..127, skipping 26 (SUB, the Ctrl-Z control
 * character), and prints the numeric value next to the character.
 *
 * Note: many of the values are control characters, so some lines print blank or odd output.
 */
public class CharIterationDemo {

    public static void main(String[] args) {
        for (char c = 0; c < 128; c++) {
            if (c == 26) continue;   // control character, prints nothing useful
            System.out.println("value " + (int) c + " -> '" + c + "'");
        }
    }
}
