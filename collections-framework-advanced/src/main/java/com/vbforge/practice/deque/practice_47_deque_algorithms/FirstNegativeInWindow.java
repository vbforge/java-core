package com.vbforge.practice.deque.practice_47_deque_algorithms;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class FirstNegativeInWindow {
    public static void main(String[] args) {

        long[] arr = {12, -1, -7, 8, -15, 30, 16, 28};
        int k = 3;
        System.out.println("First negatives: " + Arrays.toString(printFirstNegativeInteger(arr, arr.length, k)));

    }

    public static long[] printFirstNegativeInteger(long[] arr, int N, int K) {
        Deque<Integer> deque = new ArrayDeque<>();
        long[] result = new long[N - K + 1];

        for (int i = 0; i < N; i++) {
            if (arr[i] < 0) deque.addLast(i);

            if (i >= K - 1) {
                // Remove out-of-window indices
                while (!deque.isEmpty() && deque.peekFirst() < i - K + 1) {
                    deque.pollFirst();
                }

                result[i - K + 1] = deque.isEmpty() ? 0 : arr[deque.peekFirst()];
            }
        }
        return result;
    }

}
