package com.vbforge.practice.practice_7_happens_before_transitivity;

/**
 * Happens-before is transitive ("piggybacking"). A writes {@code a}, then raises flag1.
 * B sees flag1, writes {@code b}, then raises flag2. C sees flag2 and is guaranteed to see
 * BOTH {@code a} and {@code b}, although it never touched flag1 itself.
 */
public class RelayThroughThreeThreadsDemo {

    private static int a;                        // plain
    private static int b;                        // plain
    private static volatile boolean flag1;
    private static volatile boolean flag2;

    public static void main(String[] args) throws InterruptedException {
        int[] seenByC = new int[2];

        Thread threadC = new Thread(() -> {
            while (!flag2) {
                Thread.yield();
            }
            seenByC[0] = a;                      // a: A -> flag1 -> B -> flag2 -> C
            seenByC[1] = b;
        }, "C");

        Thread threadB = new Thread(() -> {
            while (!flag1) {
                Thread.yield();
            }
            b = a + 1;                           // plain write, after seeing a
            flag2 = true;
        }, "B");

        Thread threadA = new Thread(() -> {
            a = 10;                              // plain write
            flag1 = true;
        }, "A");

        threadC.start();
        threadB.start();
        threadA.start();
        threadA.join();
        threadB.join();
        threadC.join();

        System.out.println("C saw a = " + seenByC[0] + ", b = " + seenByC[1] + "  (expected 10 and 11)");
    }
}
