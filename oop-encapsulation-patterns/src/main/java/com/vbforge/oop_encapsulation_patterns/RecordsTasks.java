package com.vbforge.oop_encapsulation_patterns;

import java.util.List;

/**
 * RecordsTasks — implement every method below to make {@code RecordsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code RecordsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method or constructor — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=RecordsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The records {@link Email}, {@link Team}, {@link Rectangle} and {@link Samples} are already
 * declared — you fill in the stubs. Do not change method signatures or add external libraries —
 * only the Java standard library is needed. Requires Java 16+ (records).
 */
public class RecordsTasks {

    // ── Task 1: Email ──────────────────────────────────────────────────────────

    /**
     * An e-mail address, normalised.
     *
     * <p>The compact constructor must normalise and validate {@code value}:
     * <ol>
     *   <li>{@code null} → {@link IllegalArgumentException}</li>
     *   <li>trim surrounding whitespace and convert to lower case (use {@link java.util.Locale#ROOT})</li>
     *   <li>the result must contain exactly one {@code '@'}, with at least one character before
     *       it and at least one after it — otherwise {@link IllegalArgumentException}</li>
     * </ol>
     *
     * <p>Examples:
     * <pre>
     *   new Email("  Bob@Example.COM ").value()   → "bob@example.com"
     *   new Email("a@b").value()                  → "a@b"
     *   new Email("bob.example.com")              → throws IllegalArgumentException   (no @)
     *   new Email("a@b@c")                        → throws IllegalArgumentException   (two @)
     *   new Email("@example.com")                 → throws IllegalArgumentException   (empty local part)
     *   new Email("bob@")                         → throws IllegalArgumentException   (empty domain)
     *   new Email(null)                           → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>In a compact constructor you may reassign the PARAMETER {@code value}; the field
     *       receives it automatically.</li>
     *   <li>{@link String#indexOf(int)} and {@link String#lastIndexOf(int)} can tell "exactly one"
     *       without counting in a loop.</li>
     * </ul>
     *
     * @param value the raw address
     */
    public record Email(String value) {
        public Email {
            throw new UnsupportedOperationException("TODO: implement Email");
        }
    }

    // ── Task 2: Team ───────────────────────────────────────────────────────────

    /**
     * A team: a name and its members.
     *
     * <p>The compact constructor must:
     * <ol>
     *   <li>reject a {@code null} or blank {@code name} with {@link IllegalArgumentException}</li>
     *   <li>reject a {@code null} {@code members} list with {@link IllegalArgumentException}</li>
     *   <li>store an unmodifiable, independent copy of {@code members}</li>
     * </ol>
     * (You may assume the list holds no {@code null} elements.)
     *
     * <p>Examples:
     * <pre>
     *   list = ["ann"]; t = new Team("Core", list); list.add("bob")   → t.members() is ["ann"]
     *   new Team("Core", ["ann"]).members().add("x")                   → throws UnsupportedOperationException
     *   new Team("  ", ["ann"])                                        → throws IllegalArgumentException
     *   new Team(null, ["ann"])                                        → throws IllegalArgumentException
     *   new Team("Core", null)                                         → throws IllegalArgumentException
     *   new Team("Core", []).members()                                 → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code List.copyOf} would throw {@link NullPointerException} for a {@code null} list —
     *       check first.</li>
     *   <li>{@link String#isBlank()} exists since Java 11.</li>
     * </ul>
     *
     * @param name    the team name
     * @param members the member names
     */
    public record Team(String name, List<String> members) {
        public Team {
            throw new UnsupportedOperationException("TODO: implement Team");
        }
    }

    // ── Tasks 3 & 4: Rectangle ─────────────────────────────────────────────────

    /** A rectangle. The compact constructor (given) rejects a non-positive width or height. */
    public record Rectangle(int width, int height) {
        /** Given. */
        public Rectangle {
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("width and height must be positive");
            }
        }

