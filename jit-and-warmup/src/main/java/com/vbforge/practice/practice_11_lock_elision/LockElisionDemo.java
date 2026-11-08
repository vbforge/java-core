package com.vbforge.practice.practice_11_lock_elision;

/**
 * Lock elision: synchronized code on an object that never escapes the method is lock-free after C2.
 * Compared: StringBuffer (synchronized) vs StringBuilder (not), both as local variables,
 * plus an explicit synchronized block on a throw-away lock object.
 * Try -XX:-EliminateLocks to switch the optimisation off. Timings vary, lengths are deterministic.
 */
public class LockElisionDemo {

    static volatile long sink;

    static long withStringBuffer(int n) {
        long len = 0;
        for (int i = 0; i < n; i++) {
            StringBuffer sb = new StringBuffer();
            sb.append("a").append(i).append('b');
            len += sb.length();
        }
        return len;
    }

    static long withStringBuilder(int n) {
        long len = 0;
        for (int i = 0; i < n; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("a").append(i).append('b');
            len += sb.length();
        }
        return len;
    }

    static long withThrowAwayLock(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            Object lock = new Object();
            synchronized (lock) {
                s += i & 7;
            }
        }
        return s;
    }

    interface Work {
        long run(int n);
    }

    static void bench(String label, Work work) {
        long result = 0;
        long ns = 0;
        for (int round = 0; round < 40; round++) {
            long t = System.nanoTime();
            result = work.run(200_000);
            ns = System.nanoTime() - t;
        }
        sink = result;
        System.out.println(String.format("%-18s result=%d  last round %d us", label, result, ns / 1_000));
    }

    public static void main(String[] args) {
        bench("StringBuffer", LockElisionDemo::withStringBuffer);
        bench("StringBuilder", LockElisionDemo::withStringBuilder);
        bench("throw-away lock", LockElisionDemo::withThrowAwayLock);
    }
}
