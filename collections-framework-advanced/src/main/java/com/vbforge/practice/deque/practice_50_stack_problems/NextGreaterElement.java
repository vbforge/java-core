package com.vbforge.practice.deque.practice_50_stack_problems;

import java.util.Stack;

public class NextGreaterElement {
    public static void main(String[] args) {

        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        findNextGreaterElement(arr);

    }

    private static void findNextGreaterElement(int[] arr){
        Stack<Integer> stack = new Stack<>();
        for(int i = arr.length - 1; i >= 0; i--){
            while (!stack.isEmpty() && stack.peek() <= arr[i]){
                stack.pop();
            }
            int nextGreater = stack.isEmpty() ? -1 : stack.peek();
            System.out.println("Next greater element of " + arr[i] + " is " + nextGreater);
            stack.push(arr[i]);
        }
    }

}
