package com.vbforge.practice.practice_9_cancellation_by_interruption;

/** Catching InterruptedException without rethrowing: restore the flag with interrupt() and stop. */
public class RestoreInterruptFlagDemo {

    public static void main(String[] args) throws InterruptedException {
        boolean[] flagAfterRestore = new boolean[1];

        Thread worker = Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore: callers up the stack can still see it
                flagAfterRestore[0] = Thread.currentThread().isInterrupted();
            }
        });

        while (worker.getState() != Thread.State.TIMED_WAITING) {
            Thread.sleep(1);
        }
        worker.interrupt();
        worker.join();

        System.out.println("flag restored inside the thread: " + flagAfterRestore[0]);
    }
}
