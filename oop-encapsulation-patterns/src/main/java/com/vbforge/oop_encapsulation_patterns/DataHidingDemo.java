package com.vbforge.oop_encapsulation_patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataHidingDemo — annotated reference for data hiding (information hiding) in Java.
 *
 * <p>Read top-to-bottom. Each section covers one facet of data hiding with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>Guarded state — a public field vs a private field behind a validating method</li>
 *   <li>Leaking internals OUT — a getter that returns a mutable object</li>
 *   <li>Leaking internals IN — a constructor that keeps the caller's array</li>
 *   <li>Views vs snapshots — {@code unmodifiableList} vs {@code List.copyOf}</li>
 *   <li>Behaviour over getters/setters — private fields are not enough</li>
 *   <li>Hiding the representation — two classes, one contract</li>
 * </ol>
 */
public class DataHidingDemo {

    public static void main(String[] args) {
        guardedStateDemo();
        System.out.println("---");
        leakOutDemo();
        System.out.println("---");
        leakInDemo();
        System.out.println("---");
        viewVsSnapshotDemo();
        System.out.println("---");
        behaviourOverSettersDemo();
        System.out.println("---");
        hidingRepresentationDemo();
    }

    // ── 1. Guarded state ──────────────────────────────────────────────────────

    static class OpenAccount {
        public double balance;                       // anyone can write anything

        OpenAccount(double balance) {
            this.balance = balance;
        }
    }

    static class Account {
        private double balance;

        Account(double initial) {
            if (initial < 0) {
                throw new IllegalArgumentException("initial balance must not be negative");
            }
            this.balance = initial;
        }

        double balance() {
            return balance;
        }

        void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            balance += amount;
        }

