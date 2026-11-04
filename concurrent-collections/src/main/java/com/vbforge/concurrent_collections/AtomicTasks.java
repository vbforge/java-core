package com.vbforge.concurrent_collections;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tasks for {@code AtomicDemo}. Implement each method, then remove
 * {@code @Disabled} from the matching {@code @Nested} class in
 * {@code AtomicTasksTest}.
 *
 * <p>Each test runs with a 10 second time-out, so a deadlock in your solution
 * shows up as a failed test and not as a build that never finishes.
 */
public class AtomicTasks {

    /**
     * Start {@code threads} threads. Each of them increments ONE shared counter
     * {@code incrementsPerThread} times. Wait for all threads and return the
     * final value of the counter. No increment may be lost.
     *
     * <p>Examples:
     * <pre>
     * countConcurrently(4, 10_000)  →  40_000
     * countConcurrently(1, 5)       →  5
     * countConcurrently(3, 0)       →  0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@code volatile int} and {@code count++} would lose updates</li>
     *   <li>There are two atomic counters worth considering here: one that returns the new value
     *       of every update, and one that is optimised for many threads that only add and read at the end</li>
     * </ul>
     *
     * @param threads             number of threads, at least 1
     * @param incrementsPerThread increments per thread, at least 0
     * @return {@code threads * incrementsPerThread}, counted by the threads
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public long countConcurrently(int threads, int incrementsPerThread) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement countConcurrently");
    }

    /**
     * Start {@code threads} threads. Each of them asks {@code idsPerThread}
     * times for a new id from ONE shared generator. Ids start at 1 and grow by
     * 1. Return all ids handed out, in any order, after all threads finished.
     * Every id from 1 to {@code threads * idsPerThread} must appear exactly once.
     *
     * <p>Examples:
     * <pre>
     * generateUniqueIds(2, 3)  →  [1, 2, 3, 4, 5, 6]   (any order)
     * generateUniqueIds(1, 1)  →  [1]
     * generateUniqueIds(4, 0)  →  []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Here you need the value that YOUR increment produced - not just a total at the end</li>
     *   <li>Collect the ids per thread first and merge afterwards, or use a thread-safe collection</li>
     * </ul>
     *
     * @param threads      number of threads, at least 1
     * @param idsPerThread ids requested by each thread, at least 0
     * @return all generated ids, in any order
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public List<Long> generateUniqueIds(int threads, int idsPerThread) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement generateUniqueIds");
    }

    /**
     * Raise {@code target} to {@code candidate} <b>if and only if</b>
     * {@code candidate} is larger than the current value. The whole update must be
     * atomic, so many threads can call this at once and the target ends up
     * holding the largest candidate any of them offered. Return the value the
     * target holds after the call.
     *
     * <p>Examples:
     * <pre>
     * target = 5
     * atomicMax(target, 9)   →  9     target is now 9
     * atomicMax(target, 3)   →  9     target is still 9
     * atomicMax(target, 9)   →  9     target is still 9
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code AtomicInteger} has a method that combines the current value with another value using a function</li>
     *   <li>Alternatively: read, decide, and {@code compareAndSet} in a loop</li>
     * </ul>
     *
     * @param target    the shared value (never null)
     * @param candidate the value to offer
     * @return the value held by {@code target} after the call
     */
    public int atomicMax(AtomicInteger target, int candidate) {
        throw new UnsupportedOperationException("TODO: implement atomicMax");
    }

    /**
     * Run {@code init} <b>exactly once</b> no matter how many threads call this
     * method with the same flag. The caller that actually ran {@code init}
     * returns {@code true}; every other caller returns {@code false}
     * immediately, without waiting for the initialisation to finish.
     *
     * <p>Examples:
     * <pre>
     * flag = false
     * initOnce(flag, init)   →  true      init ran, flag is now true
     * initOnce(flag, init)   →  false     init did NOT run again
     *
     * flag = true            (already initialised)
     * initOnce(flag, init)   →  false     init did NOT run
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Exactly one of several racing {@code compareAndSet} calls can succeed</li>
     * </ul>
     *
     * @param flag the shared "initialised" flag (never null)
     * @param init the initialisation to run (never null)
     * @return {@code true} if this call ran {@code init}
     */
    public boolean initOnce(AtomicBoolean flag, Runnable init) {
        throw new UnsupportedOperationException("TODO: implement initOnce");
    }

    /**
     * Atomically move {@code state} from {@code from} to {@code to}. The move
     * only happens if the current state is <b>equal</b> ({@code equals}) to
     * {@code from}. Return whether it happened. If several threads try the same
     * transition at once, exactly one of them gets {@code true}.
     *
     * <p>The {@code from} argument may be a different String object than the one
     * stored in the reference, as long as the text is the same.
     *
     * <p>Examples:
     * <pre>
     * state = "NEW"
     * transition(state, "NEW", "RUNNING")      →  true     state is now "RUNNING"
     * transition(state, "NEW", "RUNNING")      →  false    state is still "RUNNING"
     * transition(state, "RUNNING", "DONE")     →  true     state is now "DONE"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code compareAndSet} on a reference compares with {@code ==}, not {@code equals}</li>
     *   <li>So read the current reference first, compare it by value, then compare-and-set THAT reference</li>
     *   <li>If the swap fails because another thread got in between, look at the new state again</li>
     * </ul>
     *
     * @param state the shared state (never null, never holds null)
     * @param from  the required current state (never null)
     * @param to    the new state (never null)
     * @return {@code true} if the state was moved from {@code from} to {@code to} by this call
     */
    public boolean transition(AtomicReference<String> state, String from, String to) {
        throw new UnsupportedOperationException("TODO: implement transition");
    }

    /**
     * Count how often each value in {@code values} occurs, using
     * {@code threads} threads that update ONE shared set of counters. Split the
     * input between the threads, wait for all of them, and return the counts as
     * a plain array where {@code result[v]} is the number of occurrences of
     * {@code v}.
     *
     * <p>Examples:
     * <pre>
     * histogram([0, 1, 1, 2, 2, 2], 4, 2)  →  [1, 2, 3, 0]
     * histogram([], 3, 4)                  →  [0, 0, 0]
     * histogram([2, 2], 3, 8)              →  [0, 0, 2]      (more threads than values is fine)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A shared {@code int[]} loses updates in every cell, and {@code volatile} does not help</li>
     *   <li>There is an atomic class that gives every element of an array the atomic operations</li>
     * </ul>
     *
     * @param values  the values to count; every value is in the range {@code 0 <= v < buckets}
     * @param buckets number of distinct values, at least 1
     * @param threads number of worker threads, at least 1
     * @return an array of length {@code buckets} with the occurrence counts
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    public int[] histogram(int[] values, int buckets, int threads) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement histogram");
    }
}
