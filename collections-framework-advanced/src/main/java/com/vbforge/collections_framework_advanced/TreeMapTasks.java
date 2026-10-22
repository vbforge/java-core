package com.vbforge.collections_framework_advanced;

import java.util.List;
import java.util.TreeMap;

/**
 * 🎯 TASKS — TreeMap
 *
 * <p>Implement every method so that all tests in {@code TreeMapTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class TreeMapTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Look up a shipping cost. The map holds price TIERS: each key is the MINIMUM weight (in kg) of a
     * tier and each value is the cost for that tier. A parcel belongs to the tier with the greatest
     * minimum weight that is not above its weight. A weight below the lowest tier is an error.
     *
     * <p>Examples:
     * <pre>
     *   tiers {0=5, 10=8, 25=12}
     *   shippingCost(tiers, 3)    → 5
     *   shippingCost(tiers, 10)   → 8     (the boundary belongs to the higher tier)
     *   shippingCost(tiers, 24)   → 8
     *   shippingCost(tiers, 100)  → 12
     *   shippingCost(tiers, -1)   → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>"Greatest key not above X" is exactly one of the navigation methods</li>
     *   <li>That method reports "nothing there" with a special value</li>
     * </ul>
     *
     * @param tiers  minimum weight to cost, never {@code null}
     * @param weight the parcel weight
     * @return the cost of the matching tier
     * @throws IllegalArgumentException if no tier covers the weight
     */
    public int shippingCost(TreeMap<Integer, Integer> tiers, int weight) {
        throw new UnsupportedOperationException("TODO: implement shippingCost");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Count how often each word occurs, IGNORING case, and return the counts sorted alphabetically
     * (ignoring case) by word. The key keeps the spelling of the FIRST occurrence of the word.
     *
     * <p>Examples:
     * <pre>
     *   ["Pear", "apple", "pear", "APPLE", "fig"]  → {apple=2, fig=1, Pear=2}
     *   ["b", "B", "a"]                            → {a=1, b=2}
     *   []                                         → {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Give the map a comparator that ignores case, then let the map itself decide which words are the same</li>
     *   <li>Look at {@code merge} for "insert or add to the existing value"</li>
     * </ul>
     *
     * @param words the words, never {@code null}
     * @return the case-insensitive word counts in alphabetical order
     */
    public TreeMap<String, Integer> countWords(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement countWords");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return the events whose time lies between {@code fromInclusive} and {@code toInclusive}
     * (both ends INCLUDED), in time order. A reversed range ({@code from > to}) has no events and must
     * not throw.
     *
     * <p>Examples:
     * <pre>
     *   timeline {9=standup, 11=review, 14=lunch, 17=deploy}
     *   eventsBetween(timeline, 10, 14)  → [review, lunch]
     *   eventsBetween(timeline, 9, 9)    → [standup]
     *   eventsBetween(timeline, 12, 13)  → []
     *   eventsBetween(timeline, 14, 10)  → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A range view of the map contains exactly what you need</li>
     *   <li>Creating such a view with a reversed range throws {@code IllegalArgumentException}</li>
     * </ul>
     *
     * @param timeline      time to event, never {@code null}
     * @param fromInclusive start of the range
     * @param toInclusive   end of the range
     * @return the event names in time order
     */
    public List<String> eventsBetween(TreeMap<Integer, String> timeline, int fromInclusive, int toInclusive) {
        throw new UnsupportedOperationException("TODO: implement eventsBetween");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Try to book the half-open time slot {@code [start, end)} in a calendar. The map holds the existing
     * bookings: key = start, value = end (also half-open). The new slot is accepted only when it overlaps
     * no existing booking; slots that merely TOUCH (one ends exactly where the next starts) do not overlap.
     * When accepted, add it to the map and return {@code true}; otherwise leave the map unchanged and
     * return {@code false}.
     *
     * <p>Examples:
     * <pre>
     *   bookings {10=20, 30=40}
     *   book(bookings, 20, 30)  → true     (touches both neighbours — fine), map now {10=20, 20=30, 30=40}
     *   book(bookings, 15, 25)  → false    (overlaps 10..20)
     *   book(bookings, 5, 12)   → false    (overlaps 10..20)
     *   book(bookings, 0, 10)   → true
     *   book(bookings, 35, 38)  → false    (inside 30..40)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Only TWO existing bookings can possibly collide: the closest one starting at-or-before {@code start}
     *       and the closest one starting at-or-after {@code start}</li>
     * </ul>
     *
     * @param bookings existing bookings (it is modified when the booking succeeds), never {@code null}
     * @param start    the start of the new slot (inclusive)
     * @param end      the end of the new slot (exclusive)
     * @return {@code true} if the slot was booked
     * @throws IllegalArgumentException if {@code start >= end}
     */
    public boolean book(TreeMap<Integer, Integer> bookings, int start, int end) {
        throw new UnsupportedOperationException("TODO: implement book");
    }
}
