package com.vbforge.practice.practice_16_labeled_break_continue;

/**
 * Labeled continue / break in nested for loops.
 *
 * Without a label, break and continue only ever target the innermost loop they appear in.
 * "continue inner" from the k-loop restarts the middle loop (running its update i++);
 * "continue outer" and "break outer" act on the infinite outer loop.
 */
public class LabeledForDemo {

    public static void main(String[] args) {
        int i = 0;

        outer:
        for (; ; ) {                        // infinite outer loop
            inner:
            for (; i < 10; i++) {
                System.out.println("i = " + i);

                if (i == 2) { System.out.println("  continue"); continue; }
                if (i == 3) { System.out.println("  break");    i++; break; }
                if (i == 7) { System.out.println("  continue outer"); i++; continue outer; }
                if (i == 8) { System.out.println("  break outer"); break outer; }

                for (int k = 0; k < 5; k++) {
                    if (k == 3) { System.out.println("  continue inner"); continue inner; }
                }
            }
        }
    }
}
