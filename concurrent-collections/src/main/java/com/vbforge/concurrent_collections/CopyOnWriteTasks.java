package com.vbforge.concurrent_collections;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Tasks for {@code CopyOnWriteDemo}. Implement each method, then remove
 * {@code @Disabled} from the matching {@code @Nested} class in
 * {@code CopyOnWriteTasksTest}.
 */
public class CopyOnWriteTasks {

    /**
     * Walk through {@code list} with a for-each loop. For every element
     * {@code e} you visit, <b>append</b> {@code e + suffix} to the same list
     * while the loop is still running. Return the elements you visited, in
     * order. Only the elements that were in the list when the loop started
     * are visited.
     *
     * <p>Examples:
     * <pre>
     * list = [a, b],  suffix = "!"
     * iterateWhileAppending(list, "!")  →  [a, b]          (the visited elements)
     *                                      list is now [a, b, a!, b!]
     *
     * list = [],  suffix = "!"
     * iterateWhileAppending(list, "!")  →  []              list is still []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The same loop on an {@code ArrayList} would throw {@code ConcurrentModificationException}</li>
     *   <li>The returned list is a separate, ordinary list - not the one you were given</li>
     * </ul>
     *
     * @param list   the list to walk and extend (never null)
     * @param suffix text appended to each visited element
     * @return the visited elements, in order
     */
    public List<String> iterateWhileAppending(CopyOnWriteArrayList<String> list, String suffix) {
        throw new UnsupportedOperationException("TODO: implement iterateWhileAppending");
    }

    /**
     * Add every candidate that is not yet in {@code list}. Return how many
     * elements were actually added. Duplicates among the candidates count
     * once. Safe to call from many threads at the same time on the same list:
     * afterwards the list must contain no duplicates that this method caused.
     *
     * <p>Examples:
     * <pre>
     * list = [a],  candidates = [a, b, b, c]
     * addUnique(list, candidates)  →  2        list is now [a, b, c]
     *
     * list = [a, b],  candidates = [a, b]
     * addUnique(list, candidates)  →  0        list is unchanged
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code contains} followed by {@code add} is a check-then-act race</li>
     *   <li>{@code CopyOnWriteArrayList} has a bulk method for exactly this and it returns a count</li>
     * </ul>
     *
     * @param list       the shared list (never null)
     * @param candidates elements to add if absent (never null)
     * @return the number of elements added
     */
    public int addUnique(CopyOnWriteArrayList<String> list, List<String> candidates) {
        throw new UnsupportedOperationException("TODO: implement addUnique");
    }

    /**
     * Call every listener in {@code listeners} with {@code event}, in order,
     * and return how many listeners were called. Listeners are allowed to
     * unregister themselves, or to register new listeners, while they are being
     * called. A listener registered during the notification is <b>not</b>
     * called for this event.
     *
     * <p>Examples:
     * <pre>
     * listeners = [L1, L2, L3]                     (L2 removes itself when called)
     * notifyListeners(listeners, "x")  →  3        L1, L2, L3 all called; listeners is now [L1, L3]
     *
     * listeners = [L1]                             (L1 registers a new listener L2 when called)
     * notifyListeners(listeners, "x")  →  1        L2 is NOT called; listeners is now [L1, L2]
     *
     * listeners = []
     * notifyListeners(listeners, "x")  →  0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A plain for-each loop over the list is enough - think about WHY it is safe here</li>
     * </ul>
     *
     * @param listeners the registered listeners (never null)
     * @param event     the event passed to every listener
     * @return the number of listeners that were called
     */
    public int notifyListeners(CopyOnWriteArrayList<Consumer<String>> listeners, String event) {
        throw new UnsupportedOperationException("TODO: implement notifyListeners");
    }

    /**
     * Return the distinct values of {@code values} in the order in which each
     * value FIRST appears, as a {@link CopyOnWriteArraySet}.
     *
     * <p>Examples:
     * <pre>
     * uniqueInOrder([3, 1, 3, 2, 1])  →  [3, 1, 2]
     * uniqueInOrder([7, 7, 7])        →  [7]
     * uniqueInOrder([])               →  []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This set type iterates in insertion order and ignores duplicate adds</li>
     * </ul>
     *
     * @param values the input values (never null)
     * @return a new set holding the distinct values in first-seen order
     */
    public CopyOnWriteArraySet<Integer> uniqueInOrder(int[] values) {
        throw new UnsupportedOperationException("TODO: implement uniqueInOrder");
    }
}
