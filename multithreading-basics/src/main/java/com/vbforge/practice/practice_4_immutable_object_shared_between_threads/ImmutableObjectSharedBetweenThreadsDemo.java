package com.vbforge.practice.practice_4_immutable_object_shared_between_threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An immutable object needs no synchronisation: its state never changes after construction,
 * so any number of threads can read it at the same time without data races.
 */
public class ImmutableObjectSharedBetweenThreadsDemo {

    static final class ImmutableOrder {
        private final String orderId;
        private final List<String> items;

        ImmutableOrder(String orderId, List<String> items) {
            this.orderId = orderId;
            this.items = Collections.unmodifiableList(new ArrayList<>(items));
        }

        String getOrderId()     { return orderId; }
        List<String> getItems() { return items; }
    }

    public static void main(String[] args) throws InterruptedException {
        ImmutableOrder order = new ImmutableOrder("ORD-001", List.of("Keyboard", "Mouse"));

        // results are collected and printed after join, so the output order does not depend on scheduling
        ConcurrentLinkedQueue<String> seen = new ConcurrentLinkedQueue<>();
        Runnable reader = () -> seen.add(Thread.currentThread().getName() + " sees: " + order.getOrderId());

        Thread t1 = new Thread(reader, "Thread-1");
        Thread t2 = new Thread(reader, "Thread-2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        List<String> sorted = new ArrayList<>(seen);
        Collections.sort(sorted);
        sorted.forEach(System.out::println);
    }
}
