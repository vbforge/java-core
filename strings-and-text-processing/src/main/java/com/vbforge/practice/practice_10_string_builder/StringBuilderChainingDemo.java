package com.vbforge.practice.practice_10_string_builder;

/** append/insert/reverse all return the builder itself, so calls chain fluently. */
public class StringBuilderChainingDemo {

    public static void main(String[] args) {
        String query = new StringBuilder()
                .append("SELECT ")
                .append("* ")
                .append("FROM users ")
                .append("WHERE active = true")
                .toString();
        System.out.println(query);

        String reversed = new StringBuilder("hello")
                .reverse()
                .insert(0, "[")
                .append("]")
                .toString();
        System.out.println("Reversed + wrapped: " + reversed);
    }
}
