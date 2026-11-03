package com.vbforge.java_memory_model;

import java.util.List;

/**
 * Tasks for the happens-before relation.
 * Every task is solved by picking the RIGHT edge — not by adding sleeps.
 * Implement each method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code HappensBeforeTasksTest}.
 */
public class HappensBeforeTasks {

    /**
     * A writer thread fills a plain (non-volatile, non-atomic) {@code int[]} of length
     * {@code n} with the values {@code 1..n}, then raises a volatile flag. A reader thread
     * waits for the flag and then returns the sum of the array.
     * The sum must be correct on every run.
     *
     * <p>Examples:</p>
     * <pre>
     * sumPublishedViaVolatile(0)    → 0
     * sumPublishedViaVolatile(4)    → 10   (1 + 2 + 3 + 4)
     * sumPublishedViaVolatile(1000) → 500500
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>The array elements are plain writes — the flag is what publishes them</li>
     *   <li>Read the array only AFTER you have seen the flag raised</li>
     * </ul>
     *
     * @param n number of elements (≥ 0)
     * @return the sum 1 + 2 + … + n as seen by the reader thread
     */
    public long sumPublishedViaVolatile(int n) {
        throw new UnsupportedOperationException("TODO: implement sumPublishedViaVolatile");
    }

    /**
     * Start one worker thread that writes {@code i * i} into a plain {@code int[]} at
     * index {@code i} for {@code i = 0..n-1}. The calling thread must return that array
     * after the worker has finished — without using {@code volatile}, locks or atomics.
     *
     * <p>Examples:</p>
     * <pre>
     * squaresFromWorker(0) → []
     * squaresFromWorker(5) → [0, 1, 4, 9, 16]
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>Which {@code Thread} method creates a happens-before edge from the worker's last action to the caller?</li>
     * </ul>
     *
     * @param n number of elements (≥ 0)
     * @return the array filled by the worker thread
     */
    public int[] squaresFromWorker(int n) {
        throw new UnsupportedOperationException("TODO: implement squaresFromWorker");
    }

    /**
     * Relay of three threads over plain fields, linked ONLY by volatile flags:
     * <ol>
     *   <li>Thread A writes {@code a = seed}, then raises {@code flag1}</li>
     *   <li>Thread B waits for {@code flag1}, writes {@code b = a * 2}, then raises {@code flag2}</li>
     *   <li>Thread C waits for {@code flag2}, writes {@code c = a + b}</li>
     * </ol>
     * The caller waits for thread C to finish and returns {@code {a, b, c}}.
     *
     * <p>Examples:</p>
     * <pre>
     * relayThroughThree(1)  → [1, 2, 3]
     * relayThroughThree(10) → [10, 20, 30]
     * relayThroughThree(0)  → [0, 0, 0]
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>C never touches {@code flag1}, yet it must see A's write — transitivity</li>
     *   <li>Start all three threads before waiting; each one waits for its own predecessor</li>
     * </ul>
     *
     * @param seed the value written by thread A
     * @return {@code {a, b, c}}
     */
    public int[] relayThroughThree(int seed) {
        throw new UnsupportedOperationException("TODO: implement relayThroughThree");
    }

    /**
     * Two threads, "ping" and "pong", take turns appending to ONE plain {@code ArrayList}
     * (which is not thread-safe). Each thread appends its word {@code rounds} times and
     * the turns must strictly alternate: ping, pong, ping, pong, …
     * Use a monitor with {@code wait}/{@code notifyAll}. The caller waits for both threads
     * and returns the list.
     *
     * <p>Examples:</p>
     * <pre>
     * pingPong(0) → []
     * pingPong(1) → ["ping", "pong"]
     * pingPong(3) → ["ping", "pong", "ping", "pong", "ping", "pong"]
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>A shared "whose turn is it" field, guarded by the same lock as the list</li>
     *   <li>Always call {@code wait()} inside a {@code while} loop that re-checks the condition</li>
     *   <li>{@code notify()} may wake the wrong thread — think about {@code notifyAll()}</li>
     * </ul>
     *
     * @param rounds how many times each thread appends (≥ 0)
     * @return the list of {@code 2 * rounds} words in strict alternation
     */
    public List<String> pingPong(int rounds) {
        throw new UnsupportedOperationException("TODO: implement pingPong");
    }
}
