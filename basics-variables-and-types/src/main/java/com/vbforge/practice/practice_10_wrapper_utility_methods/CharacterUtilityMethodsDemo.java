package com.vbforge.practice.practice_10_wrapper_utility_methods;

/**
 * Character classification and conversion helpers.
 */
public class CharacterUtilityMethodsDemo {

    public static void main(String[] args) {
        char c = 'A';
        System.out.println("isLetter('A'):        " + Character.isLetter(c));
        System.out.println("isDigit('5'):         " + Character.isDigit('5'));
        System.out.println("isWhitespace(' '):    " + Character.isWhitespace(' '));
        System.out.println("isUpperCase('A'):     " + Character.isUpperCase('A'));
        System.out.println("toLowerCase('A'):     " + Character.toLowerCase('A'));
        System.out.println("toUpperCase('z'):     " + Character.toUpperCase('z'));
        System.out.println("getNumericValue('7'): " + Character.getNumericValue('7'));
        System.out.println("isAlphabetic('\u03A9'):    " + Character.isAlphabetic('\u03A9')); // Greek capital omega
    }
}
