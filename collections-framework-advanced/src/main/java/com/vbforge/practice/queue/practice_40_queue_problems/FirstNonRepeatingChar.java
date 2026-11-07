package com.vbforge.practice.queue.practice_40_queue_problems;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Given a stream of characters, print the first non-repeating character at each step.
 * */
public class FirstNonRepeatingChar {
    public static void main(String[] args) {

        String str = "aabc";

        printFirstNonRepeatingChar(str);

    }

    private static void printFirstNonRepeatingChar(String str) {

        Queue<Character> queue = new LinkedList<>();
        int [] freq = new int[26];

        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            freq[c - 'a']++;
            queue.offer(c);

            //remove repeating characters from the front
            while(!queue.isEmpty() && freq[queue.peek() - 'a'] > 1){
                queue.poll();
            }

            if(queue.isEmpty()){
                System.out.print("-1 ");
            }else {
                System.out.print(queue.peek() + " ");
            }
        }

    }
}
