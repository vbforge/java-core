package com.vbforge.practice.practice_8_classic_switch;

/** Classic switch on a String (compared with equals, case-sensitive). */
public class SwitchOnStringDemo {

    public static void main(String[] args) {
        System.out.println("one   -> " + numberAsWord("one"));
        System.out.println("three -> " + numberAsWord("three"));
        System.out.println("four  -> " + numberAsWord("four"));
    }

    static String numberAsWord(String word) {
        switch (word) {
            case "one":   return "ONE";
            case "two":   return "TWO";
            case "three": return "THREE";
            default:      return "no description";
        }
    }
}
