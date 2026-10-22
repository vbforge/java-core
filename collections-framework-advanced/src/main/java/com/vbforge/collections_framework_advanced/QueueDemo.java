package com.vbforge.collections_framework_advanced;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Demonstrates the {@link Queue} interface: a collection for HOLDING ELEMENTS BEFORE PROCESSING,
 * normally in first-in-first-out (FIFO) order.
 *
 * <p>Why it matters: a {@code List} lets you reach any index, which is the wrong tool for
 * "serve the oldest request first". A {@code Queue} exposes only the head and the tail, so the
 * intent is obvious and the implementation ({@link ArrayDeque}) can make both ends cheap.
 *
 * <p>Complexity of {@code ArrayDeque} as a queue: {@code offer}, {@code poll}, {@code peek} are
 * O(1) (amortised for {@code offer}). Prefer it over {@code LinkedList} — less memory, better locality.
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code QueueDemoTest}
 * can verify every claim.
 */
public class QueueDemo {

    // ── 1. FIFO order ─────────────────────────────────────────────────────────
    /**
     * Elements leave a queue in the order they entered: first in, first out.
     * {@code offer} adds at the TAIL, {@code poll} removes from the HEAD.
     */
    public static List<String> fifoDemo(List<String> items) {
        Queue<String> queue = new ArrayDeque<>();
        for (String item : items) {
            queue.offer(item);
        }
        List<String> served = new ArrayList<>();
        while (!queue.isEmpty()) {
            served.add(queue.poll());
        }
        return served;
    }


    // ── 2. Two method families: special value vs exception ────────────────────
    /**
     * Every queue operation comes in two flavours:
     * <pre>
     *            throws on failure      returns special value on failure
     *   insert   add(e)                 offer(e)    → false when full
     *   remove   remove()               poll()      → null when empty
     *   examine  element()              peek()      → null when empty
     * </pre>
     * Rule of thumb: use the special-value family ({@code offer/poll/peek}) when "empty" is a
     * normal situation, and the throwing family when it would be a bug.
     */
    public static String peekDemo(Queue<String> queue) {
        return queue.peek();
    }

    /** Throws {@link NoSuchElementException} on an empty queue. */
    public static String elementDemo(Queue<String> queue) {
        return queue.element();
    }

    public static String pollDemo(Queue<String> queue) {
        return queue.poll();
    }

    /** Throws {@link NoSuchElementException} on an empty queue. */
    public static String removeDemo(Queue<String> queue) {
        return queue.remove();
    }


    // ── 3. Bounded queues: offer vs add ───────────────────────────────────────
    /**
     * A bounded queue ({@link ArrayBlockingQueue} with capacity 2 here) can be FULL.
     * {@code offer} reports that politely with {@code false}.
     */
    public static boolean offerToFullQueueDemo() {
        Queue<String> queue = new ArrayBlockingQueue<>(2);
        queue.offer("a");
        queue.offer("b");
        return queue.offer("c");
    }

    /** {@code add} on a full bounded queue throws {@link IllegalStateException}. */
    public static boolean addToFullQueueDemo() {
        Queue<String> queue = new ArrayBlockingQueue<>(2);
        queue.add("a");
        queue.add("b");
        return queue.add("c");
    }


    // ── 4. Null elements ──────────────────────────────────────────────────────
    /**
     * <b>Gotcha:</b> {@code ArrayDeque} rejects {@code null} with {@link NullPointerException}.
     * {@code LinkedList} accepts it — and then {@code poll()}/{@code peek()} returning {@code null}
     * no longer tells you whether the queue is empty or just holds a {@code null}.
     * Never put {@code null} into a queue.
     */
    public static boolean offerNullDemo(Queue<String> queue) {
        return queue.offer(null);
    }


    // ── 5. Queue as a work list: round robin ──────────────────────────────────
    /**
     * Take the next participant from the head, let them act, and put them back at the tail.
     * Everybody gets the same number of turns, in the same order.
     */
    public static List<String> roundRobinDemo(List<String> names, int turns) {
        Queue<String> queue = new ArrayDeque<>(names);
        List<String> order = new ArrayList<>();
        for (int i = 0; i < turns; i++) {
            String next = queue.poll();
            if (next == null) {
                break;
            }
            order.add(next);
            queue.offer(next);
        }
        return order;
    }


    // ── 6. Iterating vs draining ──────────────────────────────────────────────
    /**
     * A for-each loop visits elements from head to tail WITHOUT removing them.
     */
    public static List<Integer> iterateDemo(Queue<Integer> queue) {
        List<Integer> seen = new ArrayList<>();
        for (Integer number : queue) {
            seen.add(number);
        }
        return seen;
    }

    /**
     * Draining ({@code poll} until it returns {@code null}) consumes the queue: afterwards it is empty.
     */
    public static List<Integer> drainDemo(Queue<Integer> queue) {
        List<Integer> drained = new ArrayList<>();
        Integer next;
        while ((next = queue.poll()) != null) {
            drained.add(next);
        }
        return drained;
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. FIFO ===");
        System.out.println(fifoDemo(Arrays.asList("first", "second", "third")));

        System.out.println("---");
        System.out.println("=== 2. special value vs exception ===");
        Queue<String> empty = new ArrayDeque<>();
        System.out.println("peek()    -> " + peekDemo(empty));
        System.out.println("poll()    -> " + pollDemo(empty));
        try {
            elementDemo(empty);
        } catch (NoSuchElementException e) {
            System.out.println("element() -> NoSuchElementException");
        }
        try {
            removeDemo(empty);
        } catch (NoSuchElementException e) {
            System.out.println("remove()  -> NoSuchElementException");
        }

        System.out.println("---");
        System.out.println("=== 3. bounded queue ===");
        System.out.println("offer on full -> " + offerToFullQueueDemo());
        try {
            addToFullQueueDemo();
        } catch (IllegalStateException e) {
            System.out.println("add on full   -> IllegalStateException: " + e.getMessage());
        }

        System.out.println("---");
        System.out.println("=== 4. nulls ===");
        try {
            offerNullDemo(new ArrayDeque<String>());
        } catch (NullPointerException e) {
            System.out.println("ArrayDeque rejects null");
        }
        Queue<String> linked = new LinkedList<>();
        offerNullDemo(linked);
        System.out.println("LinkedList accepts null: size=" + linked.size() + ", peek()=" + linked.peek());

        System.out.println("---");
        System.out.println("=== 5. round robin ===");
        System.out.println(roundRobinDemo(Arrays.asList("A", "B", "C"), 7));

        System.out.println("---");
        System.out.println("=== 6. iterate vs drain ===");
        Queue<Integer> numbers = new ArrayDeque<>(Arrays.asList(1, 2, 3));
        System.out.println("iterate: " + iterateDemo(numbers) + " size=" + numbers.size());
        System.out.println("drain:   " + drainDemo(numbers) + " size=" + numbers.size());
    }
}
