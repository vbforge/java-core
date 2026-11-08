package com.vbforge.practice.practice_6_limiting_concurrency_with_semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/** tryAcquire(): give up instead of waiting when no permit is free. */
public class TryAcquireDemo {

    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire(); // main holds the only permit

        boolean[] results = new boolean[3];
        Thread other = Thread.ofVirtual().start(() -> {
            results[0] = semaphore.tryAcquire();
            try {
                results[1] = semaphore.tryAcquire(50, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        other.join();
        System.out.println("tryAcquire()            while held : " + results[0]);
        System.out.println("tryAcquire(50 ms)       while held : " + results[1]);

        semaphore.release();
        Thread again = Thread.ofVirtual().start(() -> results[2] = semaphore.tryAcquire());
        again.join();
        System.out.println("tryAcquire()            after release: " + results[2]);
    }
}
