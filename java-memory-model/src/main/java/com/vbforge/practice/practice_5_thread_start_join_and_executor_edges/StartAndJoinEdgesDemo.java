package com.vbforge.practice.practice_5_thread_start_join_and_executor_edges;

/**
 * Two happens-before edges you use every day, with plain (non-volatile) fields:
 * <ul>
 *   <li>everything before {@code Thread.start()} is visible to the started thread;</li>
 *   <li>everything a thread did is visible to whoever returns from {@code join()} on it.</li>
 * </ul>
 */
public class StartAndJoinEdgesDemo {

    private static int input;     // plain
    private static int[] output;  // plain reference and plain elements

    public static void main(String[] args) throws InterruptedException {
        input = 7;                               // written BEFORE start()

        Thread worker = new Thread(() -> {
            int local = input;                   // guaranteed to see 7 (start edge)
            output = new int[]{local, local * local};
        });
        worker.start();
        worker.join();                           // join edge: the worker's writes are visible now

        System.out.println("input seen by worker: " + output[0]);
        System.out.println("square from worker:   " + output[1]);
    }
}
