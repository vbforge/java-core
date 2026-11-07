package com.vbforge.practice.queue.practice_15_priority_blocking_queue;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Job scheduler with priorities.
 * Scenario: higher-priority jobs (lower number) must be processed first.
 * Jobs with EQUAL priority (jobs 2 and 4 here) come out in an unspecified relative order.
 */
public class JobSchedulerDemo {

    public static void main(String[] args) {

        PriorityBlockingQueue<Job> queue = new PriorityBlockingQueue<>();

        queue.offer(new Job(1, 5));
        queue.offer(new Job(2, 1));
        queue.offer(new Job(3, 3));
        queue.offer(new Job(4, 1));
        queue.offer(new Job(5, 0));

        while (!queue.isEmpty()) {
            try {
                System.out.println("\nProcessing: " + queue.peek() + "; Completed : " + queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // model class
    record Job(int id, int priority) implements Comparable<Job> {

        @Override
        public int compareTo(Job other) {
            return Integer.compare(this.priority, other.priority); // lower number = higher priority
        }

        @Override
        public String toString() {
            return String.format("Job: %d (priority: %d)", id, priority);
        }
    }
}