        void withdraw(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            if (amount > balance) {
                throw new IllegalStateException("insufficient funds");
            }
            balance -= amount;
        }
    }

    /**
     * Data hiding means the object's state can only change through code the object itself
     * controls. A {@code public} field cannot enforce any rule: {@code account.balance = -999}
     * compiles and runs. A {@code private} field forces every change through a method, and the
     * method is the single place where the rule ("never negative") is written and checked.
     *
     * <p>WHY this matters: an <em>invariant</em> (a fact that must always be true) is only
     * trustworthy if no code path can break it. With a private field, you can prove it by
     * reading one class instead of the whole program.
     *
     * <p>Common gotcha: validating in the constructor but exposing a public setter — or a public
     * field — that skips the check.
     */
    static void guardedStateDemo() {
        System.out.println("=== Guarded state ===");
        OpenAccount open = new OpenAccount(100);
        open.balance = -999;                                         // compiles, no rule enforced
        System.out.println("open account balance : " + open.balance);

        Account account = new Account(100);
        try {
            account.withdraw(500);
        } catch (IllegalStateException e) {
            System.out.println("withdraw(500) refused : " + e.getMessage());
        }
        System.out.println("account balance      : " + account.balance());   // still 100.0
    }

    // ── 2. Leaking internals OUT ──────────────────────────────────────────────

    static class LeakySchedule {
        private final List<String> slots = new ArrayList<>();

        void add(String slot) {
            slots.add(slot);
        }

        List<String> slots() {
            return slots;                            // hands out the REAL list
        }

        int size() {
            return slots.size();
        }
    }

    static class SafeSchedule {
        private final List<String> slots = new ArrayList<>();

        void add(String slot) {
            slots.add(slot);
        }

        List<String> slots() {
            return List.copyOf(slots);               // hands out an independent, read-only copy
        }

        int size() {
            return slots.size();
        }
    }

    /**
     * A getter returns a REFERENCE, not a copy. If the field is a mutable object (a
     * {@code List}, an array, a {@code Date}, a {@code StringBuilder}), the caller now holds the
     * very object inside your class and can change it behind your back.
     *
     * <p>WHY this matters: {@code private final List<String> slots} looks locked down, but
     * {@code final} only freezes the REFERENCE. {@code schedule.slots().clear()} empties the
     * schedule without ever calling a method of {@code LeakySchedule}.
     *
     * <p>Fix: return an immutable copy ({@code List.copyOf}, Java 10+) or a read-only view.
     * On Java 8 write {@code Collections.unmodifiableList(new ArrayList<>(slots))}.
     *
     * <p>Complexity: copying on every getter call is O(n) — acceptable for small collections;
     * for large ones return a view or expose {@code size()} / {@code get(i)} instead.
     */
    static void leakOutDemo() {
        System.out.println("=== Leaking internals OUT ===");
        LeakySchedule leaky = new LeakySchedule();
        leaky.add("9:00");
        leaky.slots().clear();                                       // reaches inside!
        System.out.println("leaky size after clear() : " + leaky.size());   // 0

        SafeSchedule safe = new SafeSchedule();
        safe.add("9:00");
        try {
            safe.slots().clear();
        } catch (UnsupportedOperationException e) {
            System.out.println("safe slots().clear()    : refused");
        }
        System.out.println("safe size                : " + safe.size());    // 1
    }

    // ── 3. Leaking internals IN ───────────────────────────────────────────────

    static class LeakyBox {
        private final int[] sides;

        LeakyBox(int[] sides) {
            this.sides = sides;                      // keeps the caller's array
        }

        int volume() {
            int volume = 1;
            for (int side : sides) {
                volume *= side;
            }
            return volume;
        }
    }

    static class SafeBox {
        private final int[] sides;

        SafeBox(int[] sides) {
            int[] copy = sides.clone();              // 1. copy FIRST
            for (int side : copy) {                  // 2. validate the COPY, not the original
                if (side <= 0) {
                    throw new IllegalArgumentException("every side must be positive");
                }
            }
            this.sides = copy;
        }

        int volume() {
            int volume = 1;
            for (int side : sides) {
                volume *= side;
            }
            return volume;
        }

        int[] sides() {
            return sides.clone();                    // copy OUT as well
        }
    }

    /**
     * The same leak runs in the other direction: a constructor or setter that stores the
     * caller's array/list keeps a second owner alive. The caller still holds the reference and
     * can change the object after construction — even after you validated it.
     *
     * <p>WHY this matters: "copy in, copy out" is the rule. Validate the COPY — if you check the
     * original and copy afterwards, another thread (or a sneaky caller) can change the original
     * between the check and the copy (a time-of-check/time-of-use bug).
     *
     * <p>Common gotcha: {@code clone()} on an array is a SHALLOW copy. For an array of mutable
     * objects you must copy each element too.
     */
    static void leakInDemo() {
        System.out.println("=== Leaking internals IN ===");
        int[] dims = {2, 3, 4};
        LeakyBox leaky = new LeakyBox(dims);
        SafeBox safe = new SafeBox(dims);
        dims[0] = -1;                                                // caller mutates its own array
        System.out.println("leaky volume : " + leaky.volume());      // -12  (rule broken)
        System.out.println("safe volume  : " + safe.volume());       // 24
    }

    // ── 4. Views vs snapshots ─────────────────────────────────────────────────

    static List<String> viewOf(List<String> source) {
        return Collections.unmodifiableList(source);
    }

    static List<String> snapshotOf(List<String> source) {
        return List.copyOf(source);
    }

    /**
     * Both {@code Collections.unmodifiableList(list)} and {@code List.copyOf(list)} refuse
     * {@code add}/{@code remove}/{@code clear} with {@code UnsupportedOperationException} — but
     * they are very different objects:
     *
     * <ul>
     *   <li><b>view</b> ({@code unmodifiableList}) — a read-only WINDOW onto the original list.
     *       O(1) to create, but later changes to the original are visible through it.</li>
     *   <li><b>snapshot</b> ({@code List.copyOf}) — an independent copy. O(n) to create, never
     *       changes again.</li>
     * </ul>
     *
     * <p>WHY this matters: returning a view from a getter stops the CALLER from writing, but
     * does not protect the caller from your later writes. Pick the one that matches the
     * promise you want to make.
     *
     * <p>Common gotcha: {@code List.copyOf} (and {@code List.of}) throw
     * {@code NullPointerException} on a {@code null} element.
     */
    static void viewVsSnapshotDemo() {
        System.out.println("=== Views vs snapshots ===");
        List<String> source = new ArrayList<>(List.of("a"));
        List<String> view = viewOf(source);
        List<String> snapshot = snapshotOf(source);
        source.add("b");
        System.out.println("view     : " + view);        // [a, b]  — sees the change
        System.out.println("snapshot : " + snapshot);    // [a]     — does not
    }

    // ── 5. Behaviour over getters/setters ─────────────────────────────────────

    static class AnemicAccount {
        private double balance;                  // private… but with a public setter

        double getBalance() {
            return balance;
        }

        void setBalance(double balance) {
            this.balance = balance;              // no rule, no protection
        }
    }

    /** The "business rule" now lives in every caller — and every caller must remember it. */
    static void anemicWithdraw(AnemicAccount account, double amount) {
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
        }
    }

    /**
     * A private field plus a public getter AND setter is encapsulation in syntax only: anyone
     * can put any value in. The real tool is a method named after what the object DOES
     * ({@code withdraw}, {@code deposit}) that checks the rule and changes the state together
     * — "tell, don't ask".
     *
     * <p>WHY this matters: with getters/setters the rule is copied into every caller
     * ({@code anemicWithdraw}), and one forgetful caller breaks the invariant.
     * {@code Account.withdraw} has it once.
     *
     * <p>Common gotcha: IDE-generated getters and setters for every field are a code smell, not
     * a pattern. Generate a setter only when "set it to anything" is genuinely valid.
     */
    static void behaviourOverSettersDemo() {
        System.out.println("=== Behaviour over getters/setters ===");
        AnemicAccount anemic = new AnemicAccount();
        anemic.setBalance(-50);                                      // private field, still broken
        System.out.println("anemic balance : " + anemic.getBalance());

        Account account = new Account(100);
        account.withdraw(30);
        System.out.println("account balance: " + account.balance()); // 70.0
    }

    // ── 6. Hiding the representation ──────────────────────────────────────────

    interface Stock {
        void add(String sku, int quantity);

        int quantity(String sku);
    }

    static class MapStock implements Stock {
        private final Map<String, Integer> counts = new HashMap<>();

        @Override
        public void add(String sku, int quantity) {
            counts.merge(sku, quantity, Integer::sum);
        }

        @Override
        public int quantity(String sku) {
            return counts.getOrDefault(sku, 0);
        }
    }

    static class ListStock implements Stock {
        private final List<String> skus = new ArrayList<>();
        private final List<Integer> quantities = new ArrayList<>();

        @Override
        public void add(String sku, int quantity) {
            int index = skus.indexOf(sku);
            if (index < 0) {
                skus.add(sku);
                quantities.add(quantity);
            } else {
                quantities.set(index, quantities.get(index) + quantity);
            }
        }

        @Override
        public int quantity(String sku) {
            int index = skus.indexOf(sku);
            return index < 0 ? 0 : quantities.get(index);
        }
    }

    /**
     * The deepest reason for data hiding: freedom to change. {@code MapStock} stores a
     * {@code HashMap}; {@code ListStock} stores two parallel lists. Callers only see
     * {@code Stock}, so you can swap one for the other — for speed, memory or a new requirement —
     * without touching a single caller.
     *
     * <p>Complexity: {@code MapStock} is O(1) per operation; {@code ListStock} is O(n)
     * ({@code indexOf}). Same contract, different cost — and nobody outside had to know.
     *
     * <p>Common gotcha: returning the internal type from the public API
     * ({@code HashMap<String, Integer> counts()}) welds every caller to that representation.
     */
    static void hidingRepresentationDemo() {
        System.out.println("=== Hiding the representation ===");
        for (Stock stock : new Stock[] {new MapStock(), new ListStock()}) {
            stock.add("apple", 3);
            stock.add("apple", 2);
            stock.add("pear", 1);
            System.out.println(stock.getClass().getSimpleName() + " apple=" + stock.quantity("apple")
                    + " pear=" + stock.quantity("pear") + " plum=" + stock.quantity("plum"));
        }
    }
}
