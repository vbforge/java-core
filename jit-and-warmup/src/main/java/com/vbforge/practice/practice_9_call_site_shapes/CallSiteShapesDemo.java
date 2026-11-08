package com.vbforge.practice.practice_9_call_site_shapes;

/**
 * Monomorphic (1 receiver type), bimorphic (2) and megamorphic (3+) call sites.
 * Each shape has its OWN loop, so each has its own call site and its own type profile.
 * Mono and bi can be inlined; mega needs real virtual dispatch. Checksums are deterministic, timings are not.
 */
public class CallSiteShapesDemo {

    static volatile long sink;

    static long monomorphic(Op[] ops, int rounds) {
        long acc = 0;
        for (int r = 0; r < rounds; r++) {
            for (Op op : ops) {
                acc = op.apply(acc);
            }
        }
        return acc;
    }

    static long bimorphic(Op[] ops, int rounds) {
        long acc = 0;
        for (int r = 0; r < rounds; r++) {
            for (Op op : ops) {
                acc = op.apply(acc);
            }
        }
        return acc;
    }

    static long megamorphic(Op[] ops, int rounds) {
        long acc = 0;
        for (int r = 0; r < rounds; r++) {
            for (Op op : ops) {
                acc = op.apply(acc);
            }
        }
        return acc;
    }

    interface Shape {
        long run(Op[] ops, int rounds);
    }

    static void bench(String label, Shape shape, Op[] ops) {
        int rounds = 2_000_000 / ops.length;
        long result = 0;
        long ns = 0;
        for (int i = 0; i < 30; i++) {
            long t = System.nanoTime();
            result = shape.run(ops, rounds);
            ns = System.nanoTime() - t;
        }
        sink = result;
        System.out.println(String.format("%-14s checksum=%d  last round %d us", label, result, ns / 1_000));
    }

    public static void main(String[] args) {
        Op[] one = {new Op.Add(), new Op.Add(), new Op.Add(), new Op.Add()};
        Op[] two = {new Op.Add(), new Op.Xor(), new Op.Add(), new Op.Xor()};
        Op[] four = {new Op.Add(), new Op.Xor(), new Op.Shift(), new Op.Mul()};

        bench("monomorphic", CallSiteShapesDemo::monomorphic, one);
        bench("bimorphic", CallSiteShapesDemo::bimorphic, two);
        bench("megamorphic", CallSiteShapesDemo::megamorphic, four);
    }
}
