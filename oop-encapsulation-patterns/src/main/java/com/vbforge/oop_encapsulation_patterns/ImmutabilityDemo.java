package com.vbforge.oop_encapsulation_patterns;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * ImmutabilityDemo — annotated reference for immutable objects in Java.
 *
 * <p>Read top-to-bottom. Each section covers one facet of immutability with
 * concrete examples and a note explaining <em>why</em>.
 *
 * <p>Topics:
 * <ol>
 *   <li>{@code final} is not immutable — a frozen reference vs a frozen object</li>
 *   <li>The recipe for an immutable class — {@code Money}</li>
 *   <li>Shallow vs deep immutability — a "frozen" holder of mutable parts</li>
 *   <li>Why it pays off — safe keys in hash collections</li>
 *   <li>The JDK's immutables — and the ignored-return-value bug</li>
 *   <li>Gotchas — a non-{@code final} class can be un-frozen by a subclass</li>
 * </ol>
 */
public class ImmutabilityDemo {

    public static void main(String[] args) {
        finalIsNotImmutableDemo();
        System.out.println("---");
        recipeDemo();
        System.out.println("---");
        shallowVsDeepDemo();
        System.out.println("---");
        safeKeysDemo();
        System.out.println("---");
        jdkImmutablesDemo();
        System.out.println("---");
        gotchasDemo();
    }

    // ── 1. final is not immutable ─────────────────────────────────────────────

    static int[] finalArrayStillMutable() {
        final int[] numbers = {1, 2, 3};
        numbers[0] = 99;                             // legal: the REFERENCE is final, the array is not
        return numbers;
    }

    /**
     * {@code final} on a variable or field means "this reference can never point somewhere else".
     * It says nothing about the object it points to. An object is IMMUTABLE when its state can
     * never change after construction — that is a property of the CLASS, not of the variable.
     *
     * <p>WHY this matters: people read {@code private final List<String> items} and assume the list
     * is frozen. It is not — {@code items.add(...)} still works. {@code final} is necessary for
     * an immutable class, never sufficient.
     *
     * <p>Common gotcha: a {@code final} local {@code int[]} can still be edited element by
     * element ({@code numbers[0] = 99} below).
     */
    static void finalIsNotImmutableDemo() {
        System.out.println("=== final is not immutable ===");
        int[] after = finalArrayStillMutable();
        System.out.println("final int[] after edit : " + after[0]);      // 99
        // after = new int[0];  // would not compile if 'after' were declared final
    }

    // ── 2. The recipe ─────────────────────────────────────────────────────────

    /**
     * An immutable value class. The five-step recipe:
     * <ol>
     *   <li>declare the class {@code final} (no subclass can add mutability)</li>
     *   <li>make every field {@code private final}</li>
     *   <li>provide no setters or any method that changes a field</li>
     *   <li>copy mutable inputs/outputs (see section 3)</li>
     *   <li>"change" by returning a NEW object</li>
     * </ol>
     */
    static final class Money {
        private final long cents;
        private final String currency;

        Money(long cents, String currency) {
            if (currency == null || currency.isEmpty()) {
                throw new IllegalArgumentException("currency must not be empty");
            }
            this.cents = cents;
            this.currency = currency;
        }

        long cents() {
            return cents;
        }

        String currency() {
            return currency;
        }

        Money plus(Money other) {
            if (!currency.equals(other.currency)) {
                throw new IllegalArgumentException("currency mismatch");
            }
            return new Money(Math.addExact(cents, other.cents), currency);   // a NEW object
        }

