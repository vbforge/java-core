package com.vbforge.practice.practice_6_limiting_concurrency_with_semaphore;

import java.util.concurrent.Semaphore;

/** release() belongs in finally: a permit leaked by a failing task is gone for good. */
public class PermitReleasedInFinallyDemo {

    public static void main(String[] args) throws InterruptedException {
        Semaphore safe = new Semaphore(3);
        runOnVirtualThread(() -> callWithFinally(safe));
        System.out.println("release in finally  : permits left = " + safe.availablePermits());

        Semaphore leaky = new Semaphore(3);
        runOnVirtualThread(() -> callWithoutFinally(leaky));
        System.out.println("release after call  : permits left = " + leaky.availablePermits() + "  (one leaked)");
    }

    private static void callWithFinally(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            failingCall();
        } finally {
            semaphore.release();
        }
    }

    private static void callWithoutFinally(Semaphore semaphore) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        failingCall();
        semaphore.release(); // never reached
    }

    private static void failingCall() {
        throw new IllegalStateException("remote call failed");
    }

    private static void runOnVirtualThread(Runnable action) throws InterruptedException {
        Thread thread = Thread.ofVirtual().start(() -> {
            try {
                action.run();
            } catch (IllegalStateException expected) {
                // swallow here only to keep the demo output clean
            }
        });
        thread.join();
    }
}
