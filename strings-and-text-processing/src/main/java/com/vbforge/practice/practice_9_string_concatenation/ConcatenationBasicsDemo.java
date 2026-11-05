package com.vbforge.practice.practice_9_string_concatenation;

/** + between Strings and between a String and a number; += creates a new String each time. */
public class ConcatenationBasicsDemo {

    public static void main(String[] args) {
        String s = "Hello";
        String space = " ";
        String name = "Bob";
        System.out.println(s + space + name);
        System.out.println("Hello" + " " + "John");
        System.out.println("Number: " + 5);

        String j = "Hello! Nice to meet you, what is your name? - ";
        String jn = "My name is Jack";
        System.out.println("Jack: " + (j + jn));
        String an = "Oh, Hi Jack!- ";
        String a = "I'm Alex. How you doing?";
        System.out.println("Alex: " + (an + a));

        String str = "mixed";
        str += "hello";
        System.out.println("after +=: " + str);
    }
}
