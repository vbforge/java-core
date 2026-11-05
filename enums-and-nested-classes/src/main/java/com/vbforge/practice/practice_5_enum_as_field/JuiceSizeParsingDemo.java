package com.vbforge.practice.practice_5_enum_as_field;

import java.util.Locale;
import java.util.Scanner;

/**
 * Safe parsing of user text into an enum: trim, upper-case with Locale.ROOT, valueOf, and a default
 * when the text is not a constant. The "user input" is a fixed script so the demo is deterministic.
 */
public class JuiceSizeParsingDemo {

    public static void main(String[] args) {
        Scanner scanner = new Scanner("large\n  small \nXXL\n");

        for (int i = 0; i < 3; i++) {
            System.out.print("Enter juice size (SMALL, MEDIUM, LARGE): ");
            String input = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
            System.out.println(input);

            FreshJuice.FreshJuiceSize size;
            try {
                size = FreshJuice.FreshJuiceSize.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid size! Defaulting to MEDIUM.");
                size = FreshJuice.FreshJuiceSize.MEDIUM;   // default for invalid input
            }

            System.out.println(new FreshJuice(size).getDescription());
        }
        scanner.close();
    }
}
