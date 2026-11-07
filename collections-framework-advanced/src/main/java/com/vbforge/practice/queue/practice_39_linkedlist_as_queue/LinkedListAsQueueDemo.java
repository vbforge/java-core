package com.vbforge.practice.queue.practice_39_linkedlist_as_queue;

import java.util.LinkedList;
import java.util.Queue;

public class LinkedListAsQueueDemo {
    public static void main(String[] args) {

        //using LinkedList as Queue
        Queue<String> queue = new LinkedList<>();

        //add elements (FIFO)
        queue.offer("Alex");
        queue.offer("Anna");
        queue.offer("Fred");

        System.out.println("Initial queue: " + queue);

        //access the head of the queue:
        System.out.println("Head of queue (peek): " + queue.peek());

        //remove the head of the queue:
        String removed = queue.poll();
        System.out.println("Removed element: " + removed);
        System.out.println("Queue after removing: " + queue);

        //iterating through queue:
        System.out.println("Iterating through queue: ");
        for (String item : queue) {
            System.out.println(item);
        }

        //remove specific element:
        queue.remove("Anna");
        System.out.println("Queue after removing specific element 'Anna': " + queue);

        //check if it`s empty:
        System.out.println("Is queue empty? " + queue.isEmpty());

    }
}
