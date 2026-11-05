package com.vbforge.practice.practice_11_string_buffer;

/**
 * StringBuffer has the same API as StringBuilder but its methods are synchronized.
 * Single-threaded code should prefer StringBuilder. Contains non-ASCII (Cyrillic) text:
 * run with a UTF-8 console to see it correctly.
 */
public class StringBufferApiDemo {

    public static void main(String[] args) {
        StringBuffer buffer = new StringBuffer("Thread");
        buffer.append("-Safe");
        System.out.println("StringBuffer result: " + buffer);

        StringBuffer numberRange = new StringBuffer("4321");
        numberRange.reverse();
        System.out.println("StringBuffer reverse: " + numberRange); // 1234

        StringBuffer str = new StringBuffer("Мы программируем на С++");
        System.out.println("length() = " + str.length());
        System.out.println("capacity() = " + str.capacity());
        str.insert(20, "Java и ");
        System.out.println(str);
        str.replace(27, 30, "Pascal");
        System.out.println(str);
        str.reverse();
        System.out.println(str);
    }
}
