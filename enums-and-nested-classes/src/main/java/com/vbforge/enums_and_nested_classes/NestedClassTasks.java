package com.vbforge.enums_and_nested_classes;

import java.util.Iterator;

/**
 * 🎯 TASKS — Static nested, inner and local classes
 *
 * <p>Implement every method marked {@code TODO} so that the tests in
 * {@code NestedClassTasksTest} pass. Tasks 2 and 3 live INSIDE nested classes.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or the shape of the supporting classes</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} line with your code</li>
 *   <li>You may add private helpers or private nested classes if you need them</li>
 * </ul>
 */
public class NestedClassTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return an iterator that counts down from {@code from} to {@code 1}.
     * For {@code from < 1} the iterator is empty.
     *
     * <p>Contract details the tests rely on:
     * <ul>
     *   <li>{@code hasNext()} may be called any number of times without consuming anything</li>
     *   <li>{@code next()} on an exhausted iterator throws {@link java.util.NoSuchElementException}</li>
     *   <li>every call to {@code countdown} returns an independent iterator</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   countdown(3) → 3, 2, 1
     *   countdown(1) → 1
     *   countdown(0) → (empty)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Write a small class that implements {@code Iterator<Integer>} — where you
     *       declare it (member class, local class) is your choice</li>
     *   <li>The iterator needs its own mutable position</li>
     * </ul>
     *
     * @param from the first number to produce
     * @return a fresh iterator over from, from-1, ..., 1
     */
    public Iterator<Integer> countdown(int from) {
        throw new UnsupportedOperationException("TODO: implement countdown");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * A support ticket. Its constructor is private — instances are created
     * by {@link Builder#build()}. {@code Ticket} and {@code Builder} are both
     * static nested classes of {@code NestedClassTasks}.
     */
    public static class Ticket {
        private final String title;
        private final int priority;

        private Ticket(String title, int priority) {
            this.title = title;
            this.priority = priority;
        }

        public String title() {
            return title;
        }

        public int priority() {
            return priority;
        }

        public static class Builder {
            private String title;
            private int priority = 3;

            public Builder title(String title) {
                this.title = title;
                return this;
            }

            public Builder priority(int priority) {
                this.priority = priority;
                return this;
            }

            /**
             * Validate the collected values and create the {@link Ticket}.
             *
             * <p>Rules:
             * <ul>
             *   <li>the title is stored trimmed</li>
             *   <li>a {@code null} or blank title → {@link IllegalStateException}</li>
             *   <li>a priority below 1 or above 5 → {@link IllegalStateException}</li>
             *   <li>priority defaults to 3 when never set</li>
             * </ul>
             *
             * <p>Examples:
             * <pre>
             *   new Builder().title("  Fix login ").priority(2).build() → Ticket("Fix login", 2)
             *   new Builder().title("Fix login").build()                → Ticket("Fix login", 3)
             *   new Builder().priority(2).build()                       → throws IllegalStateException
             *   new Builder().title("x").priority(6).build()            → throws IllegalStateException
             * </pre>
             *
             * <p>Hints:
             * <ul>
             *   <li>The {@code Ticket} constructor is private — which relationship between
             *       nested classes still lets you call it?</li>
             * </ul>
             *
             * @return a new, valid ticket
             */
            public Ticket build() {
                throw new UnsupportedOperationException("TODO: implement build");
            }
        }
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * A team keeps a running total of its members' points.
     * {@link Member} is an INNER class: every member belongs to the one
     * {@code Team} that created it via {@link #join(String)}.
     */
    public static class Team {
        private int totalScore;

        public int totalScore() {
            return totalScore;
        }

        public Member join(String name) {
            return new Member(name);
        }

        public class Member {
            private final String name;
            private int score;

            private Member(String name) {
                this.name = name;
            }

            public String name() {
                return name;
            }

            public int score() {
                return score;
            }

            /**
             * Give this member points — the points also count for the member's team.
             *
             * <p>A negative amount → {@link IllegalArgumentException}; nothing changes then.
             * Zero is allowed and changes nothing.
             *
             * <p>Examples:
             * <pre>
             *   team.join("ann").addPoints(5);   → ann.score() == 5, team.totalScore() == 5
             *   team.join("bob").addPoints(2);   → bob.score() == 2, team.totalScore() == 7
             *   otherTeam.totalScore()           → still 0 (teams are independent)
             * </pre>
             *
             * <p>Hints:
             * <ul>
             *   <li>A {@code Member} can reach the fields of the {@code Team} instance it was created from</li>
             * </ul>
             *
             * @param points points to add, must not be negative
             */
            public void addPoints(int points) {
                throw new UnsupportedOperationException("TODO: implement addPoints");
            }
        }
    }
}
