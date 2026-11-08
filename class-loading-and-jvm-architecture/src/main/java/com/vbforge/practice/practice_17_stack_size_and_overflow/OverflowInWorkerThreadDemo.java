package com.vbforge.practice.practice_17_stack_size_and_overflow;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Each thread owns its stack, so one thread overflowing does not touch the others:
 * the worker dies with StackOverflowError, main keeps running with its own intact stack.
 */
public class OverflowInWorkerThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        AtomicReference<Throwable> died = new AtomicReference<>();

        Thread worker = new Thread(null, OverflowInWorkerThreadDemo::recurseForever, "worker", 256L * 1024);
        worker.setUncaughtExceptionHandler((thread, error) -> died.set(error));
        worker.start();
        worker.join();

        System.out.println("worker ended with StackOverflowError: " + (died.get() instanceof StackOverflowError));
        System.out.println("main thread is unaffected, sum(1..1000) = " + sum(1000));
    }

    static void recurseForever() {
        recurseForever();
    }

    static int sum(int n) {
        return n == 0 ? 0 : n + sum(n - 1);
    }
}
