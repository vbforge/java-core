package com.vbforge.multithreading_basics;

/**
 * The six thread states of {@link Thread.State}: NEW, RUNNABLE, BLOCKED,
 * WAITING, TIMED_WAITING, TERMINATED — and how to provoke each one.
 *
 * Run: mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_basics.ThreadLifecycleDemo"
 */
public class ThreadLifecycleDemo {

    public static void main(String[] args) throws Exception {
        newAndTerminatedDemo();
        System.out.println("---");
        runnableDemo();
        System.out.println("---");
        timedWaitingDemo();
        System.out.println("---");
        waitingDemo();
        System.out.println("---");
        blockedDemo();
        System.out.println("---");
        noRestartDemo();
    }

    /**
     * Polls until {@code t} reaches {@code expected} (or ~5 s pass) and returns the last
     * state seen. State changes happen asynchronously, so a single getState() right after
     * start() proves nothing — always poll.
     */
    public static Thread.State awaitState(Thread t, Thread.State expected) throws InterruptedException {
        long deadline = System.nanoTime() + 5_000_000_000L;
        while (t.getState() != expected && System.nanoTime() < deadline) {
            Thread.sleep(1);
        }
        return t.getState();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 1. NEW and TERMINATED
    //
    // What:  NEW = constructed, start() not yet called (isAlive() is false).
    //        TERMINATED = run() has returned or thrown (isAlive() is false again).
    // Why:   these are the only two states in which a thread is not alive.
    // ─────────────────────────────────────────────────────────────────────
    static void newAndTerminatedDemo() throws InterruptedException {
        Thread t = new Thread(() -> { });
        System.out.println("before start: " + t.getState() + ", alive=" + t.isAlive());
        t.start();
        t.join();
        System.out.println("after join:   " + t.getState() + ", alive=" + t.isAlive());
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. RUNNABLE
    //
    // What:  "ready or running" — the JVM does not distinguish between actually
    //        executing on a CPU and waiting for the OS scheduler to give it one.
    // Gotcha: a thread blocked in OS-level I/O (e.g. reading a socket) still reports
    //         RUNNABLE. RUNNABLE does not mean "burning CPU".
    // ─────────────────────────────────────────────────────────────────────
    static void runnableDemo() throws InterruptedException {
        Thread spinner = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.yield();
            }
        });
        spinner.start();
        System.out.println("spinner: " + awaitState(spinner, Thread.State.RUNNABLE));
        spinner.interrupt();
        spinner.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. TIMED_WAITING
    //
    // What:  waiting with a deadline: sleep(ms), join(ms), wait(ms), parkNanos.
    // Why:   it leaves on its own when the time is up — or earlier on interrupt().
    // ─────────────────────────────────────────────────────────────────────
    static void timedWaitingDemo() throws InterruptedException {
        Thread sleeper = new Thread(() -> {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException ignored) {
                // woken up by the demo
            }
        });
        sleeper.start();
        System.out.println("sleeper: " + awaitState(sleeper, Thread.State.TIMED_WAITING));
        sleeper.interrupt();
        sleeper.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 4. WAITING
    //
    // What:  waiting with NO deadline: Object.wait(), join(), LockSupport.park().
    //        It leaves only when someone notifies / the joined thread ends / unpark().
    // Gotcha: wait() must be called while holding that object's monitor, otherwise
    //         IllegalMonitorStateException. wait() RELEASES the monitor while waiting.
    // ─────────────────────────────────────────────────────────────────────
    static void waitingDemo() throws InterruptedException {
        final Object lock = new Object();
        Thread waiter = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {
                    // not used in this demo
                }
            }
        });
        waiter.start();
        System.out.println("waiter: " + awaitState(waiter, Thread.State.WAITING));
        synchronized (lock) {
            lock.notifyAll();
        }
        waiter.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 5. BLOCKED
    //
    // What:  trying to enter a synchronized block/method whose monitor another thread holds.
    // Why:   BLOCKED is specific to intrinsic locks. A thread waiting for a
    //        java.util.concurrent Lock shows up as WAITING instead (locks module).
    // Gotcha: BLOCKED is NOT the same as WAITING: a blocked thread wants a monitor,
    //         a waiting thread wants a signal.
    // ─────────────────────────────────────────────────────────────────────
    static void blockedDemo() throws InterruptedException {
        final Object lock = new Object();
        Thread blocked;
        synchronized (lock) {
            blocked = new Thread(() -> {
                synchronized (lock) {
                    // enters only after main leaves its synchronized block
                }
            });
            blocked.start();
            System.out.println("blocked: " + awaitState(blocked, Thread.State.BLOCKED));
        }
        blocked.join();
    }

    // ─────────────────────────────────────────────────────────────────────
    // 6. No restart
    //
    // What:  TERMINATED is final. start() on a finished thread throws
    //        IllegalThreadStateException.
    // Why:   to run the same work again, create a new Thread (or use a pool).
    // ─────────────────────────────────────────────────────────────────────
    static void noRestartDemo() throws InterruptedException {
        Thread t = new Thread(() -> { });
        t.start();
        t.join();
        try {
            t.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("second start() -> " + e.getClass().getSimpleName());
        }
    }
}
