package com.vbforge.practice.practice_2_blocking_queue_family;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ArrayBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── WHAT IT IS ────────────────────────────────────────────────────────────
        // A BOUNDED FIFO queue backed by a fixed-size array.
        // • Capacity is MANDATORY at construction time.
        // • put()/take() share ONE lock (ReentrantLock) => less concurrent than
        //   LinkedBlockingQueue, but very predictable memory usage.
        // • Optional "fair" flag: true => waiting threads are served in FIFO order
        //   (safer against starvation, but slightly lower throughput).
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3, /* fair = */ true);

        Thread producer = Thread.ofPlatform().name("producer").start(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    String item = "item-" + i;
                    // put() BLOCKS while the queue is full  -> natural backpressure
                    queue.put(item);
                    System.out.printf("PUT   %-8s size=%d%n", item, queue.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = Thread.ofPlatform().name("consumer").start(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    Thread.sleep(150);                     // pretend to be slow
                    // take() BLOCKS while the queue is empty
                    String item = queue.take();
                    System.out.printf("TAKE  %-8s size=%d%n", item, queue.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.join();
        consumer.join();

        // ── NON-BLOCKING VARIANTS ────────────────────────────────────────────────
        BlockingQueue<String> full = new ArrayBlockingQueue<>(1);
        full.offer("a");
        System.out.println("offer() on full queue -> " + full.offer("b"));       // false
        System.out.println("poll()  on empty queue -> " + full.poll());         // "a"
        System.out.println("poll()  on empty queue -> " + full.poll());         // null
    }
}
