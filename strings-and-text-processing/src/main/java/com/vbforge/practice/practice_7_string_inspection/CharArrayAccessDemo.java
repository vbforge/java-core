package com.vbforge.practice.practice_7_string_inspection;

/** Getting at the characters: toCharArray, chars() stream codes, getChars into a buffer. */
public class CharArrayAccessDemo {

    public static void main(String[] args) {
        String word = "Java";
        char[] chars = word.toCharArray();
        System.out.print("toCharArray: ");
        for (char c : chars) System.out.print(c + " ");
        System.out.println();

        System.out.print("chars() as ints: ");
        word.chars().forEach(c -> System.out.print(c + " "));
        System.out.println();

        String f = "Software And Hardware!";
        char[] dst = new char[3];
        f.getChars(9, 12, dst, 0);
        System.out.println("getChars(9,12): " + new String(dst)); // And

        String s = "Java programming language is fun!";
        char[] buf = new char[16];
        s.getChars(0, 16, buf, 0);
        System.out.println("buf: " + new String(buf));

        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) sb.append('*').append(c);
        sb.append('*');
        System.out.println(sb);
    }
}
