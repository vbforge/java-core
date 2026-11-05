package com.vbforge.practice.practice_7_string_inspection;

/** indexOf / lastIndexOf with a char, a String and a start index. -1 means "not found". */
public class IndexOfDemo {

    public static void main(String[] args) {
        String text = "  Hello, World!  ";
        System.out.println("indexOf('o'):        " + text.indexOf('o'));
        System.out.println("lastIndexOf('o'):    " + text.lastIndexOf('o'));

        String s = "Bridges are crucial links that carry cars, "
                + "trucks and trains across bodies of water, mountain gorges or other roads.";
        System.out.println("indexOf('c'):        " + s.indexOf('c'));
        System.out.println("lastIndexOf('d'):    " + s.lastIndexOf('d'));
        System.out.println("indexOf(\"cars\"):    " + s.indexOf("cars"));
        System.out.println("indexOf(',',37):     " + s.indexOf(',', 37));
        System.out.println("indexOf(\".\",10):    " + s.indexOf(".", 10));

        String sub = "String in java is a sequence of characters java";
        System.out.println("indexOf(\"java\"):      " + sub.indexOf("java"));
        System.out.println("lastIndexOf(\"java\"):  " + sub.lastIndexOf("java"));
        System.out.println("indexOf(\"java\",20):   " + sub.indexOf("java", 20));
    }
}
