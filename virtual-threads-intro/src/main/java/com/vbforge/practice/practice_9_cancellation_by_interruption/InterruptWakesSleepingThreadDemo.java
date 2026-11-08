package com.vbforge.practice.practice_9_cancellation_by_interruption;

/** Cancellation = interruption: a blocked virtual thread wakes up with InterruptedException, cleans up, stops. */
public class InterruptWakesSleepingThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        boolean[] cleanedUp = new boolean[1];
        boolean[] flagAfterCatch = new boolean[1];

        Thread worker = Thread.ofVirtual().name("worker").start(() -> {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                flagAfterCatch[0] = Thread.currentThread().isInterrupted(); // cleared when thrown
                cleanedUp[0] = true; // release resources here, then simply return
            }
        });

        while (worker.getState() != Thread.State.TIMED_WAITING) {
            Thread.sleep(1);
        }
        System.out.println("worker state before interrupt: " + worker.getState());

        worker.interrupt();
        worker.join();

        System.out.println("cleanup ran                   : " + cleanedUp[0]);
        System.out.println("interrupt flag inside catch   : " + flagAfterCatch[0]);
        System.out.println("worker state after join       : " + worker.getState());
    }
}
