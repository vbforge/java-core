package com.vbforge.design_patterns.builder_pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BuilderDemo — construct complex immutable objects step by step.
 *
 * <p>Topics:
 * <ol>
 *   <li>Why builders — telescoping constructors become unreadable</li>
 *   <li>Fluent builder — named setters, one {@code build()} validation point</li>
 *   <li>Immutability — the built object cannot be mutated</li>
 * </ol>
 */
public class BuilderDemo {

    public static void main(String[] args) {
        telescopingVsBuilderDemo();
        System.out.println("---");
        builderUsageDemo();
        System.out.println("---");
        immutabilityDemo();
    }

    /**
     * A "telescoping" constructor with six parameters is unreadable at the
     * call site — nothing stops you from swapping crust and cheese.
     *
     * <p><b>Why builders:</b> named setters + optional chaining makes each
     * field explicit, order-free, and validated once.
     */
    static void telescopingVsBuilderDemo() {
        System.out.println("=== Telescoping vs builder ===");

        // Telescoping — which boolean is cheese vs extraCheese?
        Pizza telescoping = new Pizza("L", "thin", true, false, List.of("ham"), List.of());
        System.out.println("telescoping: " + telescoping);

        // Builder — every field is named
        Pizza built = Pizza.builder()
                .size("L")
                .crust("thin")
                .cheese(true)
                .toppings("ham")
                .build();
        System.out.println("builder    : " + built);
    }

    /**
     * Fluent builder — the standard call shape.
     */
    static void builderUsageDemo() {
        System.out.println("=== Fluent builder ===");
        Pizza margherita = Pizza.builder()
                .size("M")
                .crust("thin")
                .cheese(true)
                .toppings("tomato", "basil", "mozzarella")
                .build();
        System.out.println("margherita : " + margherita);
        System.out.println("toppings   : " + margherita.toppings().size());
    }

    /**
     * Immutability — the built Pizza has no mutators, and its
     * {@code toppings()} list is an unmodifiable copy.
     */
    static void immutabilityDemo() {
        System.out.println("=== Immutability ===");
        Pizza p = Pizza.builder().size("S").crust("thick").toppings("ham").build();
        try {
            p.toppings().add("olive");
        } catch (UnsupportedOperationException e) {
            System.out.println("mutation blocked");
        }
    }

    // ── Product & Builder ─────────────────────────────────────────────────────

    public static final class Pizza {
        private final String size;
        private final String crust;
        private final boolean cheese;
        private final List<String> toppings;

        /** Canonical private constructor — every public entry point funnels through it. */
        private Pizza(String size, String crust, boolean cheese, List<String> toppings) {
            this.size     = size;
            this.crust    = crust;
            this.cheese   = cheese;
            this.toppings = Collections.unmodifiableList(new ArrayList<>(toppings));
        }

        /**
         * Telescoping constructor — kept only to demonstrate <em>why</em> a builder
         * is preferable. Two booleans in a row at the call site is a bug factory.
         */
        public Pizza(String size, String crust, boolean cheese, boolean extraCheese,
                     List<String> toppings, List<String> extras) {
            this(size, crust, cheese || extraCheese, concat(toppings, extras));
        }

        private Pizza(Builder b) {
            this(b.size, b.crust, b.cheese, b.toppings);
        }

        private static List<String> concat(List<String> a, List<String> b) {
            List<String> out = new ArrayList<>(a);
            out.addAll(b);
            return out;
        }

        public String size()          { return size; }
        public String crust()         { return crust; }
        public boolean cheese()       { return cheese; }
        public List<String> toppings(){ return toppings; }

        public static Builder builder() { return new Builder(); }

        @Override public String toString() {
            return "Pizza{" + size + "," + crust + ",cheese=" + cheese + ",toppings=" + toppings + "}";
        }

        public static final class Builder {
            private String size = "M";
            private String crust = "thin";
            private boolean cheese = false;
            private final List<String> toppings = new ArrayList<>();

            public Builder size(String s)      { this.size = s; return this; }
            public Builder crust(String c)     { this.crust = c; return this; }
            public Builder cheese(boolean b)   { this.cheese = b; return this; }
            public Builder toppings(String... ts) {
                for (String t : ts) this.toppings.add(t);
                return this;
            }

            public Pizza build() {
                if (size == null || crust == null)
                    throw new IllegalStateException("size and crust are required");
                return new Pizza(this);
            }
        }
    }
}