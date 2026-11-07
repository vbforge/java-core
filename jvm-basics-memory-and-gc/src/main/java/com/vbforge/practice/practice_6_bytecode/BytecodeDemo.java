package com.vbforge.practice.practice_6_bytecode;

/**
 * Java Bytecode — the intermediate language the JVM executes.
 *
 * When you compile Java source code, javac produces .class files
 * containing bytecode — a compact, stack-based instruction set that
 * is platform-independent.
 *
 * The JVM then:
 *   1. Interprets bytecode (slow path, JVM startup)
 *   2. Profiles hot spots (methods called frequently)
 *   3. JIT-compiles hot spots to native machine code (fast path)
 *
 * Bytecode is stack-based (not register-based like x86):
 *   - All operations work on an operand stack
 *   - Instructions push values, pop operands, push results
 *
 * Example — compiling `int c = a + b;`:
 *
 *   Java source:   int c = a + b;
 *   Bytecode:
 *     iload_1     // push local variable 'a' (slot 1) onto operand stack
 *     iload_2     // push local variable 'b' (slot 2)
 *     iadd        // pop two ints, push their sum
 *     istore_3    // pop sum, store into local variable 'c' (slot 3)
 *
 * Key bytecode instruction prefixes (type-encoded in the opcode):
 *   i → int        (iload, istore, iadd, isub, imul, idiv, ireturn)
 *   l → long       (lload, ladd, ...)
 *   f → float      (fload, fadd, ...)
 *   d → double     (dload, dadd, ...)
 *   a → reference  (aload, astore, areturn, new, checkcast, instanceof)
 *   b → byte       (baload, bastore)
 *   c → char       (caload, castore)
 *   s → short      (saload, sastore)
 *
 * To inspect bytecode of any class:
 *   javap -c -verbose com.vbforge._13_jvm_internals.bytecode.BytecodeDemo
 *
 * Tools for bytecode manipulation:
 *   ASM       — low-level, fastest (used internally by Spring, Hibernate)
 *   Javassist — source-level API on top of bytecode
 *   ByteBuddy — fluent API, very developer-friendly (used by Mockito, JaCoCo)
 *
 * JIT Compilation tiers (HotSpot):
 *   Tier 0 — Interpreter: execute bytecode directly
 *   Tier 1 — C1 (client) simple JIT: fast compile, no optimization
 *   Tier 2 — C1 with instrumentation (profiling)
 *   Tier 3 — C1 with full profiling
 *   Tier 4 — C2 (server) JIT: aggressive optimization (inlining, loop unrolling,
 *             escape analysis, dead code elimination, branch prediction, etc.)
 *
 * GraalVM Native Image compiles Java to native binary ahead-of-time (AOT),
 * eliminating the JIT warm-up entirely.
 */
public class BytecodeDemo {

    // -----------------------------------------------------------------------
    // Simple bytecode subjects — the examples below are best understood
    // by running: javap -c BytecodeDemo.class
    // -----------------------------------------------------------------------

    /**
     * Computes a + b.
     *
     * Bytecode pattern:
     *   iload_1   (push a)
     *   iload_2   (push b)
     *   iadd      (pop two, push sum)
     *   ireturn   (return int from top of stack)
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * Computes a * b.
     *
     * Bytecode pattern:
     *   iload_1
     *   iload_2
     *   imul
     *   ireturn
     */
    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * Demonstrates that String concatenation with + compiles to
     * StringBuilder (Java 8) or invokedynamic StringConcatFactory (Java 9+).
     *
     * Java 8 bytecode: new StringBuilder, append(), toString()
     * Java 9+ bytecode: invokedynamic #... StringConcatFactory.makeConcatWithConstants
     *
     * This is why concatenating many strings in a loop is inefficient without
     * an explicit StringBuilder — each iteration creates a new one (Java 8).
     */
    public String concat(String a, String b) {
        return a + b; // watch the bytecode: not a simple 'add'
    }

    /**
     * Illustrates that int/long/float/double arithmetic each use distinct
     * opcodes, even for the same Java operator.
     *
     * int  add: iadd
     * long add: ladd
     */
    public long addLongs(long a, long b) {
        return a + b; // lload_1, lload_3, ladd, lreturn
    }

