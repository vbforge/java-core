package com.vbforge.practice.practice_8_string_transformation;

/** Capitalise the first letter of every word (other letters are left as they are), using split + StringBuilder. */
public class CapitalizeEachWordDemo {

    public static void main(String[] args) {
        String line = "this is a SIMPLE sentence.";
        System.out.println("original:    " + line);
        System.out.println("capitalized: " + capitalizeEachWord(line));
    }

    static String capitalizeEachWord(String line) {
        StringBuilder result = new StringBuilder();
        for (String w : line.split(" ")) {
            if (result.length() > 0) result.append(' ');
            if (!w.isEmpty()) {
                result.append(w.substring(0, 1).toUpperCase()).append(w.substring(1));
            }
        }
        return result.toString();
    }
}
