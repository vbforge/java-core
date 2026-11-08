package com.vbforge.practice.liveness.practice_20_livelock_and_backoff;

/**
 * Livelock, simulated round by round in ONE thread so the output is deterministic.
 *
 * Two walkers meet in a corridor. A round succeeds only if exactly one of them tries to pass.
 * If both try in the same round they collide, both back off and retry.
 *   - identical back-off: they always retry in the same round again -> busy, but no progress (livelock)
 *   - different exponential back-off (stand-in for random jitter): the symmetry breaks and one passes
 * A livelock burns CPU like a working program, and ThreadMXBean cannot see it.
 */
public class LivelockSimulationDemo {

    /** @return the round in which someone got through, or -1 if nobody did within maxRounds */
    static int simulate(long baseA, long baseB, int maxRounds) {
        int attemptsA = 0;
        int attemptsB = 0;
        long nextTryA = 0;
        long nextTryB = 0;

        for (int round = 0; round < maxRounds; round++) {
            boolean triesA = round >= nextTryA;
            boolean triesB = round >= nextTryB;

            if (triesA && !triesB) {
                return round;
            }
            if (triesB && !triesA) {
                return round;
            }
            if (triesA) { // both tried: collision, both step aside and back off
                nextTryA = round + 1 + (baseA << Math.min(attemptsA++, 20));
                nextTryB = round + 1 + (baseB << Math.min(attemptsB++, 20));
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int rounds = 50;
        int same = simulate(1, 1, rounds);
        System.out.println("identical back-off, rounds=" + rounds + " -> "
                + (same < 0 ? "nobody passed: LIVELOCK" : "passed in round " + same));

        int different = simulate(1, 3, rounds);
        System.out.println("different back-off (1 vs 3) -> "
                + (different < 0 ? "nobody passed" : "passed in round " + different));
    }
}
