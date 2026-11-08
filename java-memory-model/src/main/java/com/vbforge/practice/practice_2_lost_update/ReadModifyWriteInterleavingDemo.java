package com.vbforge.practice.practice_2_lost_update;

/**
 * {@code count++} is three steps: read, add, write. This demo replays ONE unlucky interleaving
 * by hand, in a single thread, so the result is the same on every run.
 */
public class ReadModifyWriteInterleavingDemo {

    public static void main(String[] args) {
        // Serial order: A runs completely, then B runs completely.
        int serial = 5;
        serial = serial + 1;   // A
        serial = serial + 1;   // B
        System.out.println("serial order:      " + serial + "  (expected 7)");

        // Interleaved: both threads read before either one writes.
        int shared = 5;
        int aRead = shared;    // A: read 5
        int bRead = shared;    // B: read 5 (A has not written yet)
        shared = aRead + 1;    // A: write 6
        shared = bRead + 1;    // B: write 6, A's update is lost
        System.out.println("interleaved order: " + shared + "  (one update lost)");
    }
}
