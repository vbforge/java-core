package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.mini.MiniArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Demonstrates how {@link ArrayList} works inside: ONE backing array that is replaced by a bigger one
 * when it is full.
 *
 * <p>The real array is private, so the growth demos use {@link MiniArrayList} — a small re-implementation of
 * the same strategy with counters. The behaviour demos at the end use the real {@code ArrayList}.
 *
 * <p>Complexity (real {@code ArrayList}):
 * <pre>
 *   get(i) / set(i)        O(1)             direct array access
 *   add(e)                 O(1) amortised   occasionally O(n) when the array must grow
 *   add(i, e) / remove(i)  O(n)             every element behind the index shifts
 *   contains / indexOf     O(n)             linear scan
 * </pre>
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code ArrayListInternalsDemoTest}
 * can verify every claim.
 */
public class ArrayListInternalsDemo {

    // ── 1. The backing array and its growth ───────────────────────────────────
    /**
     * An empty list owns no array at all. The first {@code add} allocates capacity 10; whenever the
     * array is full the capacity grows by 50 % ({@code old + (old >> 1)}): 10, 15, 22, 33, 49, 73, ...
     * Returns every distinct capacity seen while appending {@code adds} elements.
     */
    public static List<Integer> capacityHistoryDemo(int adds) {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        List<Integer> capacities = new ArrayList<>();
        for (int i = 0; i < adds; i++) {
            list.add(i);
            if (capacities.isEmpty() || capacities.get(capacities.size() - 1) != list.capacity()) {
                capacities.add(list.capacity());
            }
        }
        return capacities;
    }


    // ── 2. Why add(e) is O(1) AMORTISED ───────────────────────────────────────
    /**
     * Growing copies the whole array — an O(n) step — but because the capacity grows GEOMETRICALLY
     * the copies are rare. Returns the total number of elements copied while growing during {@code n} appends.
     * It stays below {@code 3 * n}: on average each append pays for less than three element copies.
     */
    public static int elementsCopiedWhileGrowingDemo(int n) {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        return list.copyCount();
    }

    /** How many times the array had to be (re)allocated for {@code n} appends — it grows like log(n). */
    public static int reallocationsDemo(int n) {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        return list.growCount();
    }


    // ── 3. Inserting and removing in the middle shifts elements ───────────────
    /** Elements shifted by ONE insert at {@code index} into a list of {@code size} elements: {@code size - index}. */
    public static int shiftsForInsertDemo(int size, int index) {
        MiniArrayList<Integer> list = filled(size);
        int before = list.shiftCount();
        list.add(index, -1);
        return list.shiftCount() - before;
    }

    /** Elements shifted by removing the FIRST element: all the others. */
    public static int shiftsForRemoveFirstDemo(int size) {
        MiniArrayList<Integer> list = filled(size);
        int before = list.shiftCount();
        list.remove(0);
        return list.shiftCount() - before;
    }

    /** Appending at the end shifts nothing — that is why {@code add(e)} is cheap and {@code add(0, e)} is not. */
    public static int shiftsForAppendDemo(int size) {
        MiniArrayList<Integer> list = filled(size);
        int before = list.shiftCount();
        list.add(-1);
        return list.shiftCount() - before;
    }

