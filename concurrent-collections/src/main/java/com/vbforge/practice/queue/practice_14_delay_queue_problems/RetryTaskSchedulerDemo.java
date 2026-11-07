package com.vbforge.practice.queue.practice_14_delay_queue_problems;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Retry failed tasks after a delay.
 * Scenario: each failed task is put into a DelayQueue with its own delay and comes out
 * in order of expiry (here three fixed delays; a real backoff would grow the delay per attempt).
 */
public class RetryTaskSchedulerDemo {

    public static void main(String[] args) {

        DelayQueue<RetryTask> queue = new DelayQueue<>();

        queue.put(RetryTask.in("Task-1", 4000L));
        queue.put(RetryTask.in("Task-2", 2000L));
        queue.put(RetryTask.in("Task-3", 7000L));

        while (!queue.isEmpty()) {
            try {
                RetryTask taken = queue.take();
                System.out.println("Retrying: " + taken.taskName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("\nFinished processing all tasks.");
    }

    /** retryAtMillis is an absolute time; use RetryTask.in(name, delay) to build it from a delay. */
    record RetryTask(String taskName, long retryAtMillis) implements Delayed {

        static RetryTask in(String taskName, long delayMillis) {
            return new RetryTask(taskName, System.currentTimeMillis() + delayMillis);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long delay = retryAtMillis - System.currentTimeMillis();
            return unit.convert(delay, TimeUnit.MILLISECONDS);
        }
    }
}
