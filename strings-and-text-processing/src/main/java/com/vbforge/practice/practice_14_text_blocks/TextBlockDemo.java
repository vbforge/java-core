package com.vbforge.practice.practice_14_text_blocks;

/**
 * Text block (Java 15+): multi-line String; common leading indentation is stripped
 * (relative to the closing delimiter or the least-indented line) and trailing spaces on each line are removed.
 */
public class TextBlockDemo {

    public static void main(String[] args) {
        String block = """
                       Hello World! 
                       Hello Java!
                       """;
        System.out.println(block);
        System.out.println("lines: " + block.lines().count());
        System.out.println("first line ends with space? " + block.lines().findFirst().get().endsWith(" ")); // false
    }
}
