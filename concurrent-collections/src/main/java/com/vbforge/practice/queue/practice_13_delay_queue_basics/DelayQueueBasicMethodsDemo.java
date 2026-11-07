package com.vbforge.practice.queue.practice_13_delay_queue_basics;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueue is an unbounded blocking queue of elements that implement Delayed.
 *   - an element can only be taken when its delay has expired (take() waits, poll() returns null until then)
 *   - internally a priority queue ordered by the remaining delay (head = the one that expires first)
 *   - useful for task scheduling, retry mechanisms, timeouts
 *   - the element's getDelay() decides the timing; this class uses System.currentTimeMillis()
 *     (System.nanoTime() is the safer clock because it is not affected by wall-clock changes)
 */
public class DelayQueueBasicMethodsDemo {

    public static void main(String[] args) {

        // the element class must implement Delayed (see DelayedTask below)
        DelayQueue<DelayedTask> delayQueue = new DelayQueue<>();

        delayQueue.put(new DelayedTask("Task-1", 3000L));
        delayQueue.put(new DelayedTask("Task-2", 1000L));
        delayQueue.put(new DelayedTask("Task-3", 2000L));

        // nothing has expired yet: poll() does not wait and returns null
        System.out.println("poll right away: " + delayQueue.poll());

        // for-each iteration does not respect delay ordering and ignores expiry:
        // use take() or poll() to get the next element
        // delayQueue.remove(task);  // removes a specific task
        // delayQueue.clear();       // removes all tasks

        while (!delayQueue.isEmpty()) {
            try {
                // retrieves and removes the head, waiting until an element with an expired delay is available
                System.out.println("Processed: " + delayQueue.take()); // blocks until the delay expires
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("\nFinished processing all tasks.");
    }

    static class DelayedTask implements Delayed {

        private final String name;
        private final long expiresAtMillis; // absolute time (epoch millis) when the task becomes available

        DelayedTask(String name, long delayInMillis) {
            this.name = name;
            this.expiresAtMillis = System.currentTimeMillis() + delayInMillis;
        }

        // need to override these two methods:

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = expiresAtMillis - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
