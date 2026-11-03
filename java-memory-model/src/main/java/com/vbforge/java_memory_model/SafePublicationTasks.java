package com.vbforge.java_memory_model;

import java.util.List;

/**
 * Tasks for safe publication.
 * Implement each method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code SafePublicationTasksTest}.
 */
public class SafePublicationTasks {

    /**
     * A producer thread builds an ordinary {@code ArrayList<Integer>} (NOT thread-safe)
     * containing {@code 1..n} and hands it to a consumer thread through a single shared
     * <b>volatile</b> reference field. The consumer waits until the reference appears and
     * returns the list it received. The list must be complete on every run.
     *
     * <p>Examples:</p>
     * <pre>
     * publishViaVolatileField(0) → []
     * publishViaVolatileField(3) → [1, 2, 3]
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>Fill the list completely BEFORE the single assignment that publishes it</li>
     *   <li>The reader should wait for the reference to become non-null, nothing else</li>
     * </ul>
     *
     * @param n number of elements (≥ 0)
     * @return the list received by the consumer thread
     */
    public List<Integer> publishViaVolatileField(int n) {
        throw new UnsupportedOperationException("TODO: implement publishViaVolatileField");
    }

    /**
     * Same hand-off as {@link #publishViaVolatileField(int)}, but the shared "slot" is a
     * {@code ConcurrentHashMap} entry instead of a volatile field: the producer
     * {@code put}s the finished plain {@code ArrayList} under a key, the consumer polls
     * the map until the key is present and returns the list.
     *
     * <p>Examples:</p>
     * <pre>
     * publishViaConcurrentMap(0) → []
     * publishViaConcurrentMap(3) → [1, 2, 3]
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>A {@code put} into a concurrent map happens-before a later {@code get} that sees it</li>
     * </ul>
     *
     * @param n number of elements (≥ 0)
     * @return the list received by the consumer thread
     */
    public List<Integer> publishViaConcurrentMap(int n) {
        throw new UnsupportedOperationException("TODO: implement publishViaConcurrentMap");
    }

    /**
     * Demonstrate the {@code this}-escape problem, single-threaded.
     * Define a small class with a {@code final int} field whose constructor first passes
     * {@code this} to a callback and only then assigns the field. Record what the callback
     * reads from the object. Then do the same with a safe variant that invokes the callback
     * only after construction (for example from a static factory method).
     *
     * <p>Examples:</p>
     * <pre>
     * valuesSeenByEscapedThis(99) → [0, 99]
     * valuesSeenByEscapedThis(-5) → [0, -5]
     * valuesSeenByEscapedThis(0)  → [0, 0]
     * </pre>
     * Index 0 = what the callback saw during construction, index 1 = what it saw with the safe variant.
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>Reading a final field before its assignment yields the type's default value</li>
     *   <li>A {@code Consumer} makes a convenient callback</li>
     * </ul>
     *
     * @param value the value assigned to the final field
     * @return {@code {seenDuringConstruction, seenAfterSafeConstruction}}
     */
    public int[] valuesSeenByEscapedThis(int value) {
        throw new UnsupportedOperationException("TODO: implement valuesSeenByEscapedThis");
    }
}
