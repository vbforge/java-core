package com.vbforge.practice.condition.practice_12_condition_bounded_queue;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bounded queue with ONE lock and TWO conditions (one per predicate).
 *
 *   notFull  - producers wait here until there is room
 *   notEmpty - consumers wait here until there is an item
 *
 * Compared with wait/notifyAll, signal() wakes only the kind of thread that can make progress.
 * Waits always sit in a while loop (spurious wakeups, another thread may take the item first).
 */
public class ConditionBoundedQueueDemo {

    static class ConditionBoundedQueue<T> {
        private final Queue<T> buffer;
        private final int capacity;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        ConditionBoundedQueue(int capacity) {
            this.capacity = capacity;
            this.buffer = new ArrayDeque<>(capacity);
        }

        public void put(T item) throws InterruptedException {
            lock.lock();
            try {
                while (buffer.size() == capacity) {
                    notFull.await();   // wait until a consumer removes an item
                }
                buffer.add(item);
                notEmpty.signal();     // wake one waiting consumer
            } finally {
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (buffer.isEmpty()) {
                    notEmpty.await();  // wait until a producer adds an item
                }
                T item = buffer.poll();
                notFull.signal();      // wake one waiting producer
                return item;
            } finally {
                lock.unlock();
            }
        }

        public int size() {
            lock.lock();
            try {
                return buffer.size();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("-- Condition bounded queue (capacity=2) --");
        ConditionBoundedQueue<Integer> queue = new ConditionBoundedQueue<>(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put(i);
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    System.out.println("[Consumer] took: " + queue.take());
                    Thread.sleep(60);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("items left: " + queue.size());
    }
}
