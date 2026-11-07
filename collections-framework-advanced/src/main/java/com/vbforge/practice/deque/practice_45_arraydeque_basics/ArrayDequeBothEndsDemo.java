package com.vbforge.practice.deque.practice_45_arraydeque_basics;

import java.util.ArrayDeque;
import java.util.Deque;

public class ArrayDequeBothEndsDemo {
    public static void main(String[] args) {

        Deque<String> deque = new ArrayDeque<>();

        // Adding elements
        deque.addFirst("A");
        deque.addLast("B");
        deque.offerFirst("C");
        deque.offerLast("D");

        System.out.println("Deque: " + deque); // [C, A, B, D]

        // Removing elements
        System.out.println("Removed first: " + deque.removeFirst()); // C
        System.out.println("Removed last: " + deque.removeLast());   // D

        // Peeking
        System.out.println("Peek first: " + deque.peekFirst()); // A
        System.out.println("Peek last: " + deque.peekLast());   // B

        // Iteration (front to back)
        System.out.println("Iterate:");
        for (String s : deque) {
            System.out.println(s);
        }

    }
}
