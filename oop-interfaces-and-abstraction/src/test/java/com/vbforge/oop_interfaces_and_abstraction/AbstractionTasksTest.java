package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.AbstractionTasks.CanPrint;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionTasks.CanScan;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionTasks.Counter;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionTasks.DoublingCounter;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionTasks.FixedCounter;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionTasks.ShippingPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AbstractionTasksTest — tests for every method in {@link AbstractionTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code AbstractionTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=AbstractionTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("AbstractionTasks — your implementations")
class AbstractionTasksTest {

    private AbstractionTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new AbstractionTasks();
    }

    private static class Printer implements CanPrint {
        @Override
        public String print(String document) {
            return document;
        }
    }

    private static class Scanner implements CanScan {
        @Override
        public String scan() {
            return "page";
        }
    }

    private static class AllInOne implements CanPrint, CanScan {
        @Override
        public String print(String document) {
            return document;
        }

        @Override
        public String scan() {
            return "page";
        }
    }

    // ── Task 1: flatRate ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement flatRate()")
    @DisplayName("Task 1 — flatRate()")
    class FlatRateTest {

        @ParameterizedTest(name = "flatRate(5.0).cost({0}) → 5.0")
        @CsvSource({"0.0", "10.0", "49.99", "500.0", "1000000.0"})
        @DisplayName("the cost does not depend on the order total")
        void sameForEveryTotal(double orderTotal) {
            assertThat(tasks.flatRate(5.0).cost(orderTotal)).isEqualTo(5.0);
        }

        @Test
        @DisplayName("flatRate(0.0) is free")
        void zeroRate() {
            assertThat(tasks.flatRate(0.0).cost(10.0)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("every call creates an independent policy")
        void independentPolicies() {
            ShippingPolicy a = tasks.flatRate(3.0);
            ShippingPolicy b = tasks.flatRate(8.0);
            assertThat(a.cost(1.0)).isEqualTo(3.0);
            assertThat(b.cost(1.0)).isEqualTo(8.0);
        }
    }

    // ── Task 2: freeOver ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement freeOver()")
    @DisplayName("Task 2 — freeOver()")
    class FreeOverTest {

        @ParameterizedTest(name = "freeOver(50, 5).cost({0}) → {1}")
        @CsvSource({
                "0.0,    5.0",
                "49.99,  5.0",
                "50.0,   0.0",
                "50.01,  0.0",
                "200.0,  0.0",
        })
        @DisplayName("threshold 50, rate 5")
        void boundaries(double orderTotal, double expectedCost) {
            assertThat(tasks.freeOver(50.0, 5.0).cost(orderTotal)).isEqualTo(expectedCost);
        }

        @Test
        @DisplayName("the rate is the one passed in")
        void rateIsConfigurable() {
            assertThat(tasks.freeOver(100.0, 12.5).cost(10.0)).isEqualTo(12.5);
        }

        @Test
        @DisplayName("two policies from the same factory do not interfere")
        void independentPolicies() {
            ShippingPolicy low = tasks.freeOver(10.0, 1.0);
            ShippingPolicy high = tasks.freeOver(100.0, 9.0);
            assertThat(low.cost(20.0)).isEqualTo(0.0);
            assertThat(high.cost(20.0)).isEqualTo(9.0);
        }
    }

    // ── Task 3: totalShipping ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement totalShipping()")
    @DisplayName("Task 3 — totalShipping()")
    class TotalShippingTest {

        private final ShippingPolicy flat5 = orderTotal -> 5.0;
        private final ShippingPolicy freeOver50 = orderTotal -> orderTotal >= 50.0 ? 0.0 : 5.0;

        @Test
        @DisplayName("[10, 60, 20] with a flat rate of 5 → 15.0")
        void flatRate() {
            assertThat(tasks.totalShipping(Arrays.asList(10.0, 60.0, 20.0), flat5)).isEqualTo(15.0);
        }

        @Test
        @DisplayName("[10, 60, 20] free over 50 → 10.0")
        void freeOver() {
            assertThat(tasks.totalShipping(Arrays.asList(10.0, 60.0, 20.0), freeOver50)).isEqualTo(10.0);
        }

        @Test
        @DisplayName("no orders → 0.0")
        void emptyList() {
            assertThat(tasks.totalShipping(new ArrayList<Double>(), flat5)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("works with any implementation: lambda or anonymous class")
        void anyImplementation() {
            ShippingPolicy anonymous = new ShippingPolicy() {
                @Override
                public double cost(double orderTotal) {
                    return orderTotal / 10;
                }
            };
            assertThat(tasks.totalShipping(Arrays.asList(10.0, 20.0), anonymous)).isEqualTo(3.0);
        }

        @Test
        @DisplayName("the policy is asked once per order, in order")
        void askedOncePerOrder() {
            final List<Double> seen = new ArrayList<>();
            ShippingPolicy recording = orderTotal -> {
                seen.add(orderTotal);
                return 1.0;
            };
            tasks.totalShipping(Arrays.asList(7.0, 8.0, 9.0), recording);
            assertThat(seen).containsExactly(7.0, 8.0, 9.0);
        }
    }

    // ── Task 4: FixedCounter.step ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement FixedCounter.step()")
    @DisplayName("Task 4 — FixedCounter.step()")
    class FixedCounterStepTest {

        @Test
        @DisplayName("start 0, step 5, two increments → 10")
        void basic() {
            Counter counter = new FixedCounter(0, 5);
            counter.increment();
            counter.increment();
            assertThat(counter.value()).isEqualTo(10);
        }

        @Test
        @DisplayName("start 100, step 1 → 101")
        void stepOfOne() {
            Counter counter = new FixedCounter(100, 1);
            counter.increment();
            assertThat(counter.value()).isEqualTo(101);
        }

        @Test
        @DisplayName("step 0 → the value never changes")
        void zeroStep() {
            Counter counter = new FixedCounter(7, 0);
            counter.increment();
            assertThat(counter.value()).isEqualTo(7);
        }

        @Test
        @DisplayName("a negative step counts down")
        void negativeStep() {
            Counter counter = new FixedCounter(10, -3);
            counter.increment();
            counter.increment();
            assertThat(counter.value()).isEqualTo(4);
        }

        @Test
        @DisplayName("the value is unchanged until increment() is called")
        void noIncrementNoChange() {
            assertThat(new FixedCounter(3, 9).value()).isEqualTo(3);
        }
    }

    // ── Task 5: DoublingCounter.step ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement DoublingCounter.step()")
    @DisplayName("Task 5 — DoublingCounter.step()")
    class DoublingCounterStepTest {

        @Test
        @DisplayName("start 1 → 2, 4, 8")
        void doubling() {
            Counter counter = new DoublingCounter(1);
            counter.increment();
            assertThat(counter.value()).isEqualTo(2);
            counter.increment();
            assertThat(counter.value()).isEqualTo(4);
            counter.increment();
            assertThat(counter.value()).isEqualTo(8);
        }

        @Test
        @DisplayName("start 5 → 10")
        void startFive() {
            Counter counter = new DoublingCounter(5);
            counter.increment();
            assertThat(counter.value()).isEqualTo(10);
        }

        @Test
        @DisplayName("start 0 stays 0")
        void startZero() {
            Counter counter = new DoublingCounter(0);
            counter.increment();
            assertThat(counter.value()).isZero();
        }

        @Test
        @DisplayName("a doubling counter starting at a negative value doubles in magnitude")
        void negativeStart() {
            Counter counter = new DoublingCounter(-3);
            counter.increment();
            assertThat(counter.value()).isEqualTo(-6);
        }
    }

    // ── Task 6: capabilities ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement capabilities()")
    @DisplayName("Task 6 — capabilities()")
    class CapabilitiesTest {

        @Test
        @DisplayName("printer only → [print]")
        void printerOnly() {
            assertThat(tasks.capabilities(new Printer())).containsExactly("print");
        }

        @Test
        @DisplayName("scanner only → [scan]")
        void scannerOnly() {
            assertThat(tasks.capabilities(new Scanner())).containsExactly("scan");
        }

        @Test
        @DisplayName("all-in-one → [print, scan] in that order")
        void allInOne() {
            assertThat(tasks.capabilities(new AllInOne())).containsExactly("print", "scan");
        }

        @Test
        @DisplayName("an unrelated object → []")
        void unrelatedObject() {
            assertThat(tasks.capabilities("a string")).isEmpty();
        }

        @Test
        @DisplayName("null → [] without throwing")
        void nullDevice() {
            assertThat(tasks.capabilities(null)).isEmpty();
        }

        @Test
        @DisplayName("lambdas and anonymous classes are detected by their interface")
        void lambdaAndAnonymous() {
            CanPrint lambda = document -> document;
            CanScan anonymous = new CanScan() {
                @Override
                public String scan() {
                    return "x";
                }
            };
            assertThat(tasks.capabilities(lambda)).containsExactly("print");
            assertThat(tasks.capabilities(anonymous)).containsExactly("scan");
        }
    }
}
