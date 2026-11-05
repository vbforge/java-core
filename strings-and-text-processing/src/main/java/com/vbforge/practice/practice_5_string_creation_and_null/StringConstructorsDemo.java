package com.vbforge.practice.practice_5_string_creation_and_null;

/** Ways to build a String: from a literal, char[], byte[] (ASCII codes) and a single char. */
public class StringConstructorsDemo {

    public static void main(String[] args) {
        String fromLiteral = new String("Java");
        char[] chars = {'j', 'a', 'v', 'a'};
        byte[] bytes = {48, 53, 65, 90, 97};
        System.out.println("new String(String):  " + fromLiteral);
        System.out.println("new String(char[]):  " + new String(chars));
        System.out.println("new String(byte[]):  " + new String(bytes)); // 05AZa
        System.out.println("Character.toString:  " + Character.toString('\''));
    }
}
