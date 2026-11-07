package com.vbforge.practice.list.practice_12_linkedlist_standard_api;

import java.util.LinkedList;

public class LinkedListBasicMethodsDemo {
    public static void main(String[] args) {

        LinkedList<Integer> numbers = new LinkedList<>();

        numbers.add(10);
        numbers.add(20);
        numbers.add(30);

        System.out.println("Initial List: " + numbers);

        // Access
        System.out.println("First element: " + numbers.getFirst());
        System.out.println("Last element: " + numbers.getLast());
        System.out.println("At index 1: " + numbers.get(1));

        // Update
        numbers.set(1, 25);
        System.out.println("After update: " + numbers);

        // Size
        System.out.println("Size: " + numbers.size());

    }
}
