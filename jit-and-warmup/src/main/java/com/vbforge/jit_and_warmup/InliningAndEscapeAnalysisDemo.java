package com.vbforge.jit_and_warmup;

import java.util.HashSet;
import java.util.Set;

/**
 * The two optimisations that make "clean, small-method Java" fast:
 * <b>inlining</b> and <b>escape analysis</b>.
 *
 * <p>Run it:  {@code mvn exec:java -Dexec.mainClass="com.vbforge.jit_and_warmup.InliningAndEscapeAnalysisDemo"}
 * <p>Watch it: {@code -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining}
 */
public class InliningAndEscapeAnalysisDemo {

    /** HotSpot default {@code MaxInlineSize}: methods up to this many bytecode bytes are always inline candidates. */
    public static final int MAX_INLINE_SIZE = 35;

    /** HotSpot default {@code FreqInlineSize}: limit for methods at hot call sites. */
    public static final int FREQ_INLINE_SIZE = 325;

    /** HotSpot default {@code HugeMethodLimit}: methods larger than this are never JIT-compiled. */
    public static final int HUGE_METHOD_LIMIT = 8000;

    /** Last point stored by {@link #storeGlobally(int, int)}; reachable from anywhere, so it "escapes". */
    private static Point escaped;

    private static volatile long sink;

    public static void main(String[] args) {
        inliningBasicsDemo();
        System.out.println("---");
        inliningThresholdsDemo();
        System.out.println("---");
        callSiteShapesDemo();
        System.out.println("---");
        escapeAnalysisDemo();
        System.out.println("---");
        lockElisionDemo();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 1. Inlining basics
    // ─────────────────────────────────────────────────────────────────────

    private static int add(int a, int b) {
        return a + b;
    }

    private static int twice(int x) {
        return add(x, x);
    }

    /**
     * What it is: the JIT replaces a call with the body of the callee, so
     * {@code twice(i)} becomes {@code i + i} in machine code.
     *
     * <p>Why it matters: a call costs little, but it blocks optimisation across the
     * boundary. After inlining, the compiler sees one big block and can fold constants,
     * remove redundant loads, hoist work out of loops and eliminate allocations.
     * Inlining is the "enabler" optimisation — most others depend on it.
     * It is also why getters, setters and small helper methods are free in practice.
     *
     * <ul>
     *   <li>Effect on result: none — inlining never changes behaviour, only speed</li>
     *   <li>Cost: bigger machine code (instruction cache pressure) if overdone</li>
     * </ul>
     *
     * <p>Common gotcha: do not hand-inline "for performance". The two versions below
     * return the same result, and after C2 they usually run the same machine code.
     */
    public static void inliningBasicsDemo() {
        int n = 1_000_000;
        long viaMethods = sumWithSmallMethods(n);
        long byHand = sumInlinedByHand(n);
        sink = viaMethods + byHand;
        System.out.println("small methods: " + viaMethods + ", hand-inlined: " + byHand
                + ", equal: " + (viaMethods == byHand));
    }

    /** Sums {@code twice(i)} for i in 0..n-1 using tiny helper methods. */
    public static long sumWithSmallMethods(int n) {
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += twice(i);
        }
        return sum;
    }

