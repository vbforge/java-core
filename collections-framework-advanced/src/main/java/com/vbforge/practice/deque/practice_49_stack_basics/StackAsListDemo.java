package com.vbforge.practice.deque.practice_49_stack_basics;

import java.util.List;
import java.util.Stack;

public class StackAsListDemo {
    public static void main(String[] args) {

        //create Stack as implementation of List interface
        List<Integer> stack = new Stack<>();
        stack.add(100);
        stack.add(200);
        stack.add(300);

        System.out.print("check element after peek/get(index): ");
        System.out.println(stack.get(0));

        System.out.print("check size(): ");
        System.out.println(stack.size());

        System.out.print("return currently removed element: ");
        System.out.println(stack.remove(2)); //return currently removed element

        System.out.print("check size() after pop/remove: ");
        System.out.println(stack.size());

        System.out.print("check element after peek/get(index): ");
        System.out.println(stack.get(1));

        System.out.print("check size() after peek/not-remove: ");
        System.out.println(stack.size());

        System.out.print("check stack is empty: ");
        System.out.println(stack.isEmpty());
    }
}
