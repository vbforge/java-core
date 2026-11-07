package com.vbforge.practice.deque.practice_50_stack_problems;

import java.util.Stack;

public class ReverseStringUsingStack {
    public static void main(String[] args) {

        String str = "Hello World!";

        String reversedString = reverse(str);
        System.out.println("Original: " + str);
        System.out.println("Reversed: " + reversedString);

    }

    private static String reverse(String str){

        Stack<Character> characters = new Stack<>();

        for (char ch : str.toCharArray()) {
            characters.push(ch);
            System.out.print(ch + " ");
        }
        System.out.println();
        StringBuilder reversed = new StringBuilder();
        while (!characters.isEmpty()){
            Character popped = characters.pop();
            System.out.print(popped + " ");
            reversed.append(popped);
        }
        System.out.println();

        return reversed.toString();
    }

}
