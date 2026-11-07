package com.vbforge.practice.queue.practice_11_array_blocking_queue_problems;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Bounded task queue: reject new tasks when the queue is full
 * (simulates the work queue of a thread pool). offer() returns false instead of waiting.
 */
public class RejectWhenFullTaskQueueDemo {

    private static final int MAX_QUEUE_SIZE = 3;
    private static final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);

    public static void main(String[] args) {

        for (int i = 1; i <= 5; i++) {
            int id = i;
            submitTask(id, () -> System.out.println("Task executed: task-" + id));
        }

        while (!queue.isEmpty()) {
            queue.poll().run();
        }
    }

    private static void submitTask(int id, Runnable task) {
        if (!queue.offer(task)) {
            System.out.println("Queue is full. Task rejected: task-" + id);
        }
    }
}
