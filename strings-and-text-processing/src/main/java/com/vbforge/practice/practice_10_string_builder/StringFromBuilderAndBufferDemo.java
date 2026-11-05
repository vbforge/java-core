package com.vbforge.practice.practice_10_string_builder;

/** Building a String from a StringBuilder / StringBuffer with the String constructor. */
public class StringFromBuilderAndBufferDemo {

    public static void main(String[] args) {
        StringBuilder sbSrc = new StringBuilder("String Builder");
        String fromSb = new String(sbSrc);
        StringBuffer sbfSrc = new StringBuffer("String Buffer");
        String fromSbf = new String(sbfSrc);
        System.out.println("from StringBuilder: " + fromSb);
        System.out.println("from StringBuffer:  " + fromSbf);
    }
}
