package com.vbforge.practice.practice_3_lifecycle_and_join;

import java.util.concurrent.CountDownLatch;

/** NEW -> WAITING (parked on a latch) -> TERMINATED, observed from the outside. */
public class ThreadStatesDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch gate = new CountDownLatch(1);

        Thread thread = Thread.ofVirtual().unstarted(() -> {
            try {
                gate.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("created   : " + thread.getState());

        thread.start();
        awaitState(thread, Thread.State.WAITING);
        System.out.println("blocked   : " + thread.getState());

        gate.countDown();
        thread.join();
        System.out.println("finished  : " + thread.getState() + ", isAlive=" + thread.isAlive());
    }

    private static void awaitState(Thread thread, Thread.State expected) throws InterruptedException {
        while (thread.getState() != expected) {
            Thread.sleep(1);
        }
    }
}
