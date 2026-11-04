package com.vbforge.concurrent_collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Demo: {@link CopyOnWriteArrayList} and {@link CopyOnWriteArraySet} — collections
 * built for "read a lot, write rarely".
 *
 * <p>Every claim in a comment is either printed by {@link #main(String[])} or
 * asserted in {@code CopyOnWriteDemoTest}.
 */
public class CopyOnWriteDemo {

    public static void main(String[] args) throws InterruptedException {
        snapshotIterationDemo();
        System.out.println("---");
        readOnlyIteratorDemo();
        System.out.println("---");
        addIfAbsentDemo();
        System.out.println("---");
        checkThenActRaceDemo();
        System.out.println("---");
        copyOnWriteArraySetDemo();
        System.out.println("---");
        listenerListDemo();
    }

    // =========================================================================
    // 1. Snapshot iteration
    // =========================================================================

    /*
     * WHAT
     *   Every write (add, set, remove, clear, ...) builds a brand-new copy of
     *   the internal array and swaps it in. An iterator is bound to the array
     *   that existed when the iterator was created - a SNAPSHOT.
     *
     * WHY
     *   Readers never need a lock and can never see a half-finished update.
     *   Iterating can never throw ConcurrentModificationException, even if the
     *   loop body itself modifies the list - which is what makes it ideal for
     *   lists of listeners / observers.
     *
     * COMPLEXITY
     *   - get(i), size():            O(1)
     *   - iterator():                O(1) - it only grabs the current array
     *   - add / set / remove:        O(n) - the whole array is copied
     *   - contains / indexOf:        O(n)
     *   - space: a write temporarily needs a second array of the same size
     *
     * GOTCHA
     *   The iterator never sees later changes - not even changes made by the
     *   same thread inside the loop. If you need the "latest" content, call
     *   list.iterator() again (or use get(i)).
     */
    public static void snapshotIterationDemo() {
        List<String> arrayList = new ArrayList<>(Arrays.asList("a", "b", "c"));
        boolean arrayListFailed = false;
        try {
            for (String s : arrayList) {
                arrayList.add(s + "'");
            }
        } catch (ConcurrentModificationException e) {
            arrayListFailed = true;
        }
        System.out.println("ArrayList, add while iterating          -> ConcurrentModificationException: "
                + arrayListFailed);

        CopyOnWriteArrayList<String> cow = new CopyOnWriteArrayList<>(Arrays.asList("a", "b", "c"));
        List<String> visited = new ArrayList<>();
        for (String s : cow) {
            cow.add(s + "'");
            visited.add(s);
        }
        System.out.println("CopyOnWriteArrayList, add while looping -> visited " + visited + ", list = " + cow);

        Iterator<String> oldSnapshot = cow.iterator();
        cow.clear();
        List<String> seenAfterClear = new ArrayList<>();
        while (oldSnapshot.hasNext()) {
            seenAfterClear.add(oldSnapshot.next());
        }
        System.out.println("iterator taken before clear()           -> still sees " + seenAfterClear
                + ", list = " + cow);
    }

    // =========================================================================
    // 2. The iterator is read-only
    // =========================================================================

    /*
     * WHAT
     *   Iterator.remove() on a CopyOnWriteArrayList iterator throws
     *   UnsupportedOperationException.
     *
     * WHY
     *   The iterator walks a private snapshot, so "remove the current element"
     *   would have no defined effect on the real list. Modify the LIST instead:
     *   list.remove(x) or list.removeIf(...).
     *
     * COMPLEXITY
     *   - removeIf: O(n) - one copy for the whole call, not one per removed element
     *
     * GOTCHA
     *   Code that uses the "iterator + it.remove()" idiom compiles fine and then
     *   fails at runtime the first time it runs against a CopyOnWriteArrayList.
     */
    public static void readOnlyIteratorDemo() {
        CopyOnWriteArrayList<Integer> numbers = new CopyOnWriteArrayList<>(Arrays.asList(1, 2, 3, 4));

        Iterator<Integer> it = numbers.iterator();
        it.next();
        try {
            it.remove();
        } catch (UnsupportedOperationException e) {
            System.out.println("Iterator.remove()       -> UnsupportedOperationException");
        }

        boolean changed = numbers.removeIf(n -> n % 2 == 0);
        System.out.println("removeIf(even)          -> " + changed + ", list = " + numbers);
    }

    // =========================================================================
    // 3. addIfAbsent / addAllAbsent
    // =========================================================================

    /*
     * WHAT
     *   CopyOnWriteArrayList has two methods that other Lists do not:
     *     addIfAbsent(e)        adds e unless the list already contains it; returns true if added
     *     addAllAbsent(coll)    adds every element not yet present; returns HOW MANY were added
     *
     * WHY
     *   "add if not already there" is a very common rule. Doing it with
     *   contains() + add() is two steps (see the next section); these methods
     *   do it as ONE atomic step.
     *
     * COMPLEXITY
     *   - addIfAbsent: O(n) - it has to scan for the element
     *   - addAllAbsent: O(n * m) for m candidates, still a single array copy
     *
     * GOTCHA
     *   addAllAbsent also de-duplicates the candidates among themselves:
     *   addAllAbsent(["x", "x"]) on an empty list adds ONE x.
     */
    public static void addIfAbsentDemo() {
        CopyOnWriteArrayList<String> tags = new CopyOnWriteArrayList<>();

        boolean first = tags.addIfAbsent("java");
        boolean second = tags.addIfAbsent("java");
        System.out.println("addIfAbsent twice        -> " + first + " then " + second + ", list = " + tags);

        int added = tags.addAllAbsent(Arrays.asList("java", "maven", "maven", "junit"));
        System.out.println("addAllAbsent(4 candidates) -> added " + added + ", list = " + tags);
    }

    // =========================================================================
    // 4. The check-then-act race (and its cure)
    // =========================================================================

    /*
     * WHAT
     *   "if (!list.contains(x)) list.add(x)" is thread-safe call by call, but
     *   NOT as a sequence: another thread can add x between the two calls.
     *
     * WHY
     *   Same lesson as with ConcurrentHashMap: a thread-safe collection does not
     *   make a multi-step algorithm thread-safe. Use the single compound method.
     *
     * HOW THE DEMO WORKS
     *   A CyclicBarrier(2) sits between the check and the add, so both threads
     *   are guaranteed to see "absent" before either one adds. The duplicate
     *   appears every run.
     *
     * COMPLEXITY
     *   - see section 3
     *
     * GOTCHA
     *   Making the list thread-safe is not the same as making your code
     *   thread-safe.
     */
    public static void checkThenActRaceDemo() throws InterruptedException {
        CopyOnWriteArrayList<String> broken = new CopyOnWriteArrayList<>();
        CyclicBarrier bothHaveChecked = new CyclicBarrier(2);
        runInParallel(2, i -> {
            if (!broken.contains("x")) {      // check
                bothHaveChecked.await();       // ...both threads are now between check and act
                broken.add("x");               // act
            }
        });
        System.out.println("contains + add       -> " + broken);

        CopyOnWriteArrayList<String> safe = new CopyOnWriteArrayList<>();
        runInParallel(8, i -> safe.addIfAbsent("x"));
        System.out.println("addIfAbsent (8 threads) -> " + safe);
    }

    // =========================================================================
    // 5. CopyOnWriteArraySet
    // =========================================================================

    /*
     * WHAT
     *   A Set that is implemented on top of a CopyOnWriteArrayList: no
     *   duplicates, iteration in insertion order, snapshot iterators.
     *
     * WHY
     *   When the set is small and read-mostly (a registry of handlers, a list
     *   of allowed values) it gives you thread-safety, a Set's no-duplicates
     *   rule AND a predictable order - which a hash-based set cannot offer.
     *
     * COMPLEXITY
     *   - add / contains / remove: O(n)   (it is an array underneath, not a hash table)
     *
     * GOTCHA
     *   Do not use it for big sets: every operation is linear. For large
     *   concurrent sets use ConcurrentHashMap.newKeySet() instead.
     */
    public static void copyOnWriteArraySetDemo() {
        Set<String> handlers = new CopyOnWriteArraySet<>();
        boolean added1 = handlers.add("logger");
        boolean added2 = handlers.add("metrics");
        boolean added3 = handlers.add("logger");
        System.out.println("add logger, metrics, logger -> " + added1 + ", " + added2 + ", " + added3
                + "; set = " + handlers);
    }

    // =========================================================================
    // 6. The classic use case: a listener list
    // =========================================================================

    /*
     * WHAT
     *   A list of listeners that is notified often, changed rarely, and whose
     *   listeners may register or unregister THEMSELVES while being notified.
     *
     * WHY
     *   With an ArrayList, a listener that removes itself during notification
     *   would trigger ConcurrentModificationException. With a
     *   CopyOnWriteArrayList the notification loop walks a snapshot, so it
     *   simply works - and a listener added during a round is called from the
     *   NEXT round on.
     *
     * COMPLEXITY
     *   - notify: O(listeners); register / unregister: O(listeners) each
     *
     * GOTCHA
     *   A listener removed during a round is STILL called in that round if it
     *   comes later in the snapshot. "Removed" only takes effect for the next
     *   iterator.
     */
    public static void listenerListDemo() {
        CopyOnWriteArrayList<Consumer<String>> listeners = new CopyOnWriteArrayList<>();
        List<String> log = new ArrayList<>();

        Consumer<String> oneShot = new Consumer<String>() {
            @Override
            public void accept(String event) {
                log.add("oneShot got " + event);
                listeners.remove(this);              // unregister myself while being notified
            }
        };
        listeners.add(event -> log.add("logger got " + event));
        listeners.add(oneShot);

        for (Consumer<String> listener : listeners) {
            listener.accept("event-1");
        }
        for (Consumer<String> listener : listeners) {
            listener.accept("event-2");
        }
        System.out.println("log = " + log);
    }

    // =========================================================================
    // Helper: start N threads together, wait for all of them
    // =========================================================================

    @FunctionalInterface
    private interface Body {
        void run(int threadIndex) throws Exception;
    }

    private static void runInParallel(int threads, Body body) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int index = i;
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                    body.run(index);
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                }
            }, "worker-" + i);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
        if (failure.get() != null) {
            throw new IllegalStateException("a worker thread failed", failure.get());
        }
    }
}