    /** Same result as {@link #sumWithSmallMethods(int)}, written with the calls expanded by hand. */
    public static long sumInlinedByHand(int n) {
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += i + i;
        }
        return sum;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. Inlining thresholds
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: the size limits that decide whether a callee may be inlined.
     *
     * <p>Why it matters: the limits are measured in <b>bytecode bytes</b>, not source
     * lines. A method with a few short statements can already be over 35 bytes.
     * Check real sizes with {@code javap -c}.
     *
     * <ul>
     *   <li>≤ {@link #MAX_INLINE_SIZE} (35) bytes: inlined if the call site has been executed at all</li>
     *   <li>≤ {@link #FREQ_INLINE_SIZE} (325) bytes: inlined only if the call site is hot</li>
     *   <li>larger: not inlined; the call stays a real call</li>
     *   <li>≥ {@link #HUGE_METHOD_LIMIT} (8000) bytes: the method is never compiled at all and
     *       stays in the interpreter</li>
     * </ul>
     *
     * <p>Common gotcha: one giant "do everything" method can be slower than the same logic
     * split into small methods, because the giant one may exceed the limits above.
     * (These are HotSpot defaults on JDK 8–21; they can be changed with
     * {@code -XX:MaxInlineSize} and {@code -XX:FreqInlineSize}.)
     */
    public static void inliningThresholdsDemo() {
        System.out.println("always a candidate up to: " + MAX_INLINE_SIZE + " bytecode bytes");
        System.out.println("candidate at hot sites up to: " + FREQ_INLINE_SIZE + " bytecode bytes");
        System.out.println("never compiled above: " + HUGE_METHOD_LIMIT + " bytecode bytes");
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. Monomorphic, bimorphic and megamorphic call sites
    // ─────────────────────────────────────────────────────────────────────

    /** A tiny interface with several implementations, to create polymorphic call sites. */
    public interface Op {
        int apply(int x);
    }

    private static final class Inc implements Op {
        @Override
        public int apply(int x) {
            return x + 1;
        }
    }

    private static final class Dbl implements Op {
        @Override
        public int apply(int x) {
            return x * 2;
        }
    }

    private static final class Neg implements Op {
        @Override
        public int apply(int x) {
            return -x;
        }
    }

    private static final class Sq implements Op {
        @Override
        public int apply(int x) {
            return x * x;
        }
    }

    /**
     * What it is: an interface call {@code op.apply(i)} is a <b>virtual call</b>.
     * While profiling (tier 3), HotSpot records which concrete classes arrive at each call site.
     *
     * <ul>
     *   <li><b>Monomorphic</b> (1 type seen): the JIT inlines that one implementation behind a cheap
     *       type check — as fast as a direct call</li>
     *   <li><b>Bimorphic</b> (2 types): inlines both behind two type checks</li>
     *   <li><b>Megamorphic</b> (3+ types): gives up and emits a real virtual dispatch — no inlining,
     *       so no optimisation across the call</li>
     * </ul>
     *
     * <p>Why it matters: it is the reason an interface with one implementation in practice
     * costs nothing, and the reason a hot loop over a mixed-type collection is slower.
     * The JIT also uses class-hierarchy analysis: if only one implementation is <i>loaded</i>
     * it may inline without a type check — and if a second one is loaded later, it
     * <b>deoptimises</b> (throws the compiled code away and falls back to the interpreter).
     *
     * <p>Common gotcha: <i>profile pollution</i>. The profile belongs to the method, not
     * to your test. {@link #applyAll(Op[])} is shared below, so once it has seen four types it
     * stays megamorphic for every later caller — which is why JMH runs each benchmark in a
     * fresh forked JVM. Timings are printed for illustration only.
     */
    public static void callSiteShapesDemo() {
        int size = 1_000_000;
        Op[] mono = monomorphicOps(size);
        Op[] bi = bimorphicOps(size);
        Op[] mega = megamorphicOps(size);
        for (int round = 0; round < 3; round++) {
            long t0 = System.nanoTime();
            long a = applyAll(mono);
            long t1 = System.nanoTime();
            long b = applyAll(bi);
            long t2 = System.nanoTime();
            long c = applyAll(mega);
            long t3 = System.nanoTime();
            sink = a + b + c;
            System.out.println("round " + round + ": mono " + (t1 - t0) / 1_000 + " us, bi "
                    + (t2 - t1) / 1_000 + " us, mega " + (t3 - t2) / 1_000 + " us");
        }
        System.out.println("distinct types: " + distinctTypes(mono) + ", " + distinctTypes(bi)
                + ", " + distinctTypes(mega));
    }

    /** Sums {@code ops[i].apply(i)} over the whole array — one shared virtual call site. */
    public static long applyAll(Op[] ops) {
        long acc = 0;
        for (int i = 0; i < ops.length; i++) {
            acc += ops[i].apply(i);
        }
        return acc;
    }

    /** @return {@code n} ops, all of the same class (monomorphic) */
    public static Op[] monomorphicOps(int n) {
        Op[] ops = new Op[n];
        for (int i = 0; i < n; i++) {
            ops[i] = new Inc();
        }
        return ops;
    }

    /** @return {@code n} ops alternating between two classes (bimorphic) */
    public static Op[] bimorphicOps(int n) {
        Op[] ops = new Op[n];
        for (int i = 0; i < n; i++) {
            ops[i] = (i % 2 == 0) ? new Inc() : new Dbl();
        }
        return ops;
    }

    /** @return {@code n} ops cycling through four classes (megamorphic) */
    public static Op[] megamorphicOps(int n) {
        Op[] ops = new Op[n];
        for (int i = 0; i < n; i++) {
            switch (i % 4) {
                case 0:
                    ops[i] = new Inc();
                    break;
                case 1:
                    ops[i] = new Dbl();
                    break;
                case 2:
                    ops[i] = new Neg();
                    break;
                default:
                    ops[i] = new Sq();
                    break;
            }
        }
        return ops;
    }

    /** @return how many different concrete classes appear in the array */
    public static int distinctTypes(Op[] ops) {
        Set<Class<?>> types = new HashSet<>();
        for (Op op : ops) {
            types.add(op.getClass());
        }
        return types.size();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. Escape analysis and scalar replacement
    // ─────────────────────────────────────────────────────────────────────

    /** A small immutable value object — the kind of thing Java code allocates constantly. */
    public static final class Point {
        public final int x;
        public final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point minus(Point other) {
            return new Point(x - other.x, y - other.y);
        }

        public long lengthSquared() {
            return (long) x * x + (long) y * y;
        }
    }

    /**
     * What it is: after inlining, C2 asks of every {@code new}: can this object be
     * seen by anything outside the compiled code? Three answers:
     *
     * <ul>
     *   <li><b>NoEscape</b> – never leaves the method: the allocation can be removed and the fields
     *       kept in registers (<i>scalar replacement</i>)</li>
     *   <li><b>ArgEscape</b> – passed to a method that was not inlined: it must really exist</li>
     *   <li><b>GlobalEscape</b> – stored in a field/static or returned: it must really exist</li>
     * </ul>
     *
     * <p>Why it matters: it is why short-lived value objects (points, small records,
     * boxed numbers, iterators) are often free in hot code — the heap allocation and the
     * GC work disappear.
     *
     * <ul>
     *   <li>Runs only in <b>C2</b> — never in the interpreter or C1</li>
     *   <li>Depends on inlining: if {@code minus} is not inlined, the Points are ArgEscape</li>
     *   <li>Can be switched off with {@code -XX:-DoEscapeAnalysis} to compare</li>
     * </ul>
     *
     * <p>Common gotcha: escape analysis is conservative and invisible. Do not design code
     * that <i>needs</i> it. Treat it as a bonus, and measure.
     */
    public static void escapeAnalysisDemo() {
        long allocating = sumDistances(1_000_000);
        long byHand = 0;
        for (int i = 0; i < 1_000_000; i++) {
            byHand += distanceSquaredByHand(i, i + 1, 0, 0);
        }
        sink = allocating + byHand;
        System.out.println("allocating: " + allocating + ", by hand: " + byHand + ", equal: " + (allocating == byHand));
        Point p = storeGlobally(3, 4);
        System.out.println("escaped point (3,4) length^2 = " + p.lengthSquared()
                + ", same object kept globally: " + (p == escapedPoint()));
    }

    /** Squared distance between two points, written with two temporary {@link Point}s. */
    public static long distanceSquaredAllocating(int x1, int y1, int x2, int y2) {
        return new Point(x1, y1).minus(new Point(x2, y2)).lengthSquared();
    }

    /** Same result with no objects at all — what scalar replacement turns the version above into. */
    public static long distanceSquaredByHand(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return (long) dx * dx + (long) dy * dy;
    }

    /** Sums the squared distance from (i, i+1) to the origin for i in 0..n-1, allocating Points. */
    public static long sumDistances(int n) {
        long acc = 0;
        for (int i = 0; i < n; i++) {
            acc += distanceSquaredAllocating(i, i + 1, 0, 0);
        }
        return acc;
    }

    /** Creates a Point and stores it in a static field: it <b>globally escapes</b>, so it can never be scalar-replaced. */
    public static Point storeGlobally(int x, int y) {
        Point p = new Point(x, y);
        escaped = p;
        return p;
    }

    /** @return the last point stored by {@link #storeGlobally(int, int)}, or null */
    public static Point escapedPoint() {
        return escaped;
    }

    // ─────────────────────────────────────────────────────────────────────
    // 5. Lock elision
    // ─────────────────────────────────────────────────────────────────────

    /**
     * What it is: the same analysis lets C2 remove {@code synchronized} when the lock
     * object never escapes the thread — nobody else can ever contend for it.
     *
     * <p>Why it matters: {@code StringBuffer} has synchronized methods. Used as a local
     * variable (below), the locks are proven useless and removed.
     *
     * <p>Common gotcha: this is not a reason to keep using {@code StringBuffer}. In the
     * interpreter and in C1 the locks are still taken, and the code only gets fast after
     * warm-up. Use {@code StringBuilder} when you do not need thread safety.
     */
    public static void lockElisionDemo() {
        String[] parts = {"interpreter", "C1", "C2"};
        System.out.println(joinWithLocalBuffer(parts));
    }

    /** Joins the parts with commas using a method-local {@code StringBuffer}. */
    public static String joinWithLocalBuffer(String[] parts) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(parts[i]);
        }
        return buffer.toString();
    }
}
