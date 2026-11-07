package com.vbforge.practice.deque.practice_47_deque_algorithms;

import java.util.ArrayDeque;
import java.util.Deque;

public class PalindromeChecker {
    public static void main(String[] args) {

    String input = "A man, a plan, a canal: Panama";

    System.out.println("Is palindrome? " + isPalindrome(input)); // true

}
    public static boolean isPalindrome(String str) {

        Deque<Character> deque = new ArrayDeque<>();

        for (char c : str.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                deque.addLast(Character.toLowerCase(c));
            }
        }

        while (deque.size() > 1) {
            if (!deque.pollFirst().equals(deque.pollLast())) {   // equals, not != (boxed Characters)
                return false;
            }
        }

        return true;
    }



}
