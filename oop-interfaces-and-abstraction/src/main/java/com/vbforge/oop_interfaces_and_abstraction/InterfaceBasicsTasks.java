package com.vbforge.oop_interfaces_and_abstraction;

import java.util.Iterator;
import java.util.List;

/**
 * InterfaceBasicsTasks — implement every method below to make {@code InterfaceBasicsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code InterfaceBasicsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=InterfaceBasicsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>The interfaces {@link Channel} and {@link Priced} are already written, and so are the
 * constructors of {@link Temperature} and {@link Playlist}. Do not change method signatures
 * or add external libraries — only the Java standard library is needed.
 */
public class InterfaceBasicsTasks {

    // ── Given: contracts (do NOT modify) ───────────────────────────────────────

    /** Something that can deliver a message and returns a receipt. */
    public interface Channel {
        String send(String message);
    }

    /** Anything with a price. */
    public interface Priced {
        double price();
    }

    // ── Task 1: Temperature.compareTo ──────────────────────────────────────────

    /** A temperature that must be sortable by implementing {@link Comparable}. */
    public static class Temperature implements Comparable<Temperature> {
        private final double degrees;

        public Temperature(double degrees) {
            this.degrees = degrees;
        }

        public double getDegrees() {
            return degrees;
        }

        /**
         * Implements {@link Comparable#compareTo}: orders temperatures from coldest to hottest.
         * Returns a negative number if this temperature is lower than {@code other}, zero if they
         * are equal, and a positive number if this one is higher. Only the SIGN of the result matters.
         *
         * <p>Examples:
         * <pre>
         *   new Temperature(10).compareTo(new Temperature(20))   → negative
         *   new Temperature(20).compareTo(new Temperature(10))   → positive
         *   new Temperature(15).compareTo(new Temperature(15))   → 0
         *   new Temperature(-5).compareTo(new Temperature(3))    → negative
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>The wrapper class {@link Double} already knows how to compare two primitive doubles.</li>
         *   <li>Do not return {@code (int) (a - b)}: casting a small difference such as 0.5 to
         *       {@code int} gives 0.</li>
         * </ul>
         *
         * @param other the temperature to compare with (never {@code null})
         * @return negative, zero or positive as this temperature is lower, equal or higher than {@code other}
         */
        @Override
        public int compareTo(Temperature other) {
            throw new UnsupportedOperationException("TODO: implement Temperature.compareTo");
        }
    }

    // ── Task 2: Playlist.iterator ──────────────────────────────────────────────

    /** A list of song titles that must be usable in an enhanced {@code for} loop. */
    public static class Playlist implements Iterable<String> {
        private final List<String> songs;

        public Playlist(List<String> songs) {
            this.songs = songs;
        }

        /**
         * Implements {@link Iterable#iterator()}: returns an iterator over the songs in their
         * original order. Every call must return a NEW iterator that starts at the first song,
         * so the playlist can be looped over any number of times.
         *
         * <p>Examples:
         * <pre>
         *   for (String s : new Playlist(["Intro", "Verse", "Outro"]))   → visits "Intro", "Verse", "Outro"
         *   for (String s : new Playlist([]))                            → visits nothing
         * </pre>
         *
         * <p>Hints:
         * <ul>
         *   <li>The list you were given is itself {@link Iterable} — it already owns an iterator.</li>
         * </ul>
         *
         * @return an iterator over the songs, first to last
         */
        @Override
        public Iterator<String> iterator() {
            throw new UnsupportedOperationException("TODO: implement Playlist.iterator");
        }
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the sum of the prices of all {@code items}. The list may contain any mix of
     * implementations of {@link Priced}; you only know the contract.
     *
     * <p>Examples:
     * <pre>
     *   totalPrice([price 10.0, price 2.5, price 0.5])   → 13.0
     *   totalPrice([price 7.0])                          → 7.0
     *   totalPrice([])                                   → 0.0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Program against the interface: you should not need {@code instanceof} or a cast.</li>
     * </ul>
     *
     * @param items the priced items (may be empty)
     * @return the sum of all prices
     */
    public double totalPrice(List<? extends Priced> items) {
        throw new UnsupportedOperationException("TODO: implement totalPrice");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Combines several channels into ONE channel. Sending a message through the result sends it
     * through every given channel, in order, and returns all their receipts joined with
     * {@code " | "}. With no channels the receipt is the empty string.
     *
     * <p>Examples:
     * <pre>
     *   combine([m -> "A:" + m, m -> "B:" + m]).send("hi")   → "A:hi | B:hi"
     *   combine([m -> "A:" + m]).send("hi")                  → "A:hi"
     *   combine([]).send("hi")                               → ""
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Channel} has a single abstract method, so a lambda (or an anonymous class) is a
     *       valid way to create a new implementation.</li>
     *   <li>{@link String#join(CharSequence, Iterable)} glues strings together with a separator.</li>
     * </ul>
     *
     * @param channels the channels to combine (may be empty)
     * @return a channel that delegates to all of them
     */
    public Channel combine(List<Channel> channels) {
        throw new UnsupportedOperationException("TODO: implement combine");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the simple names of the interfaces that {@code type} implements DIRECTLY, in the
     * order they are declared. Interfaces inherited from a superclass or from other interfaces are
     * not included.
     *
     * <p>Examples:
     * <pre>
     *   interfaceNames(ArrayList.class)   → ["List", "RandomAccess", "Cloneable", "Serializable"]
     *   interfaceNames(HashMap.class)     → ["Map", "Cloneable", "Serializable"]
     *   interfaceNames(Temperature.class) → ["Comparable"]
     *   interfaceNames(Object.class)      → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#getInterfaces()} already returns the direct interfaces in declaration order.</li>
     * </ul>
     *
     * @param type the class to inspect (never {@code null})
     * @return simple names of the directly implemented interfaces
     */
    public List<String> interfaceNames(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement interfaceNames");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code type} implements the INTERFACE {@code iface} — directly, through
     * a superclass, or through another interface. Returns {@code false} when {@code iface} is not
     * an interface (for example an abstract class).
     *
     * <p>Examples:
     * <pre>
     *   implementsInterface(ArrayList.class,  Collection.class)    → true    (List extends Collection)
     *   implementsInterface(HashMap.class,    Map.class)           → true
     *   implementsInterface(String.class,     CharSequence.class)  → true
     *   implementsInterface(LinkedList.class, RandomAccess.class)  → false
     *   implementsInterface(ArrayList.class,  AbstractList.class)  → false   (a class, not an interface)
     *   implementsInterface(Runnable.class,   Runnable.class)      → true    (an interface counts as itself)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Class#isAssignableFrom(Class)} knows about the whole type hierarchy — mind which
     *       class is the receiver and which is the argument.</li>
     *   <li>{@link Class#isInterface()} tells interfaces and classes apart.</li>
     * </ul>
     *
     * @param type  the class to inspect (never {@code null})
     * @param iface the candidate interface (never {@code null})
     * @return {@code true} if {@code iface} is an interface that {@code type} implements
     */
    public boolean implementsInterface(Class<?> type, Class<?> iface) {
        throw new UnsupportedOperationException("TODO: implement implementsInterface");
    }
}
