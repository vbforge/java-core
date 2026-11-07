package com.vbforge.practice.deque.practice_49_stack_basics;

import java.util.Stack;
import java.util.EmptyStackException;

public class StackBasicMethods {
    public static void main(String[] args) {

        //create Stack
        Stack<String> stack = new Stack<>();

        //add items
        System.out.println("add elements by push() method...");
        stack.push("a");
        stack.push("b");
        stack.push("c");
        stack.push("d");
        stack.push("e");

        System.out.println("Print stack:");
        System.out.println(stack);


        System.out.println("\npeek() method actions:");
        //return top
        Object topElement = stack.peek();
        System.out.println("topElement="+topElement);
        topElement = stack.peek();
        System.out.println("topElement="+topElement);
        topElement = stack.peek();
        System.out.println("topElement="+topElement);

        System.out.println("\nsize() method: " + stack.size());


        System.out.println("\npop() method actions:");
        //return top and remove it

        Object element = stack.pop();
        System.out.println("element: " + element);
        System.out.println("size: " + stack.size());
        System.out.println(stack);
        System.out.println();

        element = stack.pop();
        System.out.println("element: " + element);
        System.out.println("size: " + stack.size());
        System.out.println(stack);
        System.out.println();

        element = stack.pop();
        System.out.println("element: " + element);
        System.out.println("size: " + stack.size());
        System.out.println(stack);
        System.out.println();

        element = stack.pop();
        System.out.println("element: " + element);
        System.out.println("size: " + stack.size());
        System.out.println(stack);
        System.out.println();

        element = stack.pop();
        System.out.println("element: " + element);
        System.out.println("size: " + stack.size());
        System.out.println(stack);
        System.out.println();


        System.out.println("pop() on empty stack: EmptyStackException");
        try {
            stack.pop();
        } catch (EmptyStackException e) {
            System.out.println("caught EmptyStackException");
        }

    }
}
