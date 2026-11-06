package com.vbforge.practice.practice_4_generic_stack;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * A simple generic stack to show how generics compose with data structures.
 * Backed by ArrayList internally - demonstrates "using generics to build generics".
 */
public class GenericStackDemo {

    public static void main(String[] args) {
        System.out.println("=== Generic Stack ===");

        GenericStack<String> stack = new GenericStack<>();
        stack.push("first");
        stack.push("second");
        stack.push("third");
        System.out.println("Stack: " + stack);
        System.out.println("peek: " + stack.peek());
        System.out.println("pop:  " + stack.pop());
        System.out.println("After pop: " + stack);

        // Type safety - GenericStack<Integer> cannot accept Strings:
        GenericStack<Integer> intStack = new GenericStack<>();
        intStack.push(1);
        intStack.push(2);
        // intStack.push("three"); // compile error
        System.out.println("Int stack: " + intStack);
    }

    static class GenericStack<T> {
        private final List<T> data = new ArrayList<>();

        public void push(T item) { data.add(item); }

        public T pop() {
            if (isEmpty()) throw new EmptyStackException();
            return data.remove(data.size() - 1);
        }

        public T peek() {
            if (isEmpty()) throw new EmptyStackException();
            return data.get(data.size() - 1);
        }

        public boolean isEmpty() { return data.isEmpty(); }

        public int size() { return data.size(); }

        @Override
        public String toString() { return data.toString(); }
    }
}
