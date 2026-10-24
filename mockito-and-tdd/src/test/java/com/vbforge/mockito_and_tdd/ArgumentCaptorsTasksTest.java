package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.EventPublisher;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.Order;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.OrderLine;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * ArgumentCaptorsTasksTest — tests for every method in {@link ArgumentCaptorsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ArgumentCaptorsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ArgumentCaptorsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 3 task groups are GREEN.</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ArgumentCaptorsTasks — your implementations")
class ArgumentCaptorsTasksTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private EventPublisher publisher;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Captor
    private ArgumentCaptor<String> payloadCaptor;

    private ArgumentCaptorsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ArgumentCaptorsTasks();
    }

    // ── Task 1: splitAndSave ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement splitAndSave()")
    @DisplayName("Task 1 — splitAndSave()")
    class SplitAndSaveTest {

        @Test
        @DisplayName("[2x pen@100, 1x ink@300, 5x clip@10] → three one-line orders")
        void threeLines() {
            List<OrderLine> lines = Arrays.asList(
                    new OrderLine("pen", 2, 100),
                    new OrderLine("ink", 1, 300),
                    new OrderLine("clip", 5, 10));

            tasks.splitAndSave(repository, "cust-1", lines);

            verify(repository, times(3)).save(orderCaptor.capture());
            List<Order> saved = orderCaptor.getAllValues();
            assertThat(saved).extracting(Order::getTotalCents).containsExactly(200L, 300L, 50L);
            assertThat(saved).extracting(Order::getCustomerId)
                    .containsExactly("cust-1", "cust-1", "cust-1");
        }

        @Test
        @DisplayName("each saved order holds exactly its own line, in input order")
        void oneLineEach() {
            OrderLine pen = new OrderLine("pen", 2, 100);
            OrderLine ink = new OrderLine("ink", 1, 300);

            tasks.splitAndSave(repository, "cust-1", Arrays.asList(pen, ink));

            verify(repository, times(2)).save(orderCaptor.capture());
            assertThat(orderCaptor.getAllValues().get(0).getLines()).containsExactly(pen);
            assertThat(orderCaptor.getAllValues().get(1).getLines()).containsExactly(ink);
        }

        @Test
        @DisplayName("single line → exactly one order")
        void singleLine() {
            tasks.splitAndSave(repository, "cust-7",
                    Collections.singletonList(new OrderLine("pen", 4, 25)));

            verify(repository).save(orderCaptor.capture());
            assertThat(orderCaptor.getValue().getCustomerId()).isEqualTo("cust-7");
            assertThat(orderCaptor.getValue().getTotalCents()).isEqualTo(100);
        }

        @Test
        @DisplayName("empty list → the repository is never called")
        void emptyList() {
            tasks.splitAndSave(repository, "cust-1", Collections.<OrderLine>emptyList());

            verifyNoInteractions(repository);
        }
    }

    // ── Task 2: publishSummary ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement publishSummary()")
    @DisplayName("Task 2 — publishSummary()")
    class PublishSummaryTest {

        @Test
        @DisplayName("[2x pen@100, 1x ink@300] → \"2 lines, 500 cents\"")
        void twoLines() {
            tasks.publishSummary(publisher,
                    Arrays.asList(new OrderLine("pen", 2, 100), new OrderLine("ink", 1, 300)));

            verify(publisher).publish(eq("orders.summary"), payloadCaptor.capture());
            assertThat(payloadCaptor.getValue()).isEqualTo("2 lines, 500 cents");
        }

        @Test
        @DisplayName("[4x clip@25] → \"1 lines, 100 cents\" (no pluralisation)")
        void oneLine() {
            tasks.publishSummary(publisher,
                    Collections.singletonList(new OrderLine("clip", 4, 25)));

            verify(publisher).publish(eq("orders.summary"), payloadCaptor.capture());
            assertThat(payloadCaptor.getValue()).isEqualTo("1 lines, 100 cents");
        }

        @Test
        @DisplayName("empty list → \"0 lines, 0 cents\" is still published")
        void emptyList() {
            tasks.publishSummary(publisher, Collections.<OrderLine>emptyList());

            verify(publisher).publish(eq("orders.summary"), payloadCaptor.capture());
            assertThat(payloadCaptor.getValue()).isEqualTo("0 lines, 0 cents");
        }

        @Test
        @DisplayName("exactly one event is published")
        void exactlyOneEvent() {
            tasks.publishSummary(publisher, Arrays.asList(
                    new OrderLine("a", 1, 1), new OrderLine("b", 1, 1), new OrderLine("c", 1, 1)));

            verify(publisher, times(1)).publish(anyString(), anyString());
        }
    }

    // ── Task 3: applyDiscount ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement applyDiscount()")
    @DisplayName("Task 3 — applyDiscount()")
    class ApplyDiscountTest {

        private Order order() {
            return new Order("c1",
                    Arrays.asList(new OrderLine("A", 2, 1000), new OrderLine("B", 1, 500)),
                    2500);
        }

        @Test
        @DisplayName("50% off → unit prices halved, total recomputed (1250)")
        void halfPrice() {
            tasks.applyDiscount(repository, order(), 50);

            verify(repository).save(orderCaptor.capture());
            Order saved = orderCaptor.getValue();
            assertThat(saved.getCustomerId()).isEqualTo("c1");
            assertThat(saved.getLines()).containsExactly(
                    new OrderLine("A", 2, 500), new OrderLine("B", 1, 250));
            assertThat(saved.getTotalCents()).isEqualTo(1250);
        }

        @Test
        @DisplayName("0% off → equal lines and total, but a NEW Order object")
        void zeroPercent() {
            Order original = order();

            tasks.applyDiscount(repository, original, 0);

            verify(repository).save(orderCaptor.capture());
            assertThat(orderCaptor.getValue()).isNotSameAs(original);
            assertThat(orderCaptor.getValue().getLines()).isEqualTo(original.getLines());
            assertThat(orderCaptor.getValue().getTotalCents()).isEqualTo(2500);
        }

        @Test
        @DisplayName("100% off → unit price 0 and total 0")
        void fullDiscount() {
            tasks.applyDiscount(repository, order(), 100);

            verify(repository).save(orderCaptor.capture());
            assertThat(orderCaptor.getValue().getLines()).containsExactly(
                    new OrderLine("A", 2, 0), new OrderLine("B", 1, 0));
            assertThat(orderCaptor.getValue().getTotalCents()).isZero();
        }

        @Test
        @DisplayName("unit price rounds down: 999 with 10% off → 899")
        void roundsDown() {
            Order single = new Order("c1", Collections.singletonList(new OrderLine("A", 1, 999)), 999);

            tasks.applyDiscount(repository, single, 10);

            verify(repository).save(orderCaptor.capture());
            assertThat(orderCaptor.getValue().getLines().get(0).getUnitCents()).isEqualTo(899);
            assertThat(orderCaptor.getValue().getTotalCents()).isEqualTo(899);
        }

        @Test
        @DisplayName("the original order is not modified")
        void originalUntouched() {
            Order original = order();

            tasks.applyDiscount(repository, original, 50);

            assertThat(original.getTotalCents()).isEqualTo(2500);
            assertThat(original.getLines().get(0).getUnitCents()).isEqualTo(1000);
        }

        @Test
        @DisplayName("percent outside 0..100 → IllegalArgumentException and nothing saved")
        void invalidPercent() {
            assertThatThrownBy(() -> tasks.applyDiscount(repository, order(), -1))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.applyDiscount(repository, order(), 101))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(repository);
        }
    }
}
