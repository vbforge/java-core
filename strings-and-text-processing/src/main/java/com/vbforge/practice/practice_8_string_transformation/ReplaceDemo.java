package com.vbforge.practice.practice_8_string_transformation;

/** replace(char, char), replace(CharSequence, CharSequence) and replaceAll(regex, replacement). */
public class ReplaceDemo {

    public static void main(String[] args) {
        String text = "  Hello, World!  ";
        System.out.println("replace('l','L'):  \"" + text.replace('l', 'L') + "\"");
        System.out.println("replaceAll(\\s+):   \"" + text.replaceAll("\\s+", "_") + "\"");

        String str1 = "The weather is fine";
        String newWord = "sunny";
        String str1Updated = str1.replace("fine", newWord.toUpperCase());
        System.out.println("Old String:    " + str1 + " (length " + str1.length() + ")");
        System.out.println("New String:    " + str1Updated + " (length " + str1Updated.length() + ")");
    }
}
