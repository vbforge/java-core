package com.vbforge.practice.practice_2_blocking_queue_family;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayQueueDemo {

    // ── WHAT IT IS ────────────────────────────────────────────────────────────────
    // An UNBOUNDED queue whose elements become available only after their delay
    // has expired.
    // • Elements MUST implement java.util.concurrent.Delayed
    // • take() blocks until the HEAD's delay elapses -> perfect for schedulers,
    //   retry-with-backoff, session timeouts, cache eviction...
    // • put()/add() NEVER block (the queue is unbounded).
    record ScheduledTask(String name, long executeAtNanos) implements Delayed {

        ScheduledTask(String name, long delay, TimeUnit unit) {
            this(name, System.nanoTime() + unit.toNanos(delay));
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(executeAtNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            // Ordering by absolute time => soonest task first
            return Long.compare(this.executeAtNanos, ((ScheduledTask) other).executeAtNanos);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        DelayQueue<ScheduledTask> queue = new DelayQueue<>();
        queue.put(new ScheduledTask("A", 1500, TimeUnit.MILLISECONDS));
        queue.put(new ScheduledTask("B",  500, TimeUnit.MILLISECONDS));
        queue.put(new ScheduledTask("C", 1000, TimeUnit.MILLISECONDS));

        // peek() returns the head even before it is due; poll() returns null until a delay has expired
        System.out.println("poll() before any delay expired: " + queue.poll());
        // (peek() on a DelayQueue only returns the head once it has expired)

        long start = System.nanoTime();
        while (!queue.isEmpty()) {
            ScheduledTask task = queue.take();  // blocks until task is due
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            // rounded to 100 ms so the output is stable between runs
            System.out.printf("~%4d ms -> %s%n", Math.round(elapsedMs / 100.0) * 100, task.name());
        }
        // Expected output order: B (500 ms), C (1000 ms), A (1500 ms)
    }
}
