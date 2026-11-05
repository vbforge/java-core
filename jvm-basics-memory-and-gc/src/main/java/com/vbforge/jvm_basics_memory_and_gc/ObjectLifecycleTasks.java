package com.vbforge.jvm_basics_memory_and_gc;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tasks for {@link ObjectLifecycleDemo}: implement each method so that
 * {@code ObjectLifecycleTasksTest} passes.
 *
 * <p>A heap is a {@code Map<String, List<String>>}: each key is an object, its value
 * lists the objects it references. Roots and references that are not keys of the map
 * must be ignored.
 */
public class ObjectLifecycleTasks {

    /**
     * Return every object that is <b>not</b> reachable from any root — i.e. the objects
     * a garbage collector would reclaim.
     *
     * <p>Examples:
     * <pre>
     * graph: A→B, B→C, C, D→E, E→D, F→A      roots: {A}
     *   → {D, E, F}      (a cycle and an object pointing INTO live data are still garbage)
     * graph: A→B, B                           roots: {}
     *   → {A, B}
     * graph: A→B, B                           roots: {A}
     *   → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Trace from the roots; garbage is whatever you never visit</li>
     *   <li>Guard against cycles — remember what you already visited</li>
     * </ul>
     *
     * @param graph the heap: object → objects it references
     * @param roots the GC roots
     * @return the set of unreachable objects
     */
    public Set<String> findGarbage(Map<String, List<String>> graph, Set<String> roots) {
        throw new UnsupportedOperationException("TODO: implement findGarbage");
    }

    /**
     * Return the <em>retained set</em> of {@code node}: the objects that would become
     * garbage if {@code node} were removed from the heap. {@code node} itself is part
     * of the result when it was reachable. Heap-dump tools use this idea to show which
     * object is responsible for keeping memory alive.
     *
     * <p>Removing {@code node} means: it is not a root any more, it is never traversed,
     * and references to it are ignored. If {@code node} was not reachable to begin with,
     * the result is empty.
     *
     * <p>Examples:
     * <pre>
     * graph: A→B,C   B→D   C→D,E   D   E      roots: {A}
     *   retainedSet(.., "B") → {B}           (D is still held by C)
     *   retainedSet(.., "C") → {C, E}        (D is still held by B)
     *   retainedSet(.., "A") → {A, B, C, D, E}
     *   retainedSet(.., "Z") → {}            (not in the heap)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Compare "reachable now" with "reachable if the node did not exist"</li>
     *   <li>Do not modify the graph passed in</li>
     * </ul>
     *
     * @param graph the heap: object → objects it references
     * @param roots the GC roots
     * @param node  the object to remove
     * @return objects that are reachable now but would be unreachable without {@code node}
     */
    public Set<String> retainedSet(Map<String, List<String>> graph, Set<String> roots, String node) {
        throw new UnsupportedOperationException("TODO: implement retainedSet");
    }

    /**
     * Describe an object's journey through the generations. The returned list has
     * {@code collections + 1} entries: entry {@code i} is the stage of an object that has
     * survived {@code i} collections. The stage is {@code "EDEN"} for 0 survived,
     * {@code "SURVIVOR"} for 1 up to {@code tenuringThreshold - 1}, and {@code "OLD"}
     * from {@code tenuringThreshold} on.
     *
     * <p>Examples:
     * <pre>
     * lifecycle(3, 2) → [EDEN, SURVIVOR, OLD, OLD]
     * lifecycle(0, 5) → [EDEN]
     * lifecycle(4, 1) → [EDEN, OLD, OLD, OLD, OLD]
     * lifecycle(3, 4) → [EDEN, SURVIVOR, SURVIVOR, SURVIVOR]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Build the list with a loop over the number of survived collections</li>
     * </ul>
     *
     * @param collections      how many young collections the object lives through, &gt;= 0
     * @param tenuringThreshold the survival count at which an object is promoted, &gt;= 1
     * @return the stage after 0, 1, …, {@code collections} survived collections
     */
    public List<String> lifecycle(int collections, int tenuringThreshold) {
        throw new UnsupportedOperationException("TODO: implement lifecycle");
    }

    /**
     * Return how many contiguous G1 regions a single object occupies. An object smaller
     * than half a region is not humongous and needs {@code 0} <em>humongous</em> regions
     * (it lives in Eden). A humongous object (at least half a region) occupies
     * {@code ceil(objectBytes / regionBytes)} regions.
     *
     * <p>Examples (region = 1_048_576 bytes):
     * <pre>
     * humongousRegionsNeeded(400_000, 1_048_576)   → 0
     * humongousRegionsNeeded(524_288, 1_048_576)   → 1   (exactly half → humongous)
     * humongousRegionsNeeded(1_048_576, 1_048_576) → 1
     * humongousRegionsNeeded(1_048_577, 1_048_576) → 2
     * humongousRegionsNeeded(3_000_000, 1_048_576) → 3
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Integer division rounds down — look for a way to round up without floating point</li>
     * </ul>
     *
     * @param objectBytes the object size in bytes, &gt; 0
     * @param regionBytes the region size in bytes, &gt; 0
     * @return the number of regions the humongous object spans, or 0 if it is not humongous
     */
    public long humongousRegionsNeeded(long objectBytes, long regionBytes) {
        throw new UnsupportedOperationException("TODO: implement humongousRegionsNeeded");
    }
}
