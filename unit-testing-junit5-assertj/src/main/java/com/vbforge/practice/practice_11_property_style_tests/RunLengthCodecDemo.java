package com.vbforge.practice.practice_11_property_style_tests;

public class RunLengthCodecDemo {
    public static void main(String[] args) {
        String encoded = RunLengthCodec.encode("aaabcc");
        System.out.println(encoded + " -> " + RunLengthCodec.decode(encoded));   // a3b1c2 -> aaabcc
    }
}
