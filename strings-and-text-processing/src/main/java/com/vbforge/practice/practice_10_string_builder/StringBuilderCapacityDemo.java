package com.vbforge.practice.practice_10_string_builder;

/** Capacity grows automatically; pre-sizing avoids reallocations when the final size is known. */
public class StringBuilderCapacityDemo {

    public static void main(String[] args) {
        StringBuilder sbCap = new StringBuilder();   // default capacity: 16
        System.out.println("default capacity: " + sbCap.capacity());
        sbCap.append("12345678901234567890");          // 20 chars > 16 -> auto-expand
        System.out.println("after 20 chars:   capacity=" + sbCap.capacity() + " length=" + sbCap.length());
        System.out.println("pre-alloc(1000):  capacity=" + new StringBuilder(1000).capacity());
    }
}
