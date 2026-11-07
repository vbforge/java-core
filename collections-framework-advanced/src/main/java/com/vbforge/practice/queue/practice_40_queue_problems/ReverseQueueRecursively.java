package com.vbforge.practice.queue.practice_40_queue_problems;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Reverse the elements of a queue using recursion — no stack.
 * */
public class ReverseQueueRecursively {
    public static void main(String[] args) {

        Queue<Integer> queue = new LinkedList<>();

        //add some elements:
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);

        System.out.println("Initial queue: " + queue);
        System.out.println("Reversed queue: " + reverseQueue(queue));

    }

    private static <T> Queue<T> reverseQueue(Queue<T> queue){
        if(queue.isEmpty()) return queue;

        //remove first:
        T first = queue.remove();

        //reverse rest recursively:
        reverseQueue(queue);

        queue.offer(first);

        return queue;
    }

}
