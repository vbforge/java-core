package com.vbforge.practice.practice_6_volatile_flag_publishes_plain_data;

/**
 * A volatile write publishes everything written before it, even plain fields. The reader may
 * trust {@code data} only AFTER it has seen {@code ready == true}.
 *
 * <p>Reading {@code data} before checking the flag would have no edge and no guarantee.
 */
public class VolatileFlagPublishesPlainDataDemo {

    private static int[] data;                 // plain
    private static volatile boolean ready;     // the publishing edge

    public static void main(String[] args) throws InterruptedException {
        long[] sum = new long[1];

        Thread reader = new Thread(() -> {
            while (!ready) {
                Thread.yield();                // wait for the flag
            }
            long s = 0;                        // flag seen -> data is guaranteed complete
            for (int v : data) {
                s += v;
            }
            sum[0] = s;
        });
        reader.start();

        int[] local = new int[1_000];
        for (int i = 0; i < local.length; i++) {
            local[i] = i;
        }
        data = local;                          // plain write
        ready = true;                          // volatile write: publishes `data`

        reader.join();
        System.out.println("sum seen by reader: " + sum[0] + "  (expected 499500)");
    }
}
