package com.vbforge.design_patterns.singleton_pattern;

/**
 * SingletonTasks — implement every method below to make {@code SingletonTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code SingletonDemo.java} to see each variation in action.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Remove {@code @Disabled} from the corresponding {@code @Nested} class in the test.</li>
 *   <li>Run {@code mvn test -Dtest=SingletonTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 */
public class SingletonTasks {

    /**
     * Shared contract returned by every singleton flavour. Increments a
     * per-instance counter — used to prove that two callers see the same
     * singleton state.
     */
    public interface Counter {
        int increment();
        int get();
    }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a lazily-initialised, thread-safe counter singleton using
     * <strong>double-checked locking</strong>.
     *
     * <p>Examples:
     * <pre>
     *   getDoubleCheckedSingleton() == getDoubleCheckedSingleton()   → true
     *
     *   Counter a = getDoubleCheckedSingleton();
     *   Counter b = getDoubleCheckedSingleton();
     *   a.increment();                          // 1
     *   b.get();                                // 1  ← shared state, same instance
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Hold the singleton in a {@code private static volatile} field.</li>
     *   <li>First null-check outside {@code synchronized}, second inside.</li>
     *   <li>Do not omit {@code volatile} — it prevents reordering.</li>
     * </ul>
     *
     * @return the shared {@link Counter} instance
     */
    public Counter getDoubleCheckedSingleton() {
        throw new UnsupportedOperationException("TODO: implement getDoubleCheckedSingleton");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a lazily-initialised, thread-safe counter singleton using the
     * <strong>holder idiom</strong> (initialization-on-demand holder).
     *
     * <p>Examples:
     * <pre>
     *   getHolderSingleton() == getHolderSingleton()   → true
     *
     *   Counter c = getHolderSingleton();
     *   c.increment();                                  // 1
     *   c.increment();                                  // 2
     *   c.get();                                        // 2
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Declare a {@code private static class Holder} with a
     *       {@code static final Counter INSTANCE = ...}.</li>
     *   <li>{@code getHolderSingleton()} just returns {@code Holder.INSTANCE}.</li>
     *   <li>No {@code synchronized} and no {@code volatile} needed.</li>
     * </ul>
     *
     * @return the shared {@link Counter} instance
     */
    public Counter getHolderSingleton() {
        throw new UnsupportedOperationException("TODO: implement getHolderSingleton");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a singleton {@link Counter} implemented as an <strong>enum</strong>.
     *
     * <p>Examples:
     * <pre>
     *   getEnumSingleton() == getEnumSingleton()   → true
     *
     *   Counter c = getEnumSingleton();
     *   c.increment();                              // 1
     *   c.increment();                              // 2
     *   c.get();                                    // 2
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Declare a {@code private enum CounterImpl implements Counter { INSTANCE; ... }}.</li>
     *   <li>Return {@code CounterImpl.INSTANCE}.</li>
     *   <li>State must live in an instance field of the enum constant.</li>
     * </ul>
     *
     * @return the shared enum-backed {@link Counter} instance
     */
    public Counter getEnumSingleton() {
        throw new UnsupportedOperationException("TODO: implement getEnumSingleton");
    }
}