package com.vbforge.practice.practice_3_lifecycle_and_join;

/** join() waits for the thread and guarantees that its writes are visible afterwards. */
public class JoinMakesResultVisibleDemo {

    public static void main(String[] args) throws InterruptedException {
        long[] sum = new long[1];

        Thread thread = Thread.ofVirtual().start(() -> {
            long total = 0;
            for (int i = 1; i <= 1_000; i++) {
                total += i;
            }
            sum[0] = total;
        });

        thread.join();

        System.out.println("sum of 1..1000 = " + sum[0]);
    }
}
