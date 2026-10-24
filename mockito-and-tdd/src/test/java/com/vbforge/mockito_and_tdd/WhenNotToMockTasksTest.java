package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.FakeTaxService;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * WhenNotToMockTasksTest — tests for every method in {@link WhenNotToMockTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code WhenNotToMockTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=WhenNotToMockTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 3 task groups are GREEN.</li>
 * </ol>
 * Note: there is not a single mock in this file — real values and an in-memory fake only.
 */
@DisplayName("WhenNotToMockTasks — your implementations")
class WhenNotToMockTasksTest {

    private WhenNotToMockTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new WhenNotToMockTasks();
    }

    // ── Task 1: totalWithTax ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement totalWithTax()")
    @DisplayName("Task 1 — totalWithTax()")
    class TotalWithTaxTest {

        @Test
        @DisplayName("[1000, 500] in DE (19%) → 1785")
        void basic() {
            FakeTaxService tax = new FakeTaxService().with("DE", 1900);

            assertThat(tasks.totalWithTax(tax, Arrays.asList(Money.of(1000), Money.of(500)), "DE"))
                    .isEqualTo(Money.of(1785));
        }

        @Test
        @DisplayName("0% tax leaves the sum unchanged")
        void zeroTax() {
            FakeTaxService tax = new FakeTaxService().with("US", 0);

            assertThat(tasks.totalWithTax(tax, Collections.singletonList(Money.of(999)), "US"))
                    .isEqualTo(Money.of(999));
        }

        @Test
        @DisplayName("rounds down: 1 cent at 19% stays 1 cent")
        void roundsDown() {
            FakeTaxService tax = new FakeTaxService().with("DE", 1900);

            assertThat(tasks.totalWithTax(tax, Collections.singletonList(Money.of(1)), "DE"))
                    .isEqualTo(Money.of(1));
        }

        @Test
        @DisplayName("empty list → Money(0)")
        void emptyList() {
            FakeTaxService tax = new FakeTaxService().with("DE", 1900);

            assertThat(tasks.totalWithTax(tax, Collections.<Money>emptyList(), "DE"))
                    .isEqualTo(Money.of(0));
        }

        @Test
        @DisplayName("unknown region → the service's IllegalArgumentException propagates")
        void unknownRegion() {
            FakeTaxService tax = new FakeTaxService().with("DE", 1900);

            assertThatThrownBy(() ->
                    tasks.totalWithTax(tax, Collections.singletonList(Money.of(1000)), "XX"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("unknown region: XX");
        }

        @Test
        @DisplayName("large amounts do not overflow int arithmetic")
        void noOverflow() {
            FakeTaxService tax = new FakeTaxService().with("HU", 2700);

            assertThat(tasks.totalWithTax(tax, Collections.singletonList(Money.of(2_000_000_000L)), "HU"))
                    .isEqualTo(Money.of(2_540_000_000L));
        }

        @Test
        @DisplayName("the tax service is asked for the region (at least once)")
        void asksTheService() {
            FakeTaxService tax = new FakeTaxService().with("DE", 1900);

            tasks.totalWithTax(tax, Collections.singletonList(Money.of(100)), "DE");

            assertThat(tax.lookups()).isGreaterThanOrEqualTo(1);
        }
    }

    // ── Task 2: cheapest ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement cheapest()")
    @DisplayName("Task 2 — cheapest()")
    class CheapestTest {

        @Test
        @DisplayName("[300, 150, 900] → 150")
        void basic() {
            assertThat(tasks.cheapest(Arrays.asList(Money.of(300), Money.of(150), Money.of(900))))
                    .contains(Money.of(150));
        }

        @Test
        @DisplayName("single element → that element")
        void single() {
            assertThat(tasks.cheapest(Collections.singletonList(Money.of(42)))).contains(Money.of(42));
        }

        @Test
        @DisplayName("ties → a minimal value")
        void ties() {
            assertThat(tasks.cheapest(Arrays.asList(Money.of(5), Money.of(5)))).contains(Money.of(5));
        }

        @Test
        @DisplayName("empty list → Optional.empty")
        void empty() {
            assertThat(tasks.cheapest(Collections.<Money>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("the input list is not modified (not sorted in place)")
        void inputUntouched() {
            List<Money> input = Arrays.asList(Money.of(300), Money.of(150), Money.of(900));

            tasks.cheapest(input);

            assertThat(input).containsExactly(Money.of(300), Money.of(150), Money.of(900));
        }
    }

    // ── Task 3: splitEvenly ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement splitEvenly()")
    @DisplayName("Task 3 — splitEvenly()")
    class SplitEvenlyTest {

        @Test
        @DisplayName("100 cents / 3 → [34, 33, 33]")
        void leftoverGoesToFirstParts() {
            assertThat(tasks.splitEvenly(Money.of(100), 3))
                    .containsExactly(Money.of(34), Money.of(33), Money.of(33));
        }

        @Test
        @DisplayName("10 / 2 → [5, 5]")
        void evenSplit() {
            assertThat(tasks.splitEvenly(Money.of(10), 2)).containsExactly(Money.of(5), Money.of(5));
        }

        @Test
        @DisplayName("one way → the whole amount")
        void oneWay() {
            assertThat(tasks.splitEvenly(Money.of(5), 1)).containsExactly(Money.of(5));
        }

        @Test
        @DisplayName("2 cents / 5 → [1, 1, 0, 0, 0]")
        void moreWaysThanCents() {
            assertThat(tasks.splitEvenly(Money.of(2), 5)).containsExactly(
                    Money.of(1), Money.of(1), Money.of(0), Money.of(0), Money.of(0));
        }

        @Test
        @DisplayName("zero amount → all zeros")
        void zeroAmount() {
            assertThat(tasks.splitEvenly(Money.of(0), 3))
                    .containsExactly(Money.of(0), Money.of(0), Money.of(0));
        }

        @ParameterizedTest(name = "{0} cents / {1} ways → parts sum to {0}")
        @CsvSource({
                "100, 3",
                "1,   7",
                "999, 10",
                "12345, 4",
        })
        @DisplayName("parts always add up to the original and differ by at most one cent")
        void partsAddUp(long cents, int ways) {
            List<Money> parts = tasks.splitEvenly(Money.of(cents), ways);

            long sum = 0;
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            for (Money part : parts) {
                sum += part.cents();
                min = Math.min(min, part.cents());
                max = Math.max(max, part.cents());
            }
            assertThat(parts).hasSize(ways);
            assertThat(sum).isEqualTo(cents);
            assertThat(max - min).isLessThanOrEqualTo(1);
        }

        @Test
        @DisplayName("ways < 1 → IllegalArgumentException")
        void invalidWays() {
            assertThatThrownBy(() -> tasks.splitEvenly(Money.of(10), 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
