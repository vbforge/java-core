package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.DefaultAndStaticMethodsTasks.AmphibiousRobot;
import com.vbforge.oop_interfaces_and_abstraction.DefaultAndStaticMethodsTasks.Swimmer;
import com.vbforge.oop_interfaces_and_abstraction.DefaultAndStaticMethodsTasks.Taxable;
import com.vbforge.oop_interfaces_and_abstraction.DefaultAndStaticMethodsTasks.Validator;
import com.vbforge.oop_interfaces_and_abstraction.DefaultAndStaticMethodsTasks.Walker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * DefaultAndStaticMethodsTasksTest — tests for every method in {@link DefaultAndStaticMethodsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code DefaultAndStaticMethodsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=DefaultAndStaticMethodsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("DefaultAndStaticMethodsTasks — your implementations")
class DefaultAndStaticMethodsTasksTest {

    private DefaultAndStaticMethodsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new DefaultAndStaticMethodsTasks();
    }

    private static Taxable taxable(final double price) {
        return () -> price;
    }

    private static final Validator LONGER_THAN_3 = value -> value != null && value.length() > 3;
    private static final Validator STARTS_WITH_A = value -> value != null && value.startsWith("A");

    // ── Task 1: Taxable.priceWithTax ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Taxable.priceWithTax()")
    @DisplayName("Task 1 — Taxable.priceWithTax()")
    class PriceWithTaxTest {

        @ParameterizedTest(name = "price {0}, rate {1} → {2}")
        @CsvSource({
                "100.0, 0.25, 125.0",
                "80.0,  0.5,  120.0",
                "100.0, 0.0,  100.0",
                "0.0,   0.2,  0.0",
                "40.0,  1.0,  80.0",
        })
        @DisplayName("parametrized price/rate combinations")
        void parametrized(double price, double rate, double expected) {
            assertThat(taxable(price).priceWithTax(rate)).isCloseTo(expected, within(1e-9));
        }

        @Test
        @DisplayName("the default uses the implementer's price()")
        void usesAbstractMethod() {
            Taxable item = new Taxable() {
                @Override
                public double price() {
                    return 200.0;
                }
            };
            assertThat(item.priceWithTax(0.5)).isEqualTo(300.0);
        }

        @Test
        @DisplayName("price() itself is not changed by the call")
        void priceUnchanged() {
            Taxable item = taxable(100.0);
            item.priceWithTax(0.25);
            assertThat(item.price()).isEqualTo(100.0);
        }
    }

    // ── Task 2: Validator.and ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Validator.and()")
    @DisplayName("Task 2 — Validator.and()")
    class AndTest {

        @Test
        @DisplayName("both conditions hold → valid")
        void bothHold() {
            assertThat(LONGER_THAN_3.and(STARTS_WITH_A).isValid("Apple")).isTrue();
        }

        @Test
        @DisplayName("first condition fails → invalid")
        void firstFails() {
            assertThat(LONGER_THAN_3.and(STARTS_WITH_A).isValid("Ape")).isFalse();
        }

        @Test
        @DisplayName("second condition fails → invalid")
        void secondFails() {
            assertThat(LONGER_THAN_3.and(STARTS_WITH_A).isValid("Banana")).isFalse();
        }

        @Test
        @DisplayName("both conditions fail → invalid")
        void bothFail() {
            assertThat(LONGER_THAN_3.and(STARTS_WITH_A).isValid("be")).isFalse();
        }

        @Test
        @DisplayName("the second validator is NOT consulted when the first one rejects")
        void shortCircuit() {
            final int[] calls = {0};
            Validator counting = value -> {
                calls[0]++;
                return true;
            };
            LONGER_THAN_3.and(counting).isValid("Ape");
            assertThat(calls[0]).isZero();
            LONGER_THAN_3.and(counting).isValid("Apple");
            assertThat(calls[0]).isEqualTo(1);
        }

        @Test
        @DisplayName("and() can be chained")
        void chained() {
            Validator endsWithE = value -> value != null && value.endsWith("e");
            Validator all = LONGER_THAN_3.and(STARTS_WITH_A).and(endsWithE);
            assertThat(all.isValid("Apple")).isTrue();
            assertThat(all.isValid("Apply")).isFalse();
        }
    }

    // ── Task 3: Validator.negate ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Validator.negate()")
    @DisplayName("Task 3 — Validator.negate()")
    class NegateTest {

        @Test
        @DisplayName("accepts what the original rejects")
        void acceptsRejected() {
            assertThat(LONGER_THAN_3.negate().isValid("Ape")).isTrue();
        }

        @Test
        @DisplayName("rejects what the original accepts")
        void rejectsAccepted() {
            assertThat(LONGER_THAN_3.negate().isValid("Apple")).isFalse();
        }

        @Test
        @DisplayName("negating twice gives the original behaviour")
        void doubleNegation() {
            Validator twice = LONGER_THAN_3.negate().negate();
            assertThat(twice.isValid("Apple")).isTrue();
            assertThat(twice.isValid("Ape")).isFalse();
        }

        @Test
        @DisplayName("the original validator is not modified")
        void originalUnchanged() {
            LONGER_THAN_3.negate();
            assertThat(LONGER_THAN_3.isValid("Apple")).isTrue();
        }
    }

    // ── Task 4: Validator.notEmpty ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Validator.notEmpty()")
    @DisplayName("Task 4 — Validator.notEmpty()")
    class NotEmptyTest {

        @Test
        @DisplayName("\"a\" → true")
        void normalText() {
            assertThat(Validator.notEmpty().isValid("a")).isTrue();
        }

        @Test
        @DisplayName("a string of spaces counts as not empty")
        void spacesOnly() {
            assertThat(Validator.notEmpty().isValid(" ")).isTrue();
        }

        @Test
        @DisplayName("empty string → false")
        void emptyString() {
            assertThat(Validator.notEmpty().isValid("")).isFalse();
        }

        @Test
        @DisplayName("null → false, without throwing")
        void nullValue() {
            assertThat(Validator.notEmpty().isValid(null)).isFalse();
        }
    }

    // ── Task 5: AmphibiousRobot.move ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement AmphibiousRobot.move()")
    @DisplayName("Task 5 — AmphibiousRobot.move()")
    class AmphibiousRobotMoveTest {

        @Test
        @DisplayName("\"walk then swim\"")
        void basic() {
            assertThat(new AmphibiousRobot().move()).isEqualTo("walk then swim");
        }

        @Test
        @DisplayName("the result is built from the interfaces' own defaults")
        void builtFromDefaults() {
            assertThat(new Walker() { }.move()).isEqualTo("walk");
            assertThat(new Swimmer() { }.move()).isEqualTo("swim");
            assertThat(new AmphibiousRobot().move()).startsWith("walk").endsWith("swim");
        }

        @Test
        @DisplayName("the robot is both a Walker and a Swimmer")
        void bothTypes() {
            assertThat(new AmphibiousRobot()).isInstanceOf(Walker.class).isInstanceOf(Swimmer.class);
        }

        @Test
        @DisplayName("the override is chosen when called through either interface")
        void throughInterfaces() {
            Walker asWalker = new AmphibiousRobot();
            Swimmer asSwimmer = new AmphibiousRobot();
            assertThat(asWalker.move()).isEqualTo("walk then swim");
            assertThat(asSwimmer.move()).isEqualTo("walk then swim");
        }
    }

    // ── Task 6: defaultMethodNames ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement defaultMethodNames()")
    @DisplayName("Task 6 — defaultMethodNames()")
    class DefaultMethodNamesTest {

        @Test
        @DisplayName("Taxable → [priceWithTax]")
        void taxable() {
            assertThat(tasks.defaultMethodNames(Taxable.class)).containsExactly("priceWithTax");
        }

        @Test
        @DisplayName("Validator → [and, negate] (the static notEmpty and abstract isValid are excluded)")
        void validator() {
            assertThat(tasks.defaultMethodNames(Validator.class)).containsExactly("and", "negate");
        }

        @Test
        @DisplayName("JDK: Iterable → [forEach, spliterator]")
        void iterable() {
            assertThat(tasks.defaultMethodNames(Iterable.class)).containsExactly("forEach", "spliterator");
        }

        @Test
        @DisplayName("an interface without defaults → []")
        void noDefaults() {
            assertThat(tasks.defaultMethodNames(Runnable.class)).isEmpty();
        }

        @Test
        @DisplayName("only DECLARED defaults count: AmphibiousRobot's interfaces are separate")
        void walkerAndSwimmer() {
            assertThat(tasks.defaultMethodNames(Walker.class)).containsExactly("move");
            assertThat(tasks.defaultMethodNames(Swimmer.class)).containsExactly("move");
        }
    }
}
