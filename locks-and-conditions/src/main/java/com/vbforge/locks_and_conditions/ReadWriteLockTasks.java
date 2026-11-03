package com.vbforge.locks_and_conditions;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * 🎯 Tasks for {@link ReadWriteLockDemo}.
 *
 * <p>Implement every method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code ReadWriteLockTasksTest}.
 */
public class ReadWriteLockTasks {

    /**
     * Read the value stored under {@code key} while holding the <b>read</b> lock
     * of {@code lock}. Return {@code null} if the key is absent.
     *
     * <p>Examples:
     * <pre>
     * cache = {a=1}   getCached(lock, cache, "a") → 1
     * cache = {a=1}   getCached(lock, cache, "z") → null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code lock.readLock()} returns the shared half of the pair</li>
     *   <li>Release it in a {@code finally} block</li>
     * </ul>
     *
     * @param lock  the read/write lock guarding {@code cache}
     * @param cache the (non-thread-safe) map to read
     * @param key   the key to look up
     * @param <K>   key type
     * @param <V>   value type
     * @return the stored value or {@code null}
     */
    public <K, V> V getCached(ReentrantReadWriteLock lock, Map<K, V> cache, K key) {
        throw new UnsupportedOperationException("TODO: implement getCached");
    }

    /**
     * Store {@code value} under {@code key} while holding the <b>write</b> lock
     * of {@code lock}.
     *
     * <p>Examples:
     * <pre>
     * cache = {}      putCached(lock, cache, "a", 1) → cache is {a=1}
     * cache = {a=1}   putCached(lock, cache, "a", 2) → cache is {a=2}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code lock.writeLock()} returns the exclusive half of the pair</li>
     *   <li>The lock must be free again when the method returns</li>
     * </ul>
     *
     * @param lock  the read/write lock guarding {@code cache}
     * @param cache the (non-thread-safe) map to modify
     * @param key   the key
     * @param value the value to store
     * @param <K>   key type
     * @param <V>   value type
     */
    public <K, V> void putCached(ReentrantReadWriteLock lock, Map<K, V> cache, K key, V value) {
        throw new UnsupportedOperationException("TODO: implement putCached");
    }

    /**
     * Return the cached value for {@code key}; if it is missing, compute it with
     * {@code loader}, store it and return it. The loader must be called <b>at
     * most once per key</b>, even when many threads ask for the same missing key
     * at the same time.
     *
     * <p>Examples:
     * <pre>
     * cache = {a=1}   getOrCompute(lock, cache, "a", loader) → 1   (loader not called)
     * cache = {}      getOrCompute(lock, cache, "b", () -> 7) → 7   (cache is now {b=7})
     * 8 threads ask for missing key "k" at once → loader called exactly 1 time
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Try the cheap path first under the read lock</li>
     *   <li>You cannot upgrade a read lock — release it before taking the write lock</li>
     *   <li>Another thread may have filled the key in between: check again</li>
     * </ul>
     *
     * @param lock   the read/write lock guarding {@code cache}
     * @param cache  the (non-thread-safe) map
     * @param key    the key
     * @param loader computes the value when the key is absent
     * @param <K>    key type
     * @param <V>    value type
     * @return the cached or freshly computed value
     */
    public <K, V> V getOrCompute(ReentrantReadWriteLock lock, Map<K, V> cache, K key, Supplier<V> loader) {
        throw new UnsupportedOperationException("TODO: implement getOrCompute");
    }

    /**
     * While the current thread holds the <b>read</b> lock of {@code lock},
     * attempt — without blocking — to also obtain the <b>write</b> lock.
     * Return whether that worked. Release everything you acquired before returning.
     *
     * <p>Examples:
     * <pre>
     * canWriteWhileHoldingRead(new ReentrantReadWriteLock()) → false
     * (lock is completely free afterwards)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A blocking {@code writeLock().lock()} here would hang forever — that is the lesson</li>
     *   <li>Use the non-blocking acquisition method</li>
     *   <li>Unlock the write lock only if you actually got it</li>
     * </ul>
     *
     * @param lock the lock to experiment with (free on entry)
     * @return {@code true} if the write lock could be acquired
     */
    public boolean canWriteWhileHoldingRead(ReentrantReadWriteLock lock) {
        throw new UnsupportedOperationException("TODO: implement canWriteWhileHoldingRead");
    }

    /**
     * Acquire the write lock, then <b>downgrade</b>: obtain the read lock while
     * still holding the write lock, and only then release the write lock.
     * Return {@code true} if, right after the downgrade, the current thread holds
     * exactly one read lock and nobody holds the write lock. Release the read
     * lock before returning.
     *
     * <p>Examples:
     * <pre>
     * downgradeToReadLock(new ReentrantReadWriteLock()) → true
     * (lock is completely free afterwards)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Order matters: read first, then release write</li>
     *   <li>{@code isWriteLocked()} and {@code getReadHoldCount()} describe the state</li>
     * </ul>
     *
     * @param lock the lock to experiment with (free on entry)
     * @return {@code true} if the downgrade left a single read hold and no write hold
     */
    public boolean downgradeToReadLock(ReentrantReadWriteLock lock) {
        throw new UnsupportedOperationException("TODO: implement downgradeToReadLock");
    }
}
