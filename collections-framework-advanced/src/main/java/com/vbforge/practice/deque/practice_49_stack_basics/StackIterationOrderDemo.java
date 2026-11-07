package com.vbforge.practice.deque.practice_49_stack_basics;

import java.util.Stack;
import java.util.Deque;
import java.util.ArrayDeque;

public class StackIterationOrderDemo {
    public static void main(String[] args) {

        Stack<String> stack = new Stack<>();
        stack.push("A");
        stack.push("B");
        stack.push("C");

        //direct for each (LIFO view)
        for (String item : stack){
            System.out.println("Item: " + item); //bottom to top
        }

        // ArrayDeque used as a stack iterates the other way round: top -> bottom
        Deque<String> dequeStack = new ArrayDeque<>();
        dequeStack.push("A");
        dequeStack.push("B");
        dequeStack.push("C");
        for (String item : dequeStack) {
            System.out.println("ArrayDeque item: " + item); // C, B, A
        }
        // Classic LIFO iteration
        while (!stack.isEmpty()) {
            System.out.println("Pop: " + stack.pop());
        }

    }
}
