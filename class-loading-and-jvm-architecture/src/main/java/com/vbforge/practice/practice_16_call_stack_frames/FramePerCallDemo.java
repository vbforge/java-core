package com.vbforge.practice.practice_16_call_stack_frames;

/**
 * Recursion = one more frame per call. The stack depth grows by exactly one for each level.
 */
public class FramePerCallDemo {

    private static final int[] DEPTHS = new int[4];

    public static void main(String[] args) {
        descend(0);
        for (int level = 1; level < DEPTHS.length; level++) {
            System.out.println("level " + (level - 1) + " -> " + level + ": frames added = "
                    + (DEPTHS[level] - DEPTHS[level - 1]));
        }
    }

    static void descend(int level) {
        DEPTHS[level] = new Throwable().getStackTrace().length;
        if (level + 1 < DEPTHS.length) {
            descend(level + 1);
        }
    }
}
