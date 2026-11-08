package com.vbforge.practice.practice_9_cancellation_by_interruption;

/** Swallowing InterruptedException: the task can never be cancelled by interrupt(). We need a side-channel flag to stop it. */
public class SwallowedInterruptDemo {

    private static volatile boolean stop;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = Thread.ofVirtual().start(() -> {
            while (!stop) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                    // swallowed: the request to stop is lost
                }
            }
        });

        Thread.sleep(50);
        worker.interrupt();
        Thread.sleep(150); // plenty of time for a well-behaved thread to react

        System.out.println("still alive after interrupt(): " + worker.isAlive());

        stop = true; // only the extra flag ends it
        worker.join();
        System.out.println("alive after the flag + join   : " + worker.isAlive());
    }
}