    /**
     * Inserting {@code count} elements one after another at the FRONT costs 0 + 1 + 2 + ... + (count - 1)
     * shifts in total — quadratic, O(n²).
     */
    public static int totalShiftsForFrontInsertsDemo(int count) {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(0, i);
        }
        return list.shiftCount();
    }

    private static MiniArrayList<Integer> filled(int size) {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }


    // ── 4. Fail-fast iterators (modCount) ─────────────────────────────────────
    /**
     * Every structural change (add/remove) increments a hidden {@code modCount}. An iterator remembers the value
     * it started with and throws {@link ConcurrentModificationException} when it finds a different one.
     * Removing inside a for-each loop therefore usually fails.
     */
    public static List<Integer> removeWhileIteratingDemo(List<Integer> input) {
        List<Integer> list = new ArrayList<>(input);
        for (Integer value : list) {
            if (value % 2 == 0) {
                list.remove(value);
            }
        }
        return list;
    }

    /**
     * <b>Gotcha:</b> the check happens in {@code next()}, and the loop condition {@code hasNext()} is just
     * {@code cursor != size}. Removing the SECOND-TO-LAST element makes the loop end silently, with no exception
     * — which hides the bug. Input {@code [1, 2, 3, 4]}: removing 3 ends the loop before 4 is visited.
     */
    public static List<Integer> removeSecondToLastWhileIteratingDemo(List<Integer> input) {
        List<Integer> list = new ArrayList<>(input);
        for (Integer value : list) {
            if (value.equals(list.get(list.size() - 2))) {
                list.remove(value);
            }
        }
        return list;
    }

    /** The correct way: {@code Iterator.remove()} (or {@code removeIf}) keeps the iterator's expected modCount in sync. */
    public static List<Integer> removeEvensWithIteratorDemo(List<Integer> input) {
        List<Integer> list = new ArrayList<>(input);
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() % 2 == 0) {
                iterator.remove();
            }
        }
        return list;
    }


    // ── 5. remove(int index) vs remove(Object) ────────────────────────────────
    /** {@code remove(int)} takes an INDEX and returns the removed element. */
    public static List<Integer> removeByIndexDemo(List<Integer> input, int index) {
        List<Integer> list = new ArrayList<>(input);
        list.remove(index);
        return list;
    }

    /**
     * <b>Gotcha:</b> on a {@code List<Integer>} the call {@code list.remove(7)} picks the {@code remove(int index)}
     * overload. To remove the VALUE 7 you must box it: {@code list.remove(Integer.valueOf(7))} selects {@code remove(Object)}.
     */
    public static List<Integer> removeByValueDemo(List<Integer> input, int value) {
        List<Integer> list = new ArrayList<>(input);
        list.remove(Integer.valueOf(value));
        return list;
    }


    // ── 6. subList is a VIEW ──────────────────────────────────────────────────
    /**
     * {@code subList(from, to)} does not copy: it is a window onto the same array. Writing through the view changes
     * the parent list.
     */
    public static List<Integer> writeThroughSubListDemo(List<Integer> input) {
        List<Integer> list = new ArrayList<>(input);
        list.subList(0, 2).set(0, 99);
        return list;
    }

    /** A structural change of the parent invalidates the view: using it afterwards throws {@link ConcurrentModificationException}. */
    public static int useSubListAfterParentChangeDemo(List<Integer> input) {
        List<Integer> list = new ArrayList<>(input);
        List<Integer> view = list.subList(0, 2);
        list.add(-1);
        return view.size();
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. capacity growth ===");
        System.out.println(capacityHistoryDemo(60));

        System.out.println("---");
        System.out.println("=== 2. amortised O(1) ===");
        System.out.println("1000 appends -> reallocations: " + reallocationsDemo(1000)
                + ", elements copied: " + elementsCopiedWhileGrowingDemo(1000));

        System.out.println("---");
        System.out.println("=== 3. shifting ===");
        System.out.println("insert at 0 in 1000 elements : " + shiftsForInsertDemo(1000, 0) + " shifts");
        System.out.println("append to 1000 elements      : " + shiftsForAppendDemo(1000) + " shifts");
        System.out.println("100 inserts at the front     : " + totalShiftsForFrontInsertsDemo(100) + " shifts");

        System.out.println("---");
        System.out.println("=== 4. fail-fast iterators ===");
        try {
            removeWhileIteratingDemo(Arrays.asList(1, 2, 3, 4));
        } catch (ConcurrentModificationException e) {
            System.out.println("remove in for-each -> ConcurrentModificationException");
        }
        System.out.println("second-to-last quirk: " + removeSecondToLastWhileIteratingDemo(Arrays.asList(1, 2, 3, 4)));
        System.out.println("with Iterator.remove: " + removeEvensWithIteratorDemo(Arrays.asList(1, 2, 3, 4)));

        System.out.println("---");
        System.out.println("=== 5. remove(int) vs remove(Object) ===");
        System.out.println(removeByIndexDemo(Arrays.asList(5, 6, 7), 1));
        System.out.println(removeByValueDemo(Arrays.asList(5, 6, 7), 7));

        System.out.println("---");
        System.out.println("=== 6. subList view ===");
        System.out.println(writeThroughSubListDemo(Arrays.asList(1, 2, 3)));
        try {
            useSubListAfterParentChangeDemo(Arrays.asList(1, 2, 3));
        } catch (ConcurrentModificationException e) {
            System.out.println("view after parent change -> ConcurrentModificationException");
        }
    }
}
