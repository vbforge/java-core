package com.vbforge.practice.practice_7_thread_locals;

/** ThreadLocal.withInitial: every thread works on its own copy; main's copy is untouched. */
public class PerThreadCopiesDemo {

    private static final ThreadLocal<Integer> COUNTER = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) throws InterruptedException {
        int[] results = new int[5];
        Thread[] threads = new Thread[results.length];

        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int k = 0; k <= index; k++) { // thread i increments i+1 times
                    COUNTER.set(COUNTER.get() + 1);
                }
                results[index] = COUNTER.get();
            });
        }
        for (Thread thread : threads) {
            thread.join();
        }

        for (int i = 0; i < results.length; i++) {
            System.out.println("thread " + i + " counter = " + results[i]);
        }
        System.out.println("main   counter = " + COUNTER.get());
    }
}
