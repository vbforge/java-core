package com.vbforge.practice.practice_9_call_site_shapes;

/** Four implementations of one interface; the JIT cares how many of them reach ONE call site. */
public interface Op {

    long apply(long x);

    final class Add implements Op {
        public long apply(long x) {
            return x + 3;
        }
    }

    final class Xor implements Op {
        public long apply(long x) {
            return x ^ 0x5A5A;
        }
    }

    final class Shift implements Op {
        public long apply(long x) {
            return (x << 1) | 1;
        }
    }

    final class Mul implements Op {
        public long apply(long x) {
            return x * 7;
        }
    }
}
