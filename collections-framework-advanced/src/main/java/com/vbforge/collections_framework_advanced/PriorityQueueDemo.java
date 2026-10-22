package com.vbforge.collections_framework_advanced;

import com.vbforge.collections_framework_advanced.model.Ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Demonstrates {@link PriorityQueue}: a queue that always hands out the SMALLEST element first
 * (by natural order or by a {@link Comparator}), no matter in which order elements were added.
 *
 * <p>Why it matters: "serve the most urgent item next" is everywhere — task schedulers, Dijkstra,
 * "top K" problems, event simulations. Keeping a sorted list would cost O(n) per insert;
 * a binary heap does it in O(log n).
 *
 * <p>Complexity: {@code offer}/{@code poll} O(log n), {@code peek} O(1),
 * {@code contains}/{@code remove(Object)} O(n).
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code PriorityQueueDemoTest}
 * can verify every claim.
 */
public class PriorityQueueDemo {

    // ── 1. Natural order: smallest first ──────────────────────────────────────
    /**
     * {@code poll()} always returns the current minimum. Polling until empty therefore yields the
     * elements in ascending order (a "heap sort").
     */
    public static List<Integer> pollOrderDemo(List<Integer> items) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(items);
        List<Integer> polled = new ArrayList<>();
        while (!queue.isEmpty()) {
            polled.add(queue.poll());
        }
        return polled;
    }


    // ── 2. Gotcha: iteration order is NOT sorted ──────────────────────────────
    /**
     * <b>Gotcha:</b> only the HEAD of a priority queue is guaranteed to be the smallest.
     * The rest is stored as a binary heap, so a for-each loop, {@code toString()} or
     * {@code new ArrayList<>(queue)} shows the internal heap layout — NOT sorted order.
     * To read elements in order you must {@code poll()}.
     */
    public static List<Integer> iterationOrderDemo(List<Integer> items) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        for (Integer item : items) {
            queue.offer(item);
        }
        return new ArrayList<>(queue);
    }


    // ── 3. A Comparator changes "smallest": max-heap ──────────────────────────
    /**
     * Pass a {@link Comparator} to the constructor to define the order. Reversing the natural
     * order turns the min-heap into a max-heap: {@code poll()} returns the LARGEST element.
     */
    public static List<Integer> maxHeapDemo(List<Integer> items) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.<Integer>reverseOrder());
        queue.addAll(items);
        List<Integer> polled = new ArrayList<>();
        while (!queue.isEmpty()) {
            polled.add(queue.poll());
        }
        return polled;
    }


    // ── 4. Domain objects ─────────────────────────────────────────────────────
    /**
     * Elements are usually domain objects ordered by a field: here tickets by priority
     * (smaller number = more urgent). Returns the ticket ids in service order.
     */
    public static List<String> ticketOrderDemo(List<Ticket> tickets) {
        PriorityQueue<Ticket> queue = new PriorityQueue<>(Comparator.comparingInt(Ticket::getPriority));
        queue.addAll(tickets);
        List<String> ids = new ArrayList<>();
        while (!queue.isEmpty()) {
            ids.add(queue.poll().getId());
        }
        return ids;
    }


    // ── 5. Gotcha: ties are NOT served first-come-first-served ────────────────
    /**
     * <b>Gotcha:</b> a {@code PriorityQueue} is not stable. Elements with EQUAL priority may leave in
     * a different order than they arrived. All tickets here have priority 1, yet for the arrival
     * order a, b, c the service order is a, c, b.
     */
    public static List<String> unstableTiesDemo(List<String> arrivalOrder) {
        PriorityQueue<Ticket> queue = new PriorityQueue<>(Comparator.comparingInt(Ticket::getPriority));
        for (String id : arrivalOrder) {
            queue.offer(new Ticket(id, 1));
        }
        List<String> ids = new ArrayList<>();
        while (!queue.isEmpty()) {
            ids.add(queue.poll().getId());
        }
        return ids;
    }

    /**
     * The fix: add a tie-breaker that records the arrival sequence number. Equal priorities are now
     * served strictly first-come-first-served.
     */
    public static List<String> stableTiesDemo(List<String> arrivalOrder) {
        PriorityQueue<Entry> queue = new PriorityQueue<>(
                Comparator.comparingInt((Entry e) -> e.priority).thenComparingLong(e -> e.sequence));
        long sequence = 0;
        for (String id : arrivalOrder) {
            queue.offer(new Entry(id, 1, sequence++));
        }
        List<String> ids = new ArrayList<>();
        while (!queue.isEmpty()) {
            ids.add(queue.poll().id);
        }
        return ids;
    }

    /** Helper for {@link #stableTiesDemo(List)}: a ticket plus its arrival number. */
    private static final class Entry {
        private final String id;
        private final int priority;
        private final long sequence;

        private Entry(String id, int priority, long sequence) {
            this.id = id;
            this.priority = priority;
            this.sequence = sequence;
        }
    }


    // ── 6. Null and non-comparable elements ───────────────────────────────────
    /** {@code null} is never allowed — {@link NullPointerException}. */
    public static boolean offerNullDemo() {
        return new PriorityQueue<Integer>().offer(null);
    }

    /**
     * Without a Comparator the elements must be {@link Comparable}. Two plain {@code Object}s
     * cannot be ordered, so the second {@code add} fails with {@link ClassCastException}.
     */
    public static boolean addNonComparableDemo() {
        PriorityQueue<Object> queue = new PriorityQueue<>();
        queue.add(new Object());
        return queue.add(new Object());
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. natural order ===");
        System.out.println(pollOrderDemo(Arrays.asList(5, 3, 8, 1)));

        System.out.println("---");
        System.out.println("=== 2. iteration order is NOT sorted ===");
        System.out.println("for-each / toString: " + iterationOrderDemo(Arrays.asList(5, 3, 8, 1)));
        System.out.println("poll order:          " + pollOrderDemo(Arrays.asList(5, 3, 8, 1)));

        System.out.println("---");
        System.out.println("=== 3. max-heap ===");
        System.out.println(maxHeapDemo(Arrays.asList(5, 3, 8, 1)));

        System.out.println("---");
        System.out.println("=== 4. domain objects ===");
        System.out.println(ticketOrderDemo(Arrays.asList(
                new Ticket("T1", 3), new Ticket("T2", 1), new Ticket("T3", 2))));

        System.out.println("---");
        System.out.println("=== 5. ties ===");
        System.out.println("arrival a,b,c -> plain comparator: " + unstableTiesDemo(Arrays.asList("a", "b", "c")));
        System.out.println("arrival a,b,c -> with sequence:    " + stableTiesDemo(Arrays.asList("a", "b", "c")));

        System.out.println("---");
        System.out.println("=== 6. null / non-comparable ===");
        try {
            offerNullDemo();
        } catch (NullPointerException e) {
            System.out.println("null -> NullPointerException");
        }
        try {
            addNonComparableDemo();
        } catch (ClassCastException e) {
            System.out.println("Object -> ClassCastException");
        }
    }
}
