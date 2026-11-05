package com.vbforge.jvm_basics_memory_and_gc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Object lifecycle — how the JVM decides an object is garbage.
 *
 * <p>The object graph is modelled as {@code Map<String, List<String>>}: every key is an
 * object, its value lists the objects it references. That lets us run a tiny, fully
 * deterministic mark-and-sweep without depending on real GC timing.
 */
public class ObjectLifecycleDemo {

    public static void main(String[] args) {
        reachabilityDemo();
        System.out.println("---");
        markAndSweepDemo();
        System.out.println("---");
        generationsDemo();
        System.out.println("---");
        humongousDemo();
    }

    /**
     * A small heap: {@code A→B→C} hangs off a root, {@code D↔E} is an isolated cycle,
     * and {@code F} (garbage) still points at the live object {@code A}.
     */
    public static Map<String, List<String>> sampleGraph() {
        Map<String, List<String>> graph = new LinkedHashMap<>();
        graph.put("A", new ArrayList<>(Collections.singletonList("B")));
        graph.put("B", new ArrayList<>(Collections.singletonList("C")));
        graph.put("C", new ArrayList<>());
        graph.put("D", new ArrayList<>(Collections.singletonList("E")));
        graph.put("E", new ArrayList<>(Collections.singletonList("D")));
        graph.put("F", new ArrayList<>(Collections.singletonList("A")));
        return graph;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Reachability from GC roots
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: an object is *live* if it can be reached by following references from a
     *       GC root. Roots are things outside the heap graph: local variables and
     *       parameters on thread stacks, static fields, JNI references, active threads.
     *
     * Why it matters: the JVM does NOT count references (reference counting). It
     *   traces from roots. So:
     *     - a cycle (D↔E) with no path from a root is garbage
     *     - a reference FROM garbage (F→A) does not keep anything alive
     *     - "memory leak" in Java = still reachable from a root, yet no longer needed
     *
     * Complexity (mark phase):
     *   - time:  O(live objects + live references) — dead objects cost nothing to find
     *   - space: O(live objects) for the marked set and work stack
     *
     * Common gotcha: setting a local variable to null helps only if no other root
     *   still reaches the object.
     */
    public static void reachabilityDemo() {
        Map<String, List<String>> graph = sampleGraph();
        Set<String> live = reachable(graph, Collections.singleton("A"));
        Set<String> garbage = new LinkedHashSet<>(graph.keySet());
        garbage.removeAll(live);
        System.out.println("graph   : " + graph);
        System.out.println("roots   : [A]");
        System.out.println("live    : " + live);
        System.out.println("garbage : " + garbage + "  (note the D<->E cycle and F -> A)");
    }

    /**
     * Mark phase: returns every object reachable from {@code roots}. Roots or references
     * that are not keys of {@code graph} are ignored.
     */
    public static Set<String> reachable(Map<String, List<String>> graph, Collection<String> roots) {
        Set<String> marked = new LinkedHashSet<>();
        Deque<String> workStack = new ArrayDeque<>();
        for (String root : roots) {
            if (graph.containsKey(root) && marked.add(root)) {
                workStack.push(root);
            }
        }
        while (!workStack.isEmpty()) {
            String current = workStack.pop();
            for (String next : graph.get(current)) {
                if (graph.containsKey(next) && marked.add(next)) {
                    workStack.push(next);
                }
            }
        }
        return marked;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. Mark-and-sweep
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: the classic two-phase algorithm.
     *         mark  — find all reachable objects (previous section)
     *         sweep — reclaim everything that was not marked
     *       Real collectors differ in how they reclaim (copy survivors, compact,
     *       free-list), but all start from the same reachability question.
     *
     * Why it matters: explains why GC cost follows the amount of LIVE data, and why a
     *   large heap full of garbage is cheap to collect while a large live set is not.
     *
     * Complexity:
     *   - mark:  O(live)
     *   - sweep: O(heap objects) for a non-moving sweep; a copying collector pays only O(live)
     *
     * Common gotcha: the sweep returns memory to the JVM's heap, not necessarily to the OS.
     */
    public static void markAndSweepDemo() {
        Map<String, List<String>> before = sampleGraph();
        Map<String, List<String>> after = markAndSweep(before, Collections.singleton("A"));
        System.out.println("before sweep : " + before.keySet());
        System.out.println("after sweep  : " + after.keySet());
    }

    /** Returns a new heap containing only the objects reachable from {@code roots}. */
    public static Map<String, List<String>> markAndSweep(Map<String, List<String>> graph,
                                                         Collection<String> roots) {
        Set<String> live = reachable(graph, roots);
        Map<String, List<String>> survivors = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {
            if (live.contains(entry.getKey())) {
                survivors.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return survivors;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Generations and tenuring
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: the heap is split by object age (the "weak generational hypothesis":
     *       most objects die young).
     *         Eden     — new objects are allocated here
     *         Survivor — objects that survived at least one young collection
     *         Old      — objects that survived `tenuringThreshold` collections
     *
     * Why it matters: young collections only scan the small young area, so they are
     *   short and frequent; the expensive old-generation work happens rarely.
     *
     * Complexity: a young collection costs O(live young objects), independent of how
     *   much garbage was in Eden.
     *
     * Common gotcha: the threshold is a maximum. HotSpot adapts it at run time, so the
     *   real promotion age can be lower than -XX:MaxTenuringThreshold (default 15).
     */
    public static void generationsDemo() {
        int threshold = 3;
        for (int survived = 0; survived <= 4; survived++) {
            System.out.println("survived " + survived + " collections -> " + generationFor(survived, threshold));
        }
    }

    /** Returns EDEN (0 survived), SURVIVOR (1 … threshold-1) or OLD (threshold or more). */
    public static String generationFor(int survivedCollections, int tenuringThreshold) {
        if (survivedCollections <= 0) {
            return "EDEN";
        }
        return survivedCollections < tenuringThreshold ? "SURVIVOR" : "OLD";
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Humongous objects (G1)
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: G1 divides the heap into equal regions (1–32 MB, a power of two). Any object
     *       of at least HALF a region is "humongous": it skips Eden and is allocated
     *       directly into contiguous old-generation regions.
     *
     * Why it matters: huge arrays/buffers can fragment the heap and trigger extra
     *   concurrent cycles, so many large short-lived arrays hurt G1.
     *
     * Complexity: the check itself is O(1).
     *
     * Common gotcha: an object of exactly half a region IS humongous (>=, not >).
     */
    public static void humongousDemo() {
        long region = 1024 * 1024;
        long[] sizes = {100_000, 524_287, 524_288, 2_000_000};
        for (long size : sizes) {
            System.out.println(size + " bytes in a " + region + "-byte region -> humongous? "
                    + isHumongous(size, region));
        }
        System.out.println("sizes used: " + Arrays.toString(sizes));
    }

    /** True when the object occupies at least half of a G1 region. */
    public static boolean isHumongous(long objectBytes, long regionBytes) {
        return objectBytes >= regionBytes / 2;
    }
}
