package com.vbforge.practice.practice_8_string_transformation;

/** Java 11+ helpers: isBlank, repeat, lines. */
public class ModernStringMethodsDemo {

    public static void main(String[] args) {
        System.out.println("isBlank():     " + "   ".isBlank());          // true
        System.out.println("repeat(3):     " + "ab".repeat(3));           // ababab
        System.out.println("lines() count: " + "a\nb\nc".lines().count()); // 3
    }
}
