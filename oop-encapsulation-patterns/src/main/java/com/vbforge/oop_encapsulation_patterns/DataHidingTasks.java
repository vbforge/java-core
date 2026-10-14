package com.vbforge.oop_encapsulation_patterns;

import java.util.ArrayList;
import java.util.List;

/**
 * DataHidingTasks — implement every method below to make {@code DataHidingTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code DataHidingDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=DataHidingTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The classes {@link Playlist}, {@link Account} and {@link Roster} are already written except
 * for the stubs. Do not change method signatures or add external libraries — only the Java
 * standard library is needed.
 */
public class DataHidingTasks {

    // ── Tasks 1 & 2: Playlist ──────────────────────────────────────────────────

    /** A playlist that owns its list of track names. */
    public static class Playlist {
        private final List<String> tracks = new ArrayList<>();

        /** Given: appends one track. */
        public void add(String track) {
            tracks.add(track);
        }

        /** Given: how many tracks the playlist holds right now. */
        public int size() {
            return tracks.size();
        }

        /**
         * Returns the tracks in insertion order WITHOUT letting the caller reach the playlist's
         * own list: the result is read-only, and tracks added to the playlist later do NOT
         * appear in a list that was returned earlier.
         *
         * <p>Examples:
         * <pre>
         *   p = playlist with ["a", "b"]
         *   p.tracks()                  → ["a", "b"]
         *   p.tracks().add("x")         → throws UnsupportedOperationException
         *   t = p.tracks(); p.add("c")  → t is still ["a", "b"]
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>A read-only VIEW of the field would still show later changes — you need an
         *       independent copy that is also read-only.</li>
         *   <li>See {@link List#copyOf(java.util.Collection)}.</li>
         * </ul>
         *
         * @return an unmodifiable snapshot of the current tracks
         */
        public List<String> tracks() {
            throw new UnsupportedOperationException("TODO: implement tracks");
        }

        /**
         * Appends all of {@code newTracks}, but only if EVERY one of them is valid — otherwise
         * the playlist must stay exactly as it was (all-or-nothing). A track is valid if it is
         * not {@code null} and not blank (empty or only whitespace). An empty list is allowed and
         * changes nothing.
         *
         * <p>Examples:
         * <pre>
         *   p = playlist with ["a"]
         *   p.addAll(["b", "c"])     → p.tracks() is ["a", "b", "c"]
         *   p.addAll(["d", " "])     → throws IllegalArgumentException, p.tracks() is still ["a"]
         *   p.addAll(null)           → throws IllegalArgumentException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Check the whole input BEFORE the first change to the playlist.</li>
         *   <li>{@link String#isBlank()} exists since Java 11.</li>
         * </ul>
         *
         * @param newTracks the tracks to append
         * @throws IllegalArgumentException if the list is {@code null} or holds an invalid track
         */
        public void addAll(List<String> newTracks) {
            throw new UnsupportedOperationException("TODO: implement addAll");
        }
    }

    // ── Tasks 3 & 4: Account ───────────────────────────────────────────────────

    /** A bank account whose balance can only change through its methods. */
    public static class Account {
        private double balance;

        /** Given: rejects a negative opening balance with {@link IllegalArgumentException}. */
        public Account(double initial) {
            if (initial < 0) {
                throw new IllegalArgumentException("initial balance must not be negative");
            }
            this.balance = initial;
        }

        /** Given: the current balance. */
        public double balance() {
            return balance;
        }

        /** Given: adds {@code amount}; rejects a non-positive amount with {@link IllegalArgumentException}. */
        public void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            balance += amount;
        }

        /**
         * Removes {@code amount} from the balance, guarding the invariant "never negative".
         *
         * <p>Examples:
         * <pre>
         *   new Account(100).withdraw(30)    → balance becomes 70.0
         *   new Account(100).withdraw(100)   → balance becomes 0.0
         *   new Account(100).withdraw(0)     → throws IllegalArgumentException
         *   new Account(100).withdraw(-5)    → throws IllegalArgumentException
         *   new Account(100).withdraw(100.5) → throws IllegalStateException, balance stays 100.0
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Two different failures, two different exception types.</li>
         *   <li>A failed call must leave the balance untouched.</li>
         * </ul>
         *
         * @param amount the amount to remove
         * @throws IllegalArgumentException if {@code amount} is not positive
         * @throws IllegalStateException    if {@code amount} exceeds the balance
         */
        public void withdraw(double amount) {
            throw new UnsupportedOperationException("TODO: implement withdraw");
        }

        /**
         * Moves {@code amount} from this account to {@code target}. Either both balances change
         * or neither does.
         *
         * <p>Examples:
         * <pre>
         *   a = new Account(100); b = new Account(10)
         *   a.transferTo(b, 40)       → a is 60.0, b is 50.0
         *   a.transferTo(b, 500)      → throws IllegalStateException, a and b unchanged
         *   a.transferTo(b, 0)        → throws IllegalArgumentException, a and b unchanged
         *   a.transferTo(null, 10)    → throws IllegalArgumentException, a unchanged
         *   a.transferTo(a, 10)       → throws IllegalArgumentException (same account)
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Reuse {@code withdraw} and {@code deposit} instead of touching the balance
         *       directly — they already hold the rules.</li>
         *   <li>Think about which step can fail and do it first.</li>
         * </ul>
         *
         * @param target the account to credit
         * @param amount the amount to move
         * @throws IllegalArgumentException if {@code target} is {@code null} or this account, or
         *                                  {@code amount} is not positive
         * @throws IllegalStateException    if {@code amount} exceeds this account's balance
         */
        public void transferTo(Account target, double amount) {
            throw new UnsupportedOperationException("TODO: implement transferTo");
        }
    }

    // ── Task 5: Roster ─────────────────────────────────────────────────────────

    /** An immutable list of names. The constructor is private — use {@link #of(String[])}. */
    public static class Roster {
        private final String[] names;

        private Roster(String[] names) {
            this.names = names;                      // stores EXACTLY the array it is given
        }

        /** Given: a copy of the names (copy OUT is already handled). */
        public String[] names() {
            return names.clone();
        }

        /** Given: the number of names. */
        public int size() {
            return names.length;
        }

        /**
         * Creates a roster from {@code names} that is completely independent of the caller's
         * array: changing the array afterwards must NOT change the roster. Every name must be
         * non-null and not blank (empty or only whitespace).
         *
         * <p>Examples:
         * <pre>
         *   arr = {"Ann", "Bob"}; r = Roster.of(arr); arr[0] = "Zed"   → r.names() is ["Ann", "Bob"]
         *   Roster.of(new String[] {"Ann", null})                      → throws IllegalArgumentException
         *   Roster.of(new String[] {"Ann", "  "})                      → throws IllegalArgumentException
         *   Roster.of(null)                                            → throws IllegalArgumentException
         *   Roster.of(new String[0]).size()                            → 0
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Copy first, then validate the COPY and hand the copy to the private constructor.</li>
         *   <li>Arrays have a {@code clone()} method.</li>
         * </ul>
         *
         * @param names the names to store
         * @return a roster that owns its own copy of the names
         * @throws IllegalArgumentException if {@code names} is {@code null} or holds an invalid name
         */
        public static Roster of(String[] names) {
            throw new UnsupportedOperationException("TODO: implement of");
        }
    }
}
