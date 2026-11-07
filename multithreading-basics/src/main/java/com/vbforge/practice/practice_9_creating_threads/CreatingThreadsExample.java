package com.vbforge.practice.practice_9_creating_threads;

import java.util.concurrent.TimeUnit;

public class CreatingThreadsExample {

    public static void main(String[] args) {
        // Create and start Thread-1
        Thread thread1 = createThread(
            "Thread-1",
            () -> System.out.println("Logic of " + Thread.currentThread().getName() + " is printed")
        );
        thread1.start();

        // Print the main thread's name
        System.out.println(Thread.currentThread().getName() + " is started");

        // Create and start Thread-2 (sleeps for 5 seconds)
        Thread thread2 = createThread(
            "Thread-2",
            () -> {
                System.out.println("Thread started... " + Thread.currentThread().getName());
                sleepSilently(5_000L);
                System.out.println("Thread finished! " + Thread.currentThread().getName());
            }
        );
        thread2.start();

        // Create and start Thread-3 (sleeps for 10 seconds)
        Thread thread3 = createThread(
            "Thread-3",
            () -> {
                System.out.println("Thread started... " + Thread.currentThread().getName());
                sleepSilently(10_000L);
                System.out.println("Thread finished! " + Thread.currentThread().getName());
            }
        );
        thread3.start();

        // Wait for all threads to finish
        waitForThreads(thread1, thread2, thread3);

        System.out.println("Main finished!");
    }

    /**
     * Helper method to create a thread with a name and a Runnable task.
     */
    private static Thread createThread(String name, Runnable task) {
        Thread thread = new Thread(task);
        thread.setName(name);
        return thread;
    }

    /**
     * Helper method to sleep for a specified time without throwing InterruptedException.
     */
    private static void sleepSilently(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Helper method to wait for multiple threads to finish.
     */
    private static void waitForThreads(Thread... threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
