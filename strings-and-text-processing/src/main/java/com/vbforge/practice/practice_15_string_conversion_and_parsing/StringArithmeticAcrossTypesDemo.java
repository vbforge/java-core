package com.vbforge.practice.practice_15_string_conversion_and_parsing;

/**
 * Adding and subtracting when the operands or the wanted result are int or String:
 * parse on the way in, concatenate or "" + on the way out.
 */
public class StringArithmeticAcrossTypesDemo {

    public static void main(String[] args) {
        addIntInt(2, 3);
        addStringString("Real ", " Madrid");
        addIntStringToInt(1, "999");
        addIntStringToString(1878, " Manchester United");
        minusIntInt(100, 1);
        minusStringStringToInt("10", "9");
        minusIntIntToString(10, 8);
        minusStringStringToString("55", "50");
        System.out.println("plus1 (sum + \"\"): " + plus1(2, 3));
        System.out.println("plus2 (a+b+\"\"):   " + plus2(2, 3));
        addWithFormat(4, 5);
        addWithFormat(10, 20);
    }

    static void addIntInt(int a, int b) {
        System.out.println(String.format("add1(int+int); a=%s,b=%s", a, b));
        System.out.println("c= " + (a + b));
    }

    static void addStringString(String a, String b) {
        System.out.println(String.format("add2(String+String); a=%s,b=%s", a, b));
        System.out.println("c: " + (a + b));
    }

    static void addIntStringToInt(int a, String b) {
        System.out.println(String.format("add3(int+String) -> String into int; a=%s,b=%s", a, b));
        System.out.println("c=" + (a + Integer.parseInt(b)));
    }

    static void addIntStringToString(int a, String b) {
        System.out.println(String.format("add4(int+String) -> int into String; a=%s,b=%s", a, b));
        System.out.println("c=" + (a + "" + b));
    }

    static void minusIntInt(int a, int b) {
        System.out.println(String.format("minus1(int-int); a=%s,b=%s", a, b));
        System.out.println("rez=" + (a - b));
    }

    static void minusStringStringToInt(String a, String b) {
        System.out.println(String.format("minus2(String-String) -> rez into int; a=%s,b=%s", a, b));
        System.out.println("rez=" + (Integer.parseInt(a) - Integer.parseInt(b)));
    }

    static void minusIntIntToString(int a, int b) {
        System.out.println(String.format("minus3(int-int) -> rez in String; a=%s,b=%s", a, b));
        System.out.println("rez= " + ((a - b) + ""));
    }

    static void minusStringStringToString(String a, String b) {
        System.out.println(String.format("minus4(String-String) -> rez in String; a=%s,b=%s", a, b));
        System.out.println("rez= " + ((Integer.parseInt(a) - Integer.parseInt(b)) + ""));
    }

    static String plus1(int a, int b) {
        System.out.println("plus1(); a=" + a + "; b=" + b);
        int sum = a + b;
        return sum + "";
    }

    static String plus2(int a, int b) {
        System.out.println("plus2(); a=" + a + "; b=" + b);
        return a + b + "";   // a + b is evaluated as int first, then converted
    }

    static void addWithFormat(int a, int b) {
        String template = "addWithFormat; a=%s; b=%s";
        System.out.println(String.format(template, a, b));
    }
}
