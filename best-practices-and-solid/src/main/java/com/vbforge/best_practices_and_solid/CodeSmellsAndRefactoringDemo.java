package com.vbforge.best_practices_and_solid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * <b>Code smells and refactoring.</b>
 *
 * <p>A <i>smell</i> is a surface sign that the design may have a problem; a
 * <i>refactoring</i> changes structure without changing behaviour. The second half of
 * that sentence is why refactoring needs tests.
 *
 * <p>Sections:
 * <ol>
 *   <li>Long parameter list → parameter object</li>
 *   <li>Primitive obsession → value object</li>
 *   <li>Feature envy → move the method to the data</li>
 *   <li>Refactoring with a safety net</li>
 * </ol>
 */
public class CodeSmellsAndRefactoringDemo {

    // ── 1. Long parameter list → parameter object ─────────────────────────────
    /**
     * What it is: values that always travel together ({@code start}, {@code end}) get
     * their own type.
     *
     * <p>Why it matters: the bad signature lets you pass the arguments in the wrong
     * order; {@link IntRange} checks {@code start <= end} <b>once</b>, in the
     * constructor, and the rule then holds everywhere.
     *
     * <p>Gotcha: do not bundle unrelated parameters just to shorten a signature —
     * the group should be a real concept.
     */
    public static boolean containsBad(int start, int end, int value) {
        return value >= start && value <= end;
    }

    public static final class IntRange {
        private final int start;
        private final int end;

        public IntRange(int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException("start must not be greater than end: " + start + " > " + end);
            }
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public boolean contains(int value) {
            return value >= start && value <= end;
        }

        public int length() {
            return end - start + 1;
        }
    }


    // ── 2. Primitive obsession → value object ─────────────────────────────────
    /**
     * What it is: money as a bare {@code double} versus an immutable {@link Money}
     * (currency + whole cents).
     *
     * <p>Why it matters: {@code 0.1 + 0.2} is {@code 0.30000000000000004} in binary
     * floating point, and a {@code double} cannot stop you adding dollars to euros.
     * {@code Money} makes both mistakes impossible.
     *
     * <p>Complexity: {@code plus} is O(1) and allocates one small object.
     *
     * <p>Gotcha: a value object needs {@code equals}/{@code hashCode} — two
     * {@code Money} of the same currency and cents must be equal.
     */
    public static double addPricesBad(double first, double second) {
        return first + second;
    }

    public static final class Money {
        private final String currency;
        private final long cents;

        private Money(String currency, long cents) {
            this.currency = currency;
            this.cents = cents;
        }

        public static Money of(String currency, long cents) {
            if (currency == null || currency.isEmpty()) {
                throw new IllegalArgumentException("currency must not be empty");
            }
            return new Money(currency, cents);
        }

        public Money plus(Money other) {
            if (!currency.equals(other.currency)) {
                throw new IllegalArgumentException("Currency mismatch: " + currency + " vs " + other.currency);
            }
            return new Money(currency, cents + other.cents);
        }

        public String getCurrency() {
            return currency;
        }

