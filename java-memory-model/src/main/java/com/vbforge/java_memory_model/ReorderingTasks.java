package com.vbforge.java_memory_model;

/**
 * Tasks for reordering. Each task is a litmus test you build yourself — and the
 * property you implement is one the JMM <b>guarantees</b>, so the result is the same on every machine.
 * Implement each method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code ReorderingTasksTest}.
 */
public class ReorderingTasks {

    /**
     * Run the store-buffering litmus test {@code iterations} times with
     * <b>sequentially consistent</b> shared variables (two shared ints {@code x} and {@code y},
     * both initially 0):
     * <pre>
     * Thread 1:  x = 1;  r1 = y;
     * Thread 2:  y = 1;  r2 = x;
     * </pre>
     * Start two fresh threads per iteration, join both, and tally the pair {@code (r1, r2)}.
     * Return counts indexed by {@code (r1 * 2) + r2}.
     *
     * <p>Examples:</p>
     * <pre>
     * storeBufferingOutcomes(0)    → [0, 0, 0, 0]
     * storeBufferingOutcomes(1000) → [0, a, b, c]   with a + b + c == 1000
     * </pre>
     * Index 0 — the pair (0, 0) — can never occur.
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>Which modifier turns the four shared accesses into one total order?</li>
     *   <li>{@code r1} and {@code r2} may be plain fields if you read them after {@code join()}</li>
     * </ul>
     *
     * @param iterations number of rounds (≥ 0)
     * @return array of length 4 with the number of rounds per outcome
     */
    public int[] storeBufferingOutcomes(int iterations) {
        throw new UnsupportedOperationException("TODO: implement storeBufferingOutcomes");
    }

    /**
     * Run the message-passing litmus test {@code iterations} times:
     * <pre>
     * Writer:  data = 1;  flag = 1;          (both initially 0)
     * Reader:  r1 = flag; r2 = data;
     * </pre>
     * Start both threads per iteration, join them, and count the rounds in which the reader
     * saw {@code r1 == 1} but {@code r2 == 0} (the flag without the data). Publish with a
     * <b>volatile</b> flag, so that count must be 0.
     *
     * <p>Examples:</p>
     * <pre>
     * countStaleReads(0)    → 0
     * countStaleReads(1000) → 0
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>The reader may read the flag in a bounded retry loop to make "flag seen" more likely</li>
     *   <li>Only the flag needs a special modifier — which write is ordered before it?</li>
     * </ul>
     *
     * @param iterations number of rounds (≥ 0)
     * @return number of rounds with a stale read
     */
    public int countStaleReads(int iterations) {
        throw new UnsupportedOperationException("TODO: implement countStaleReads");
    }

    /**
     * Same experiment as {@link #countStaleReads(int)}, but with <b>plain</b> fields guarded by a
     * single lock instead of {@code volatile}. The writer sets both fields inside one critical
     * section; the reader reads both fields inside one critical section.
     *
     * <p>Examples:</p>
     * <pre>
     * countStaleReadsSynchronized(0)    → 0
     * countStaleReadsSynchronized(1000) → 0
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>If the reader reads the two fields in <i>separate</i> critical sections, the writer can sneak in between</li>
     *   <li>Both threads must use the same lock object</li>
     * </ul>
     *
     * @param iterations number of rounds (≥ 0)
     * @return number of rounds with a stale read
     */
    public int countStaleReadsSynchronized(int iterations) {
        throw new UnsupportedOperationException("TODO: implement countStaleReadsSynchronized");
    }
}
