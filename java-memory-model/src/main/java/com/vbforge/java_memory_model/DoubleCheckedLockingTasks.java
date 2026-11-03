package com.vbforge.java_memory_model;

import java.util.function.Supplier;

/**
 * Tasks for lazy initialisation and double-checked locking.
 * Implement each method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code DoubleCheckedLockingTasksTest}.
 */
public class DoubleCheckedLockingTasks {

    /**
     * Return a thread-safe lazy {@code Supplier}: the first {@code get()} calls {@code factory}
     * and remembers the result; every later {@code get()} returns that same object.
     * {@code factory} must be called at most once even when many threads call
     * {@code get()} at the same time, and must not be called before the first {@code get()}.
     * Use the simple approach: lock on every call.
     *
     * <p>Examples:</p>
     * <pre>
     * Supplier&lt;Object&gt; s = lazyWithSynchronized(() -&gt; new Object());
     * s.get() == s.get()      → true
     * // factory has run exactly once, 16 racing threads all got the same instance
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>Check and create under the same lock</li>
     *   <li>You may return an anonymous class, a lambda over a holder object, or a small nested class</li>
     * </ul>
     *
     * @param factory creates the value; never returns {@code null} in this task
     * @param <T>     value type
     * @return a lazy supplier backed by {@code factory}
     */
    public <T> Supplier<T> lazyWithSynchronized(Supplier<T> factory) {
        throw new UnsupportedOperationException("TODO: implement lazyWithSynchronized");
    }

    /**
     * Same contract as {@link #lazyWithSynchronized(Supplier)}, but after initialisation callers
     * must not take any lock: implement double-checked locking correctly.
     *
     * <p>Examples:</p>
     * <pre>
     * Supplier&lt;Object&gt; s = lazyWithDoubleCheckedLocking(() -&gt; new Object());
     * s.get() == s.get()      → true
     * // factory has run exactly once, 16 racing threads all got the same instance
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>Which modifier on the cached field makes the unlocked first check safe?</li>
     *   <li>Test the field again after acquiring the lock</li>
     * </ul>
     *
     * @param factory creates the value; never returns {@code null} in this task
     * @param <T>     value type
     * @return a lazy supplier backed by {@code factory}
     */
    public <T> Supplier<T> lazyWithDoubleCheckedLocking(Supplier<T> factory) {
        throw new UnsupportedOperationException("TODO: implement lazyWithDoubleCheckedLocking");
    }

    /**
     * Same lazy contract, but {@code factory} <b>may return {@code null}</b>. A {@code null}
     * result still counts as "initialised": {@code factory} must still run exactly once,
     * and every {@code get()} returns {@code null} afterwards.
     *
     * <p>Examples:</p>
     * <pre>
     * Supplier&lt;String&gt; s = lazyNullable(() -&gt; null);
     * s.get() → null
     * s.get() → null      // factory was NOT called a second time
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>"value == null" can no longer mean "not yet computed"</li>
     *   <li>Keep a separate marker for "initialised" — and publish it safely</li>
     * </ul>
     *
     * @param factory creates the value; may return {@code null}
     * @param <T>     value type
     * @return a lazy supplier backed by {@code factory}
     */
    public <T> Supplier<T> lazyNullable(Supplier<T> factory) {
        throw new UnsupportedOperationException("TODO: implement lazyNullable");
    }

    /**
     * Return the one and only instance of an object of your choice (for example a
     * {@code new Object()}), using the initialization-on-demand holder idiom: the instance
     * must be created lazily, exactly once, with no {@code volatile} and no explicit lock
     * in your code. Every call, from any thread, returns the same instance.
     *
     * <p>Examples:</p>
     * <pre>
     * holderIdiomInstance() == holderIdiomInstance()   → true
     * // 16 racing threads all receive the same instance
     * </pre>
     *
     * <p>Hints:</p>
     * <ul>
     *   <li>A private static nested class with a {@code static final} field</li>
     *   <li>The JVM initialises that nested class on first use, once, under its own lock</li>
     * </ul>
     *
     * @return the shared instance
     */
    public Object holderIdiomInstance() {
        throw new UnsupportedOperationException("TODO: implement holderIdiomInstance");
    }
}
