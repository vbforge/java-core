package com.vbforge.practice.practice_10_string_builder;

/** append, insert, delete, replace, reverse, indexOf, charAt, setCharAt, substring on one StringBuilder. */
public class StringBuilderCoreApiDemo {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("Hello");
        System.out.println("initial:        " + sb);

        sb.append(", ").append("World").append("!");
        System.out.println("append:         " + sb);

        sb.insert(5, " Beautiful");
        System.out.println("insert(5,...):  " + sb);

        sb.delete(5, 15);
        System.out.println("delete(5,15):   " + sb);

        sb.replace(7, 12, "Java");
        System.out.println("replace(7,12):  " + sb);

        sb.reverse();
        System.out.println("reverse():      " + sb);

        sb.reverse(); // restore
        System.out.println("indexOf(\"o\"):   " + sb.indexOf("o"));
        System.out.println("length():       " + sb.length());
        System.out.println("charAt(0):      " + sb.charAt(0));

        sb.setCharAt(0, 'h');
        System.out.println("setCharAt(0,h): " + sb);
        System.out.println("substring(7):   " + sb.substring(7));
        System.out.println("toString():     " + sb);

        StringBuilder born = new StringBuilder("Bon");
        born.insert(2, 'r');
        System.out.println("insert(2,'r'):  " + born); // Born
    }
}
