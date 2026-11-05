package com.vbforge.practice.practice_8_string_transformation;

import java.util.Arrays;

/** split(regex) into an array and String.join back into one String. */
public class SplitAndJoinDemo {

    public static void main(String[] args) {
        String csv = "Alice,Bob,Charlie,Diana";
        String[] parts = csv.split(",");
        System.out.println("split(\",\"):   " + Arrays.toString(parts));
        System.out.println("join(\" | \"): " + String.join(" | ", parts));
    }
}