        Money times(int factor) {
            return new Money(Math.multiplyExact(cents, (long) factor), currency);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Money)) {
                return false;
            }
            Money money = (Money) o;
            return cents == money.cents && currency.equals(money.currency);
        }

        @Override
        public int hashCode() {
            return Objects.hash(cents, currency);
        }

        @Override
        public String toString() {
            return currency + " " + cents + "c";
        }
    }

    /**
     * {@code plus} and {@code times} never touch {@code this}; they build and return a new
     * {@code Money}. The original keeps its value forever, so it can be shared freely.
     *
     * <p>WHY this matters: an immutable object is trivially thread-safe (nothing to
     * synchronise), safe to share and cache, easy to reason about (a value cannot change
     * between two lines of code), and a safe {@code HashMap} key.
     *
     * <p>Complexity: every "change" allocates a new object, O(1) for a small value class. For a
     * large structure that is changed in a loop (string concatenation!) use a mutable builder
     * ({@code StringBuilder}) and freeze the result once.
     */
    static void recipeDemo() {
        System.out.println("=== The recipe ===");
        Money ten = new Money(1000, "EUR");
        Money five = new Money(500, "EUR");
        Money sum = ten.plus(five);
        System.out.println("ten.plus(five) : " + sum);                   // EUR 1500c
        System.out.println("ten unchanged  : " + ten);                   // EUR 1000c
        System.out.println("same object?   : " + (sum == ten));          // false
    }

    // ── 3. Shallow vs deep ────────────────────────────────────────────────────

    static final class ShallowSnapshot {
        private final List<StringBuilder> parts;

        ShallowSnapshot(List<StringBuilder> parts) {
            this.parts = List.copyOf(parts);         // the LIST is frozen, the StringBuilders are not
        }

        String text() {
            StringBuilder all = new StringBuilder();
            for (StringBuilder part : parts) {
                all.append(part);
            }
            return all.toString();
        }
    }

    static final class DeepSnapshot {
        private final List<String> parts;

        DeepSnapshot(List<String> parts) {
            this.parts = List.copyOf(parts);         // Strings are immutable → truly frozen
        }

        String text() {
            return String.join("", parts);
        }
    }

    /**
     * Immutability must go ALL the way down. A class whose fields are {@code final} but point to
     * mutable objects is only SHALLOWLY immutable. {@code ShallowSnapshot} froze the list, but
     * the {@code StringBuilder}s inside are still shared with the caller.
     *
     * <p>WHY this matters: the whole point — "this object never changes" — is lost the moment
     * one nested part can change. Store immutable parts ({@code String}, {@code Integer},
     * {@code LocalDate}, other immutable classes) or copy the mutable ones deeply.
     *
     * <p>Common gotcha: {@code List.copyOf} copies the list, not the elements.
     */
    static void shallowVsDeepDemo() {
        System.out.println("=== Shallow vs deep ===");
        StringBuilder part = new StringBuilder("ab");
        ShallowSnapshot shallow = new ShallowSnapshot(List.of(part));
        DeepSnapshot deep = new DeepSnapshot(List.of(part.toString()));
        part.append("c");
        System.out.println("shallow text : " + shallow.text());          // abc  (changed!)
        System.out.println("deep text    : " + deep.text());             // ab
    }

    // ── 4. Safe keys ──────────────────────────────────────────────────────────

    static final class MutablePoint {
        int x;
        int y;

        MutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MutablePoint)) {
                return false;
            }
            MutablePoint other = (MutablePoint) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static final class FixedPoint {
        private final int x;
        private final int y;

        FixedPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        FixedPoint withX(int newX) {
            return new FixedPoint(newX, y);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof FixedPoint)) {
                return false;
            }
            FixedPoint other = (FixedPoint) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /**
     * A {@code HashSet} files an element in a bucket chosen from its {@code hashCode()} at the
     * moment of insertion. If the element's fields change afterwards, its hash changes, but it
     * stays in the OLD bucket — {@code contains(...)} then looks in the wrong place and reports
     * {@code false}, even though the very same object is in the set.
     *
     * <p>WHY this matters: an immutable key cannot change after insertion, so the lookup always
     * works. This is why {@code String} and {@code Integer} are the classic map keys.
     *
     * <p>Common gotcha: the bug is silent — no exception, the set simply "loses" the element.
     */
    static void safeKeysDemo() {
        System.out.println("=== Safe keys ===");
        Set<MutablePoint> mutableSet = new HashSet<>();
        MutablePoint mutable = new MutablePoint(1, 2);
        mutableSet.add(mutable);
        mutable.x = 99;                                              // changes the hash AFTER insertion
        System.out.println("mutable key found? : " + mutableSet.contains(mutable));   // false

        Set<FixedPoint> fixedSet = new HashSet<>();
        FixedPoint fixed = new FixedPoint(1, 2);
        fixedSet.add(fixed);
        FixedPoint moved = fixed.withX(99);                          // a new object; 'fixed' is untouched
        System.out.println("fixed key found?   : " + fixedSet.contains(fixed));       // true
        System.out.println("moved key found?   : " + fixedSet.contains(moved));       // false (never added)
    }

    // ── 5. JDK immutables ─────────────────────────────────────────────────────

    static String ignoredReturnValue() {
        String text = "java";
        text.toUpperCase(Locale.ROOT);               // BUG: result thrown away, 'text' is unchanged
        return text;
    }

    static String keptReturnValue() {
        String text = "java";
        text = text.toUpperCase(Locale.ROOT);        // reassign the NEW object
        return text;
    }

    /**
     * The JDK is full of immutable classes: {@code String}, the wrappers ({@code Integer},
     * {@code Long}, …), {@code BigDecimal}, {@code BigInteger}, {@code java.time.*}
     * ({@code LocalDate}), {@code Optional}, and the collections from {@code List.of},
     * {@code Set.of}, {@code Map.of}. Their "modifying" methods all return a NEW object.
     *
     * <p>WHY this matters: you already use immutability every day, and it has one classic bug —
     * calling {@code text.toUpperCase()} or {@code total.add(x)} and forgetting to use the
     * result. The original is never changed, so the line does nothing.
     *
     * <p>Common gotcha: {@code List.of(...)} is immutable, {@code Arrays.asList(...)} is only
     * fixed-SIZE (you can still {@code set}), and {@code Collections.unmodifiableList} is a view.
     */
    static void jdkImmutablesDemo() {
        System.out.println("=== JDK immutables ===");
        System.out.println("ignored result : " + ignoredReturnValue());          // java
        System.out.println("kept result    : " + keptReturnValue());             // JAVA

        BigDecimal total = new BigDecimal("10.00");
        total.add(new BigDecimal("5.00"));                                       // result ignored
        System.out.println("BigDecimal total (ignored add) : " + total);        // 10.00

        LocalDate jan31 = LocalDate.of(2024, 1, 31);
        System.out.println("plusMonths(1) : " + jan31.plusMonths(1));            // 2024-02-29
        System.out.println("original      : " + jan31);                          // 2024-01-31
    }

    // ── 6. Gotchas ────────────────────────────────────────────────────────────

    /** Looks immutable (private final field, no setters) — but the CLASS is not final. */
    static class OpenPoint {
        private final int x;

        OpenPoint(int x) {
            this.x = x;
        }

        int x() {
            return x;
        }
    }

    /** A subclass that overrides the accessor: the "immutable" parent now changes on every read. */
    static class MovingPoint extends OpenPoint {
        private int calls;

        MovingPoint() {
            super(0);
        }

        @Override
        int x() {
            return ++calls;
        }
    }

    static boolean isFinalClass(Class<?> type) {
        return java.lang.reflect.Modifier.isFinal(type.getModifiers());
    }

    /**
     * Private final fields are not enough: if the class can be subclassed, a subclass can
     * override the accessors and return different values on every call. Code that received an
     * {@code OpenPoint} and trusted it to be immutable is now wrong.
     *
     * <p>WHY this matters: declaring the class {@code final} (step 1 of the recipe) closes the
     * hole. {@code Money} above is final; {@code OpenPoint} is not.
     *
     * <p>Common gotcha: never let {@code this} escape from a constructor (registering it in a
     * static list, starting a thread) — another thread could see the object half-built.
     */
    static void gotchasDemo() {
        System.out.println("=== Gotchas ===");
        OpenPoint point = new MovingPoint();
        System.out.println("x() first call  : " + point.x());                    // 1
        System.out.println("x() second call : " + point.x());                    // 2
        System.out.println("Money is final  : " + isFinalClass(Money.class));    // true
        System.out.println("OpenPoint final : " + isFinalClass(OpenPoint.class));// false
    }
}