        public long getCents() {
            return cents;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Money)) {
                return false;
            }
            Money other = (Money) o;
            return cents == other.cents && currency.equals(other.currency);
        }

        @Override
        public int hashCode() {
            return 31 * currency.hashCode() + Long.hashCode(cents);
        }

        @Override
        public String toString() {
            long absolute = Math.abs(cents);
            String sign = cents < 0 ? "-" : "";
            return currency + " " + sign + absolute / 100 + "." + String.format(Locale.ROOT, "%02d", absolute % 100);
        }
    }


    // ── 3. Feature envy → move method ─────────────────────────────────────────
    /**
     * What it is: {@link OrderPrinterBad#totalOf} digs through an {@link Order}'s items
     * to compute something that is the order's own business.
     *
     * <p>Why it matters: the total belongs next to the data it needs. As
     * {@link Order#total()} it is reused by every caller and changes in one place
     * (discounts, tax) instead of in every class that "envied" the order.
     */
    public static class LineItem {
        private final String name;
        private final int quantity;
        private final double unitPrice;

        public LineItem(String name, int quantity, double unitPrice) {
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }
    }

    public static class Order {
        private final List<LineItem> items = new ArrayList<>();

        public Order add(LineItem item) {
            items.add(item);
            return this;
        }

        public List<LineItem> getItems() {
            return Collections.unmodifiableList(items);
        }

        public double total() {
            double sum = 0;
            for (LineItem item : items) {
                sum += item.getQuantity() * item.getUnitPrice();
            }
            return sum;
        }
    }

    public static class OrderPrinterBad {
        public double totalOf(Order order) {
            double sum = 0;
            for (LineItem item : order.getItems()) {
                sum += item.getQuantity() * item.getUnitPrice();
            }
            return sum;
        }
    }


    // ── 4. Refactoring with a safety net ──────────────────────────────────────
    /**
     * What it is: {@link #legacyPrice} is the "old" code; {@link #price} is the
     * refactored version (guard clauses + named constants). Both must return the same
     * number for every input.
     *
     * <p>Why it matters: a refactoring is only safe if something can prove behaviour
     * did not change. Run old and new side by side over many inputs
     * (a <i>characterization test</i>) before deleting the old code.
     *
     * <p>Complexity: checking n inputs costs O(n) — cheap compared with one production bug.
     *
     * <p>Gotcha: change structure <b>or</b> behaviour in one commit, never both.
     */
    public static int legacyPrice(int quantity) {
        int result;
        if (quantity >= 0) {
            if (quantity < 10) {
                result = quantity * 100;
            } else {
                if (quantity < 50) {
                    result = quantity * 90;
                } else {
                    result = quantity * 80;
                }
            }
        } else {
            throw new IllegalArgumentException("quantity must be >= 0");
        }
        return result;
    }

    private static final int BULK_FROM = 10;
    private static final int WHOLESALE_FROM = 50;
    private static final int REGULAR_UNIT_PRICE = 100;
    private static final int BULK_UNIT_PRICE = 90;
    private static final int WHOLESALE_UNIT_PRICE = 80;

    public static int price(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be >= 0");
        }
        if (quantity >= WHOLESALE_FROM) {
            return quantity * WHOLESALE_UNIT_PRICE;
        }
        if (quantity >= BULK_FROM) {
            return quantity * BULK_UNIT_PRICE;
        }
        return quantity * REGULAR_UNIT_PRICE;
    }


    // ── Demo runners ──────────────────────────────────────────────────────────
    static void parameterObjectDemo() {
        System.out.println("containsBad(5, 1, 3)       = " + containsBad(5, 1, 3) + "  (arguments swapped - silently wrong)");
        try {
            new IntRange(5, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("new IntRange(5, 1)         -> " + e.getMessage());
        }
    }

    static void valueObjectDemo() {
        System.out.println("0.1 + 0.2 as double        = " + addPricesBad(0.1, 0.2));
        System.out.println("Money 0.10 + 0.20          = " + Money.of("USD", 10).plus(Money.of("USD", 20)));
    }

    static void featureEnvyDemo() {
        Order order = new Order().add(new LineItem("pen", 2, 1.5)).add(new LineItem("book", 1, 4.0));
        System.out.println("printer computes: " + new OrderPrinterBad().totalOf(order));
        System.out.println("order computes  : " + order.total());
    }

    static void safetyNetDemo() {
        int differences = 0;
        for (int quantity = 0; quantity <= 1000; quantity++) {
            if (legacyPrice(quantity) != price(quantity)) {
                differences++;
            }
        }
        System.out.println("inputs where old and new differ: " + differences);
    }

    public static void main(String[] args) {
        parameterObjectDemo();
        System.out.println("---");
        valueObjectDemo();
        System.out.println("---");
        featureEnvyDemo();
        System.out.println("---");
        safetyNetDemo();
    }
}
