package com.vbforge.practice.practice_3_lifecycle_and_join;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/** join(Duration) returns false when the thread is still running, true once it has finished. */
public class JoinWithTimeoutDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch gate = new CountDownLatch(1);

        Thread thread = Thread.ofVirtual().start(() -> {
            try {
                gate.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        boolean finished = thread.join(Duration.ofMillis(50));
        System.out.println("join after 50 ms, thread blocked : " + finished);

        gate.countDown();
        finished = thread.join(Duration.ofSeconds(5));
        System.out.println("join after release               : " + finished);
    }
}
