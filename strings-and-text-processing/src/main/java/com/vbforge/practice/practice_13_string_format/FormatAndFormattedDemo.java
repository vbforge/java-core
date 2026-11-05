package com.vbforge.practice.practice_13_string_format;

/** String.format (static) and formatted() (instance, Java 15+) with the same template. */
public class FormatAndFormattedDemo {

    public static void main(String[] args) {
        String formatted = String.format("Name: %-10s | Score: %05d | GPA: %.2f", "Alice", 42, 3.876);
        System.out.println(formatted);

        String formatted2 = "Name: %-10s | Score: %05d | GPA: %.2f".formatted("Bob", 99, 4.0);
        System.out.println(formatted2);
    }
}
