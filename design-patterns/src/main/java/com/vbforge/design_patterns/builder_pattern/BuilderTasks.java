package com.vbforge.design_patterns.builder_pattern;

import java.util.List;

/**
 * BuilderTasks — implement every method below to make {@code BuilderTasksTest} go GREEN.
 *
 * <p>Note: this file also declares {@link PizzaBuilder}, whose fluent methods
 * also throw {@link UnsupportedOperationException}. Implementing the three
 * {@code build*} methods without also implementing {@link PizzaBuilder}
 * is possible (build a {@link Pizza} directly) — but the intended exercise is
 * to write a correct builder and use it.
 */
public class BuilderTasks {

    /** Immutable product — fields set only via {@link PizzaBuilder#build()}. */
    public static final class Pizza {
        private final String size;
        private final String crust;
        private final boolean cheese;
        private final List<String> toppings;

        Pizza(String size, String crust, boolean cheese, List<String> toppings) {
            this.size = size;
            this.crust = crust;
            this.cheese = cheese;
            this.toppings = List.copyOf(toppings);
        }

        public String size()           { return size; }
        public String crust()          { return crust; }
        public boolean cheese()        { return cheese; }
        public List<String> toppings() { return toppings; }
    }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Builds a Margherita pizza via {@link PizzaBuilder}: size {@code "M"},
     * crust {@code "thin"}, cheese {@code true}, toppings
     * {@code ["tomato", "basil", "mozzarella"]}.
     *
     * <p>Examples:
     * <pre>
     *   Pizza p = buildMargherita();
     *   p.size()      → "M"
     *   p.toppings()  → ["tomato", "basil", "mozzarella"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use {@code new PizzaBuilder().size("M").crust("thin")...build()}.</li>
     *   <li>Chain toppings with the varargs form or repeated calls.</li>
     * </ul>
     *
     * @return a Margherita {@link Pizza}
     */
    public Pizza buildMargherita() {
        throw new UnsupportedOperationException("TODO: implement buildMargherita");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Builds a Pepperoni pizza via {@link PizzaBuilder}: size {@code "L"},
     * crust {@code "thick"}, cheese {@code true}, toppings {@code ["pepperoni"]}.
     *
     * <p>Examples:
     * <pre>
     *   Pizza p = buildPepperoni();
     *   p.size()      → "L"
     *   p.crust()     → "thick"
     *   p.toppings()  → ["pepperoni"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Same fluent style as {@link #buildMargherita()} — different values.</li>
     * </ul>
     *
     * @return a Pepperoni {@link Pizza}
     */
    public Pizza buildPepperoni() {
        throw new UnsupportedOperationException("TODO: implement buildPepperoni");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Builds a custom pizza: size {@code size}, crust {@code "thin"},
     * cheese {@code true}, toppings {@code toppings}.
     *
     * <p>Examples:
     * <pre>
     *   buildCustom("S", ["ham", "olive"])
     *     → size "S", crust "thin", cheese true, toppings ["ham","olive"]
     *
     *   buildCustom("L", [])   → cheese only, empty toppings
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use the varargs form {@code toppings(t)} per element, or call
     *       {@code toppings(toppings.toArray(new String[0]))}.</li>
     * </ul>
     *
     * @param size     pizza size (e.g. "S", "M", "L")
     * @param toppings list of topping names
     * @return a custom {@link Pizza}
     */
    public Pizza buildCustom(String size, List<String> toppings) {
        throw new UnsupportedOperationException("TODO: implement buildCustom");
    }

    // ── Task 4 (supporting class) — implement PizzaBuilder ───────────────────

    /**
     * Fluent builder for {@link Pizza}. Each {@code with*} method returns
     * {@code this} to allow chaining.
     *
     * <p>Defaults: {@code size="M"}, {@code crust="thin"}, {@code cheese=false},
     * no toppings. {@link #build()} creates a {@link Pizza} from the current
     * field state.
     */
    public static final class PizzaBuilder {
        private String size = "M";
        private String crust = "thin";
        private boolean cheese = false;
        private final java.util.List<String> toppings = new java.util.ArrayList<>();

        public PizzaBuilder size(String s)      { throw new UnsupportedOperationException("TODO: implement PizzaBuilder.size"); }
        public PizzaBuilder crust(String c)     { throw new UnsupportedOperationException("TODO: implement PizzaBuilder.crust"); }
        public PizzaBuilder cheese(boolean b)   { throw new UnsupportedOperationException("TODO: implement PizzaBuilder.cheese"); }

        public PizzaBuilder toppings(String... ts) {
            throw new UnsupportedOperationException("TODO: implement PizzaBuilder.toppings");
        }

        public Pizza build() {
            throw new UnsupportedOperationException("TODO: implement PizzaBuilder.build");
        }
    }
}