        /**
         * Static factory: a square with the given side length.
         *
         * <p>Examples:
         * <pre>
         *   Rectangle.square(3)    → Rectangle[width=3, height=3]
         *   Rectangle.square(1)    → Rectangle[width=1, height=1]
         *   Rectangle.square(0)    → throws IllegalArgumentException
         *   Rectangle.square(-4)   → throws IllegalArgumentException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Validation already lives in the compact constructor — do not repeat it.</li>
         * </ul>
         *
         * @param side the side length
         * @return a square
         */
        public static Rectangle square(int side) {
            throw new UnsupportedOperationException("TODO: implement square");
        }

        /**
         * Returns a NEW rectangle with both width and height multiplied by {@code factor}. This
         * rectangle is not changed (records have no setters).
         *
         * <p>Examples:
         * <pre>
         *   new Rectangle(2, 3).scaledBy(2)    → Rectangle[width=4, height=6]
         *   new Rectangle(2, 3).scaledBy(1)    → Rectangle[width=2, height=3]
         *   new Rectangle(2, 3).scaledBy(0)    → throws IllegalArgumentException
         *   new Rectangle(2, 3).scaledBy(-1)   → throws IllegalArgumentException
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Build the result through the canonical constructor and let it validate.</li>
         * </ul>
         *
         * @param factor the multiplier
         * @return a new, scaled rectangle
         */
        public Rectangle scaledBy(int factor) {
            throw new UnsupportedOperationException("TODO: implement scaledBy");
        }
    }

    // ── Tasks 5 & 6: Samples ───────────────────────────────────────────────────

    /**
     * A record with an ARRAY component. The generated {@code equals}/{@code hashCode} compare
     * arrays by identity, which is rarely what you want — you fix that by overriding both.
     * The constructor already copies the array in, and the accessor copies it out.
     */
    public record Samples(int[] values) {
        /** Given: copy IN. */
        public Samples {
            values = values.clone();
        }

        /** Given: copy OUT. */
        @Override
        public int[] values() {
            return values.clone();
        }

        /**
         * Two {@code Samples} are equal when they hold the same numbers in the same order.
         *
         * <p>Examples:
         * <pre>
         *   new Samples({1, 2}).equals(new Samples({1, 2}))   → true
         *   new Samples({1, 2}).equals(new Samples({2, 1}))   → false   (order matters)
         *   new Samples({1, 2}).equals(new Samples({1}))      → false
         *   new Samples({}).equals(new Samples({}))           → true
         *   new Samples({1}).equals(null)                     → false
         *   new Samples({1}).equals("1")                      → false
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@link java.util.Arrays} compares arrays by content.</li>
         *   <li>Pattern matching for {@code instanceof} (Java 16) saves the explicit cast.</li>
         * </ul>
         *
         * @param o the object to compare with
         * @return {@code true} if {@code o} is a {@code Samples} with equal contents
         */
        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException("TODO: implement equals");
        }

        /**
         * Returns a hash code consistent with {@link #equals(Object)}: two equal {@code Samples}
         * must have the same hash code.
         *
         * <p>Examples:
         * <pre>
         *   new Samples({1, 2}).hashCode() == new Samples({1, 2}).hashCode()   → true
         *   new Samples({}).hashCode()     == new Samples({}).hashCode()       → true
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@link java.util.Arrays} can hash an array by content as well.</li>
         * </ul>
         *
         * @return a content-based hash code
         */
        @Override
        public int hashCode() {
            throw new UnsupportedOperationException("TODO: implement hashCode");
        }
    }

    // ── Task 7 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the names of the components of a record, in declaration order. For a type that is
     * not a record, returns an empty list.
     *
     * <p>Examples:
     * <pre>
     *   componentNames(Rectangle.class)   → ["width", "height"]
     *   componentNames(Email.class)       → ["value"]
     *   componentNames(Team.class)        → ["name", "members"]
     *   componentNames(String.class)      → []
     *   componentNames(Runnable.class)    → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>See {@link Class#isRecord()} and {@link Class#getRecordComponents()}.</li>
     *   <li>{@code getRecordComponents()} returns {@code null} for a non-record.</li>
     * </ul>
     *
     * @param type the class to inspect (never {@code null})
     * @return the component names, or an empty list if {@code type} is not a record
     */
    public List<String> componentNames(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement componentNames");
    }
}
