package com.vbforge.practice.practice_1_creating_virtual_threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * builder.name(prefix, start).factory(): every newThread() gets prefix + counter.
 * Threads from a factory are unstarted; the caller starts them.
 */
public class VirtualThreadFactoryDemo {

    public static void main(String[] args) throws InterruptedException {
        ThreadFactory factory = Thread.ofVirtual().name("task-", 0).factory();

        String[] ranIn = new String[3];
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < ranIn.length; i++) {
            final int slot = i;
            threads.add(factory.newThread(() -> ranIn[slot] = Thread.currentThread().getName()));
        }

        System.out.println("state before start: " + threads.get(0).getState());
        threads.forEach(Thread::start);
        for (Thread t : threads) {
            t.join();
        }

        for (String name : ranIn) {
            System.out.println("ran in " + name);
        }
    }
}
