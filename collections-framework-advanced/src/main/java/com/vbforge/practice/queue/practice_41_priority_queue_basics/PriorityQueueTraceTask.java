package com.vbforge.practice.queue.practice_41_priority_queue_basics;

import java.util.PriorityQueue;

/**
 * Mental-tracing puzzle: predict each print() call.
 */
public class PriorityQueueTraceTask {

    public static void main(String[] args) {

        PriorityQueue<String> pq = new PriorityQueue<>();
        pq.add("2");
        pq.add("4");
        System.out.print(pq.peek() + " "); //2
        pq.offer("1"); // {1,2,4}
        pq.add("3");   // {1,2,3,4}
        pq.remove("1");// {2,3,4}
        System.out.print(pq.poll() + " ");                      // prints 2   -> {3,4}
        if(pq.remove("2")) System.out.print(pq.poll() + " ");   // "2" is already gone: false, nothing printed
        System.out.println(pq.poll() + " " + pq.peek());        // prints "3 4" (poll -> 3, peek -> 4)
        // whole output: 2 2 3 4

    }


}
