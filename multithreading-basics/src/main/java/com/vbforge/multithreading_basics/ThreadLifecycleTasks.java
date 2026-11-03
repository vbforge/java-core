package com.vbforge.multithreading_basics;

/**
 * Tasks for the thread lifecycle. Implement every method so that
 * {@code ThreadLifecycleTasksTest} goes green (remove {@code @Disabled} per task).
 */
public class ThreadLifecycleTasks {

    /**
     * Starts {@code task} on a new thread, waits at most {@code timeoutMillis} for it
     * and reports whether it finished in time.
     *
     * <pre>
     * Examples:
     *   finishesWithin(() -> { }, 2000)                       → true
     *   finishesWithin(() -> sleep(300ms), 20)                → false
     * </pre>
     *
     * Hints:
     * - Thread has a join variant with a timeout
     * - After a timed join, ask the thread whether it is still alive
     *
     * @param task          the work to run
     * @param timeoutMillis maximum time to wait
     * @return true if the thread had terminated when the wait ended
     * @throws InterruptedException if interrupted while waiting
     */
    public boolean finishesWithin(Runnable task, long timeoutMillis) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement finishesWithin");
    }

    /**
     * Starts a thread that sleeps for a long time, waits until that thread is
     * actually sleeping, records its state, then wakes it up, waits for it to end
     * and returns the recorded state.
     *
     * <pre>
     * Examples:
     *   observeStateWhileSleeping() → TIMED_WAITING
     * </pre>
     *
     * Hints:
     * - The state changes asynchronously; poll getState() instead of reading it once
     * - Do not leave the sleeping thread behind — wake it and join it
     *
     * @return the state observed while the thread was sleeping
     * @throws InterruptedException if the caller is interrupted
     */
    public Thread.State observeStateWhileSleeping() throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement observeStateWhileSleeping");
    }

    /**
     * Starts a thread that waits on {@code lock} (no timeout), waits until it is in
     * that waiting state, records its state, then wakes it up, waits for it to end and
     * returns the recorded state.
     *
     * <pre>
     * Examples:
     *   observeStateWhileWaiting(new Object()) → WAITING
     * </pre>
     *
     * Hints:
     * - The wait/notify methods may only be called while holding the object's monitor
     * - A notification sent before the thread started waiting is lost, so poll the state first
     *
     * @param lock the monitor object to wait on (not null)
     * @return the state observed while the thread was waiting
     * @throws InterruptedException if the caller is interrupted
     */
    public Thread.State observeStateWhileWaiting(Object lock) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement observeStateWhileWaiting");
    }

    /**
     * Produces a thread that is blocked on a monitor, records its state and cleans up.
     * The calling thread holds a private monitor while a second thread tries to enter the
     * same monitor. Once the second thread reports the blocked state, release the monitor,
     * wait for the second thread to end and return the recorded state.
     *
     * <pre>
     * Examples:
     *   observeStateWhileBlocked() → BLOCKED
     * </pre>
     *
     * Hints:
     * - Both threads must use the very same object for their synchronized blocks
     * - Start the second thread while still holding the monitor
     *
     * @return the state observed while the thread was blocked
     * @throws InterruptedException if the caller is interrupted
     */
    public Thread.State observeStateWhileBlocked() throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement observeStateWhileBlocked");
    }

    /**
     * Runs {@code task} on a new thread to completion, then tries to start that SAME
     * thread object again. Returns whether the second start succeeded.
     *
     * <pre>
     * Examples:
     *   tryRestart(task) → false   (task has run exactly once)
     * </pre>
     *
     * Hints:
     * - The JVM reports an illegal restart with an unchecked exception
     * - Do not let that exception escape from your method
     *
     * @param task the work to run
     * @return true if the thread could be started a second time (it never can)
     * @throws InterruptedException if interrupted while waiting
     */
    public boolean tryRestart(Runnable task) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement tryRestart");
    }
}
