package com.vbforge.practice.liveness.practice_20_livelock_and_backoff;

/**
 * Exponential back-off: the delay doubles with every failed attempt, up to a cap.
 * A naive base << attempt overflows for large attempt numbers (shift distance is taken modulo 64
 * and the sign bit flips), so the computation must be overflow-safe.
 */
public class BackoffDelayDemo {

    static long backoffDelayMillis(int attempt, long baseMillis, long capMillis) {
        if (attempt < 0 || baseMillis <= 0) {
            return 0;
        }
        if (attempt >= 62 || baseMillis > (capMillis >> attempt)) {
            return capMillis; // would exceed the cap (or overflow): clamp
        }
        return baseMillis << attempt;
    }

    public static void main(String[] args) {
        System.out.println("attempt -> delay (base=10ms, cap=500ms)");
        int[] attempts = {0, 1, 2, 3, 4, 5, 6, 10, 62, 63, 64, 100};
        for (int attempt : attempts) {
            System.out.println(attempt + " -> " + backoffDelayMillis(attempt, 10, 500));
        }
        System.out.println("naive 1L << 63 = " + (1L << 63) + " (negative delay: overflow)");
        System.out.println("naive 1L << 64 = " + (1L << 64) + " (shift distance wraps to 0: delay resets to base)");
    }
}
