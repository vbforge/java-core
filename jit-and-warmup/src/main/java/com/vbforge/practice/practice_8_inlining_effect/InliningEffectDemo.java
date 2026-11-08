package com.vbforge.practice.practice_8_inlining_effect;

/**
 * What inlining buys you: a call to a tiny method costs about the same as writing its body by hand.
 * A callee that is too big in BYTECODE is not inlined, even if it does the same work.
 * See the decisions with: -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
 * Checksums are deterministic, timings are not.
 */
public class InliningEffectDemo {

    static volatile long sink;

    static int tinyAdd(int a, int b) {
        return a + b;
    }

    /** Same result as tinyAdd, but ~370 bytecode bytes (see practice_7), over FreqInlineSize. */
    static int bloatedAdd(int a, int b) {
        int r = a + b;
        r = (r ^ 0) + 0;   // no-op line 1
        r = (r ^ 0) + 0;   // no-op line 2
        r = (r ^ 0) + 0;   // no-op line 3
        r = (r ^ 0) + 0;   // no-op line 4
        r = (r ^ 0) + 0;   // no-op line 5
        r = (r ^ 0) + 0;   // no-op line 6
        r = (r ^ 0) + 0;   // no-op line 7
        r = (r ^ 0) + 0;   // no-op line 8
        r = (r ^ 0) + 0;   // no-op line 9
        r = (r ^ 0) + 0;   // no-op line 10
        r = (r ^ 0) + 0;   // no-op line 11
        r = (r ^ 0) + 0;   // no-op line 12
        r = (r ^ 0) + 0;   // no-op line 13
        r = (r ^ 0) + 0;   // no-op line 14
        r = (r ^ 0) + 0;   // no-op line 15
        r = (r ^ 0) + 0;   // no-op line 16
        r = (r ^ 0) + 0;   // no-op line 17
        r = (r ^ 0) + 0;   // no-op line 18
        r = (r ^ 0) + 0;   // no-op line 19
        r = (r ^ 0) + 0;   // no-op line 20
        r = (r ^ 0) + 0;   // no-op line 21
        r = (r ^ 0) + 0;   // no-op line 22
        r = (r ^ 0) + 0;   // no-op line 23
        r = (r ^ 0) + 0;   // no-op line 24
        r = (r ^ 0) + 0;   // no-op line 25
        r = (r ^ 0) + 0;   // no-op line 26
        r = (r ^ 0) + 0;   // no-op line 27
        r = (r ^ 0) + 0;   // no-op line 28
        r = (r ^ 0) + 0;   // no-op line 29
        r = (r ^ 0) + 0;   // no-op line 30
        r = (r ^ 0) + 0;   // no-op line 31
        r = (r ^ 0) + 0;   // no-op line 32
        r = (r ^ 0) + 0;   // no-op line 33
        r = (r ^ 0) + 0;   // no-op line 34
        r = (r ^ 0) + 0;   // no-op line 35
        r = (r ^ 0) + 0;   // no-op line 36
        r = (r ^ 0) + 0;   // no-op line 37
        r = (r ^ 0) + 0;   // no-op line 38
        r = (r ^ 0) + 0;   // no-op line 39
        r = (r ^ 0) + 0;   // no-op line 40
        r = (r ^ 0) + 0;   // no-op line 41
        r = (r ^ 0) + 0;   // no-op line 42
        r = (r ^ 0) + 0;   // no-op line 43
        r = (r ^ 0) + 0;   // no-op line 44
        r = (r ^ 0) + 0;   // no-op line 45
        r = (r ^ 0) + 0;   // no-op line 46
        r = (r ^ 0) + 0;   // no-op line 47
        r = (r ^ 0) + 0;   // no-op line 48
        r = (r ^ 0) + 0;   // no-op line 49
        r = (r ^ 0) + 0;   // no-op line 50
        r = (r ^ 0) + 0;   // no-op line 51
        r = (r ^ 0) + 0;   // no-op line 52
        r = (r ^ 0) + 0;   // no-op line 53
        r = (r ^ 0) + 0;   // no-op line 54
        r = (r ^ 0) + 0;   // no-op line 55
        r = (r ^ 0) + 0;   // no-op line 56
        r = (r ^ 0) + 0;   // no-op line 57
        r = (r ^ 0) + 0;   // no-op line 58
        r = (r ^ 0) + 0;   // no-op line 59
        r = (r ^ 0) + 0;   // no-op line 60
        return r;
    }

    static long viaTinyCall(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += tinyAdd(i, 1);
        }
        return s;
    }

    static long handInlined(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += i + 1;
        }
        return s;
    }

    static long viaBloatedCall(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += bloatedAdd(i, 1);
        }
        return s;
    }

    interface Variant {
        long run(int n);
    }

    static long measure(String label, Variant v, int n) {
        long result = 0;
        long ns = 0;
        for (int round = 0; round < 40; round++) {
            long t = System.nanoTime();
            result = v.run(n);
            ns = System.nanoTime() - t;
        }
        sink = result;
        System.out.println(String.format("%-16s checksum=%d  last round %d us", label, result, ns / 1_000));
        return result;
    }

    public static void main(String[] args) {
        int n = 1_000_000;
        long a = measure("tiny call", InliningEffectDemo::viaTinyCall, n);
        long b = measure("hand-inlined", InliningEffectDemo::handInlined, n);
        long c = measure("bloated call", InliningEffectDemo::viaBloatedCall, n);
        System.out.println("all three agree: " + (a == b && b == c));
    }
}
