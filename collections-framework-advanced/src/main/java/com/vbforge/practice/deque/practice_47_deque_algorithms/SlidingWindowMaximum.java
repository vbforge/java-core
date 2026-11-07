package com.vbforge.practice.deque.practice_47_deque_algorithms;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class SlidingWindowMaximum {
    public static void main(String[] args) {

        int[] nums = {1,3,-1,-3,5,3,6,7};
        int k = 3;
        int[] result = maxSlidingWindow(nums, k);
        System.out.println("Max in each sliding window:");
        System.out.println(Arrays.toString(result));

    }

    private static int[] maxSlidingWindow(int[] nums, int k) {

        if (nums == null || k <= 0) return new int[0];

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // Stores indices

        for (int i = 0; i < n; i++) {
            // Remove indices out of the current window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove smaller elements from the back
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.offerLast(i); // Add current index

            // Record the maximum for current window
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;

    }
}
