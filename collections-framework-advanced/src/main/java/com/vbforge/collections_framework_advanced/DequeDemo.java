package com.vbforge.collections_framework_advanced;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Demonstrates {@link Deque} ("deck"): a double-ended queue that can add and remove at BOTH ends.
 *
 * <p>Why it matters: one interface covers three jobs — a queue (FIFO), a stack (LIFO) and a
 * "work from both sides" structure. {@link ArrayDeque} is the standard implementation and the
 * recommended replacement for the legacy {@link Stack} class.
 *
 * <p>Complexity of {@code ArrayDeque}: add/remove/peek at either end is O(1) (amortised for adds).
 * Searching ({@code contains}, {@code removeFirstOccurrence}) is O(n).
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code DequeDemoTest}
 * can verify every claim.
 */
public class DequeDemo {

    // ── 1. Working at both ends ───────────────────────────────────────────────
    /**
     * {@code addFirst}/{@code addLast} insert at the head/tail; {@code pollFirst}/{@code pollLast}
     * remove from them. Returns the two polled elements followed by what is left.
     */
    public static List<String> bothEndsDemo() {
        Deque<String> deque = new ArrayDeque<>();
        deque.addLast("b");
        deque.addFirst("a");
        deque.addLast("c");              // [a, b, c]

        List<String> events = new ArrayList<>();
        events.add(deque.pollFirst());   // "a"
        events.add(deque.pollLast());    // "c"
        events.add(deque.toString());    // "[b]"
        return events;
    }


    // ── 2. Deque as a stack (LIFO) ────────────────────────────────────────────
    /**
     * {@code push} adds at the HEAD, {@code pop} removes from the HEAD, {@code peek} looks at the head:
     * the last element pushed is the first one popped. Pushing 1,2,3 and popping gives 3,2,1.
     */
    public static List<Integer> stackDemo(List<Integer> items) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (Integer item : items) {
            stack.push(item);
        }
        List<Integer> popped = new ArrayList<>();
        while (!stack.isEmpty()) {
            popped.add(stack.pop());
        }
        return popped;
    }


    // ── 3. Iteration order: Deque vs legacy Stack ─────────────────────────────
    /**
     * <b>Gotcha:</b> because {@code push} adds at the HEAD, iterating an {@code ArrayDeque} that is
     * used as a stack visits the TOP of the stack first — the reverse of the push order.
     */
    public static List<Integer> dequeStackIterationDemo(List<Integer> items) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (Integer item : items) {
            stack.push(item);
        }
        return new ArrayList<>(stack);
    }

    /**
     * The legacy {@link Stack} (a synchronised subclass of {@code Vector}) iterates the OTHER way:
     * bottom first, i.e. in push order. Mixing the two up is a classic bug — another reason to
     * prefer {@code ArrayDeque} and its single, consistent head-based model.
     */
    public static List<Integer> legacyStackIterationDemo(List<Integer> items) {
        Stack<Integer> stack = new Stack<>();
        for (Integer item : items) {
            stack.push(item);
        }
        return new ArrayList<>(stack);
    }


    // ── 4. Empty deque behaviour ──────────────────────────────────────────────
    /** The "special value" family returns {@code null} on an empty deque. */
    public static Integer peekEmptyDemo() {
        return new ArrayDeque<Integer>().peek();
    }

    /** The "exception" family — {@code pop}, {@code removeFirst}, {@code getFirst} — throws {@link NoSuchElementException}. */
    public static Integer popEmptyDemo() {
        return new ArrayDeque<Integer>().pop();
    }


    // ── 5. descendingIterator and removeFirstOccurrence ───────────────────────
    /**
     * {@code descendingIterator()} walks from tail to head without copying or reversing anything.
     */
    public static List<Integer> descendingDemo(List<Integer> items) {
        Deque<Integer> deque = new ArrayDeque<>(items);
        List<Integer> reversed = new ArrayList<>();
        Iterator<Integer> iterator = deque.descendingIterator();
        while (iterator.hasNext()) {
            reversed.add(iterator.next());
        }
        return reversed;
    }

    /**
     * {@code removeFirstOccurrence} removes only the FIRST match scanning from the head
     * ({@code remove(Object)} is the same thing); {@code removeIf} would remove all matches.
     */
    public static List<String> removeFirstOccurrenceDemo(List<String> items, String target) {
        Deque<String> deque = new ArrayDeque<>(items);
        deque.removeFirstOccurrence(target);
        return new ArrayList<>(deque);
    }


    // ── 6. Both ends at once: palindrome check ────────────────────────────────
    /**
     * Compare the first and the last character, discard both, repeat. Only letters and digits
     * count, case is ignored. A deque makes "look at both ends" natural.
     */
    public static boolean isPalindromeDemo(String text) {
        Deque<Character> chars = new ArrayDeque<>();
        for (char c : text.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                chars.addLast(Character.toLowerCase(c));
            }
        }
        while (chars.size() > 1) {
            if (!chars.pollFirst().equals(chars.pollLast())) {
                return false;
            }
        }
        return true;
    }


    // ── 7. Null elements ──────────────────────────────────────────────────────
    /**
     * {@code ArrayDeque} rejects {@code null} (it would be ambiguous with the "empty" return value)
     * and throws {@link NullPointerException}.
     */
    public static void pushNullDemo() {
        new ArrayDeque<String>().push(null);
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. both ends ===");
        System.out.println(bothEndsDemo());

        System.out.println("---");
        System.out.println("=== 2. stack ===");
        System.out.println(stackDemo(Arrays.asList(1, 2, 3)));

        System.out.println("---");
        System.out.println("=== 3. iteration order ===");
        System.out.println("ArrayDeque: " + dequeStackIterationDemo(Arrays.asList(1, 2, 3)));
        System.out.println("Stack:      " + legacyStackIterationDemo(Arrays.asList(1, 2, 3)));

        System.out.println("---");
        System.out.println("=== 4. empty deque ===");
        System.out.println("peek() -> " + peekEmptyDemo());
        try {
            popEmptyDemo();
        } catch (NoSuchElementException e) {
            System.out.println("pop()  -> NoSuchElementException");
        }

        System.out.println("---");
        System.out.println("=== 5. descendingIterator / removeFirstOccurrence ===");
        System.out.println(descendingDemo(Arrays.asList(1, 2, 3)));
        System.out.println(removeFirstOccurrenceDemo(Arrays.asList("a", "b", "a"), "a"));

        System.out.println("---");
        System.out.println("=== 6. palindrome ===");
        System.out.println(isPalindromeDemo("A man, a plan, a canal: Panama"));
        System.out.println(isPalindromeDemo("deque"));

        System.out.println("---");
        System.out.println("=== 7. null ===");
        try {
            pushNullDemo();
        } catch (NullPointerException e) {
            System.out.println("ArrayDeque rejects null");
        }
    }
}
