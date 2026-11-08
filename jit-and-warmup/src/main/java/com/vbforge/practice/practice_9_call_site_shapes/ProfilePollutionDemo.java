package com.vbforge.practice.practice_9_call_site_shapes;

/**
 * Profile pollution: ONE shared loop (one call site) collects a type profile.
 * If it first sees four receiver types, the later "easy" monomorphic input is stuck with the
 * slow megamorphic code, although every element is the same class.
 *
 * Run twice and compare the last line:
 *   no argument -> pollute with four types first, then time the monomorphic input
 *   "fresh"     -> time the monomorphic input in an unpolluted JVM
 * Running benchmark A then B in one JVM has the same effect; JMH's @Fork exists for this reason.
 * Timings vary; the checksums are deterministic.
 */
public class ProfilePollutionDemo {

    static volatile long sink;

    /** The one shared call site. */
    static long run(Op[] ops, int rounds) {
        long acc = 0;
        for (int r = 0; r < rounds; r++) {
            for (Op op : ops) {
                acc = op.apply(acc);
            }
        }
        return acc;
    }

    static long timeUs(Op[] ops) {
        long ns = 0;
        for (int i = 0; i < 30; i++) {
            long t = System.nanoTime();
            sink = run(ops, 500_000);
            ns = System.nanoTime() - t;
        }
        return ns / 1_000;
    }

    public static void main(String[] args) {
        boolean fresh = args.length > 0 && "fresh".equals(args[0]);
        Op[] mono = {new Op.Add(), new Op.Add(), new Op.Add(), new Op.Add()};
        Op[] mega = {new Op.Add(), new Op.Xor(), new Op.Shift(), new Op.Mul()};

        if (!fresh) {
            System.out.println("polluting the profile with four types: " + timeUs(mega) + " us");
        }
        System.out.println("monomorphic input (" + (fresh ? "fresh JVM" : "after pollution") + "): " + timeUs(mono) + " us");
        System.out.println("checksum mono=" + run(mono, 10) + ", mega=" + run(mega, 10));
    }
}