    /**
     * Demonstrates autoboxing bytecode: `Integer.valueOf(i)` call is inserted
     * by the compiler whenever a primitive is used where an object is expected.
     *
     * Bytecode for `Integer boxed = i`:
     *   iload_1
     *   invokestatic java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
     *   astore_2
     *
     * Unboxing: `int unboxed = boxed`:
     *   aload_2
     *   invokevirtual java/lang/Integer.intValue:()I
     *   istore_3
     */
    public Integer autoboxDemo(int i) {
        Integer boxed   = i;    // autoboxing  → Integer.valueOf(i)
        int     unboxed = boxed; // unboxing    → boxed.intValue()
        return boxed;
    }

    /**
     * Demonstrates instanceof and checkcast bytecode instructions.
     *
     * instanceof: pops object ref, pushes 1 (true) or 0 (false)
     * checkcast:  pops object ref, throws ClassCastException if not compatible,
     *             otherwise pushes ref back unchanged
     */
    public String typeCheckDemo(Object obj) {
        if (obj instanceof String) {      // instanceof bytecode instruction
            String s = (String) obj;      // checkcast bytecode instruction
            return "String: " + s;
        }
        return "Not a String: " + obj;
    }

    /**
     * Demonstrates that a switch on int compiles to either:
     *   - tableswitch  — for dense case ranges (O(1) lookup by index)
     *   - lookupswitch — for sparse cases (sorted key-value pairs, O(log n))
     *
     * @param n switch value
     * @return descriptive label
     */
    public String switchDemo(int n) {
        return switch (n) {   // Java 14+ switch expression
            case 1  -> "one";
            case 2  -> "two";
            case 3  -> "three";
            default -> "other";
        };
    }

    // -----------------------------------------------------------------------
    // JIT warm-up demonstration (behavioral — we can't observe JIT directly)
    // -----------------------------------------------------------------------

    /**
     * Illustrates JIT behavior conceptually.
     * After ~10,000 invocations, HotSpot typically promotes a method to C2.
     * Subsequent calls run at native speed.
     *
     * In production: JVM flags to observe:
     *   -XX:+PrintCompilation          — logs each method as it's JIT-compiled
     *   -XX:CompileThreshold=10000     — invocations before C1 kicks in
     *   -XX:+TieredCompilation         — enable tiered (default since Java 8)
     *
     * @param iterations number of calls to simulate warm-up
     * @return accumulated sum (forces JIT to keep the loop)
     */
    public static long jitWarmUpSimulation(int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            sum += i * i; // hot loop — JIT will optimize this
        }
        return sum;
    }

    public static void main(String[] args) {
        BytecodeDemo demo = new BytecodeDemo();

        System.out.println("=== Bytecode Demo ===");
        System.out.println("Tip: run `javap -c -verbose BytecodeDemo.class` to see opcodes\n");

        System.out.println("add(3, 4)         = " + demo.add(3, 4));
        System.out.println("multiply(3, 4)    = " + demo.multiply(3, 4));
        System.out.println("concat(Hi, There) = " + demo.concat("Hi", "There"));
        System.out.println("addLongs(10L, 20L)= " + demo.addLongs(10L, 20L));
        System.out.println("autoboxDemo(42)   = " + demo.autoboxDemo(42));
        System.out.println("typeCheckDemo(str)= " + demo.typeCheckDemo("hello"));
        System.out.println("typeCheckDemo(123)= " + demo.typeCheckDemo(123));
        System.out.println("switchDemo(2)     = " + demo.switchDemo(2));
        System.out.println("switchDemo(99)    = " + demo.switchDemo(99));

        System.out.println("\n=== JIT Warm-up Simulation ===");
        long t0  = System.nanoTime();
        long sum = jitWarmUpSimulation(1_000_000);
        long t1  = System.nanoTime();
        System.out.printf("jitWarmUpSimulation(1M) sum=%d  time=%,d ns%n", sum, (t1 - t0));
    }
}
