package com.vbforge.practice.queue.practice_11_array_blocking_queue_problems;

import java.util.concurrent.ArrayBlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Logging service with a blocking queue.
 * Goal: asynchronously log messages using a dedicated logging thread.
 *
 * Shutdown: a volatile flag plus poll(timeout), so the logger can never block forever in take().
 * It stops when the flag is off AND the queue is drained.
 */
public class AsyncLoggerDemo {

    private static final int MAX_QUEUE_SIZE = 10;
    private static final ArrayBlockingQueue<String> logQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);
    private static volatile boolean isRunning = true;
    private static Thread loggerThread;

    public static void main(String[] args) throws InterruptedException {

        startLoggerThread();
        log("App started");
        log("Processing...");
        log("App finished");
        shutdownProcess();
        // Wait for the logger thread to complete
        loggerThread.join();
    }

    private static void startLoggerThread() {

        loggerThread = new Thread(() -> {
            try {
                while (isRunning || !logQueue.isEmpty()) {
                    String message = logQueue.poll(1, SECONDS);
                    if (message != null) {
                        System.out.println("LOG: " + message);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Don't set it as a daemon if you want to be sure the logs are processed
        // loggerThread.setDaemon(true);
        loggerThread.start();
    }

    public static void log(String message) {
        try {
            logQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void shutdownProcess() {
        isRunning = false;
    }
}
