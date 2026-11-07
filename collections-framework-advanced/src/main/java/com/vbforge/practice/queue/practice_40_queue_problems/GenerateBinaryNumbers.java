package com.vbforge.practice.queue.practice_40_queue_problems;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Given a number N, generate binary numbers from 1 to N using a queue.
 * */
public class GenerateBinaryNumbers {
    public static void main(String[] args) {

        int n = 10;

        generateBinaryNumbers(n);

    }

    private static void generateBinaryNumbers(int n) {
        Queue<String> queue = new LinkedList<>();
        queue.offer("1");
        for (int i = 1; i <= n; i++) {
            String current = queue.poll();
            System.out.println(current);

            queue.offer(current + "0");   // append the next bit (was prepended: gave 01, 11, 001 ...)
            queue.offer(current + "1");
        }
    }

}
