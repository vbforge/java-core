package com.vbforge.practice.practice_15_string_conversion_and_parsing;

/** String -> int with Integer.parseInt, int -> String with + "". Text itself has no arithmetic. */
public class IntegerParsingDemo {

    public static void main(String[] args) {
        String i = "1";
        // compile error: add(i, j) for int params; text has no arithmetic: i = i * 10;
        int res = Integer.parseInt(i);
        System.out.println("res=" + res);
        res = res * 20;
        System.out.println("res=" + res);

        String textRes = res + "";   // int -> String
        System.out.println("resText=" + textRes);
        // compile error: textRes = textRes * 5;

        int w = Integer.parseInt("123");
        System.out.println("w=" + w);
        int w2 = Integer.parseInt("25");
        System.out.println("w2=" + w2);
    }
}
