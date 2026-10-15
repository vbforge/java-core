package com.vbforge.equals_hashcode_and_object_contract;


/**
 * ToStringTasks — implement every method below to make {@code ToStringTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ToStringDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ToStringTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The classes below are already written except for the stubs. The EXACT output format in each
 * Javadoc is what the tests check — copy it character for character. Do not change method
 * signatures or add external libraries — only the Java standard library is needed.
 */
public class ToStringTasks {

    // ── Task 1: Point ──────────────────────────────────────────────────────────

    /** An immutable 2D point. */
    public static final class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Returns {@code "Point(x=<x>, y=<y>)"}.
         *
         * <p>Examples:
         * <pre>
         *   new Point(1, 2).toString()     → "Point(x=1, y=2)"
         *   new Point(-3, 0).toString()    → "Point(x=-3, y=0)"
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>String concatenation with {@code +} converts an {@code int} automatically.</li>
         * </ul>
         *
         * @return the text form of this point
         */
        @Override
        public String toString() {
            throw new UnsupportedOperationException("TODO: implement Point.toString");
        }
    }

    // ── Task 2: Account ────────────────────────────────────────────────────────

    /** An account whose IBAN is sensitive. */
    public static final class Account {
        private final String owner;
        private final String iban;

        public Account(String owner, String iban) {
            this.owner = owner;
            this.iban = iban;                  // may be null
        }

        /**
         * Returns {@code "Account[owner=<owner>, iban=<masked>]"} where the IBAN is masked:
         * <ul>
         *   <li>{@code null} → {@code none}</li>
         *   <li>4 characters or fewer → {@code ****}</li>
         *   <li>otherwise → {@code ****} followed by the LAST four characters</li>
         * </ul>
         *
         * <p>Examples:
         * <pre>
         *   new Account("Ann", "DE8937040044").toString()   → "Account[owner=Ann, iban=****0044]"
         *   new Account("Ann", "12345").toString()          → "Account[owner=Ann, iban=****2345]"
         *   new Account("Ann", "1234").toString()           → "Account[owner=Ann, iban=****]"
         *   new Account("Ann", "12").toString()             → "Account[owner=Ann, iban=****]"
         *   new Account("Ann", null).toString()             → "Account[owner=Ann, iban=none]"
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>{@link String#substring(int)} with {@code length() - 4}.</li>
         *   <li>Four characters are NOT shown — it would reveal the whole number.</li>
         * </ul>
         *
         * @return the text form with a masked IBAN
         */
        @Override
        public String toString() {
            throw new UnsupportedOperationException("TODO: implement Account.toString");
        }
    }

    // ── Task 3: Node ───────────────────────────────────────────────────────────

    /** A tree node that knows its parent. */
    public static final class Node {
        private final String name;
        private final Node parent;

        public Node(String name, Node parent) {
            this.name = name;
            this.parent = parent;              // null for the root
        }

        /**
         * Returns {@code "Node[name=<name>, parent=<parent's NAME>]"}; the root prints
         * {@code parent=none}. Print only the parent's NAME, never the parent's {@code toString}
         * — a long chain of ancestors (or a cycle) must not explode the text.
         *
         * <p>Examples:
         * <pre>
         *   root = new Node("root", null)
         *   root.toString()                       → "Node[name=root, parent=none]"
         *   child = new Node("child", root)
         *   child.toString()                      → "Node[name=child, parent=root]"
         *   new Node("leaf", child).toString()    → "Node[name=leaf, parent=child]"
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Access the parent's private field directly — same class, no getter needed.</li>
         * </ul>
         *
         * @return the text form of this node
         */
        @Override
        public String toString() {
            throw new UnsupportedOperationException("TODO: implement Node.toString");
        }
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns exactly what {@link Object#toString()} would return for {@code o}: the class name,
     * an {@code '@'}, and the hash code in lower-case hexadecimal — even if {@code o}'s class
     * overrides {@code toString}.
     *
     * <p>Examples:
     * <pre>
     *   defaultFormat(new Object())      → "java.lang.Object@1b6d3586"   (the hex part varies)
     *   defaultFormat("abc")             → "java.lang.String@17862"      ("abc".hashCode() = 96354 = 0x17862)
     *   defaultFormat(new Point(1, 2))   → "...ToStringTasks$Point@" + hex of its hashCode
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getName()} and {@link Integer#toHexString(int)}.</li>
     *   <li>Call the VIRTUAL {@code hashCode()} — {@code Object.toString()} does the same.</li>
     * </ul>
     *
     * @param o the object (never {@code null})
     * @return {@code className@hexHashCode}
     */
    public String defaultFormat(Object o) {
        throw new UnsupportedOperationException("TODO: implement defaultFormat");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * A null-safe, array-aware text form of any object, as used in assertion messages:
     * <ul>
     *   <li>{@code null} → {@code "null"}</li>
     *   <li>an array (of any type, including primitives) → its elements in square brackets,
     *       separated by {@code ", "}, each element described by these same rules (so nested arrays
     *       work)</li>
     *   <li>anything else → its {@code toString()}</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   describe(null)                          → "null"
     *   describe("x")                           → "x"
     *   describe(42)                            → "42"
     *   describe(new int[] {1, 2})              → "[1, 2]"
     *   describe(new String[] {"a", null})      → "[a, null]"
     *   describe(new int[0])                    → "[]"
     *   describe(new double[] {1.5})            → "[1.5]"
     *   describe(new Object[] {1, new int[] {2, 3}})   → "[1, [2, 3]]"
     *   describe(java.util.Arrays.asList(1, 2)) → "[1, 2]"       (a List prints itself)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code o.getClass().isArray()} detects any array; {@link java.lang.reflect.Array}
     *       reads its length and elements without knowing the element type.</li>
     *   <li>{@code Array.get} boxes primitives for you.</li>
     *   <li>The method is naturally recursive.</li>
     * </ul>
     *
     * @param o any object, possibly {@code null} or an array
     * @return its text form
     */
    public String describe(Object o) {
        throw new UnsupportedOperationException("TODO: implement describe");
    }

    // ── Task 6: Money ──────────────────────────────────────────────────────────

    /** An amount in cents. */
    public static final class Money {
        private final long cents;
        private final String currency;

        public Money(long cents, String currency) {
            this.cents = cents;
            this.currency = currency;
        }

        /**
         * Returns {@code "<amount> <currency>"} where the amount has EXACTLY two decimals, a
         * {@code '.'} separator, no thousands grouping, and a leading {@code '-'} for negative
         * amounts. Assume {@code |cents| < Long.MAX_VALUE}.
         *
         * <p>Examples:
         * <pre>
         *   new Money(1250, "EUR").toString()      → "12.50 EUR"
         *   new Money(5, "EUR").toString()         → "0.05 EUR"
         *   new Money(0, "USD").toString()         → "0.00 USD"
         *   new Money(-50, "EUR").toString()       → "-0.50 EUR"
         *   new Money(-1999, "EUR").toString()     → "-19.99 EUR"
         *   new Money(123456789, "EUR").toString() → "1234567.89 EUR"
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>Work with the absolute value and add the sign separately — {@code -50 / 100} is
         *       {@code 0}, which loses the minus sign.</li>
         *   <li>{@code String.format(Locale.ROOT, "%d.%02d", ...)}; the locale keeps the decimal
         *       separator a dot on every machine.</li>
         * </ul>
         *
         * @return the text form of this amount
         */
        @Override
        public String toString() {
            throw new UnsupportedOperationException("TODO: implement Money.toString");
        }
    }
}
