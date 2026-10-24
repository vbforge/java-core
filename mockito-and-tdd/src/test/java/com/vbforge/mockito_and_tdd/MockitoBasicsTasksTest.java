package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.ChargeStatus;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.GatewayException;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.Notifier;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * MockitoBasicsTasksTest — tests for every method in {@link MockitoBasicsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code MockitoBasicsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=MockitoBasicsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 3 task groups are GREEN.</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MockitoBasicsTasks — your implementations")
class MockitoBasicsTasksTest {

    @Mock
    private PaymentGateway gateway;

    @Mock
    private Notifier notifier;

    private MockitoBasicsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new MockitoBasicsTasks();
    }

    // ── Task 1: checkout ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement checkout()")
    @DisplayName("Task 1 — checkout()")
    class CheckoutTest {

        @Test
        @DisplayName("approved charge → APPROVED and the customer is notified")
        void approved() {
            when(gateway.isAvailable()).thenReturn(true);
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.APPROVED);

            ChargeStatus result = tasks.checkout(gateway, notifier, "acc-1", 500);

            assertThat(result).isEqualTo(ChargeStatus.APPROVED);
            verify(notifier).send("acc-1", "Charged 500 cents");
        }

        @Test
        @DisplayName("declined charge → DECLINED and nobody is notified")
        void declined() {
            when(gateway.isAvailable()).thenReturn(true);
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.DECLINED);

            ChargeStatus result = tasks.checkout(gateway, notifier, "acc-1", 500);

            assertThat(result).isEqualTo(ChargeStatus.DECLINED);
            verifyNoInteractions(notifier);
        }

        @Test
        @DisplayName("gateway down → IllegalStateException")
        void unavailable() {
            when(gateway.isAvailable()).thenReturn(false);

            assertThatThrownBy(() -> tasks.checkout(gateway, notifier, "acc-1", 500))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("gateway unavailable");
        }

        @Test
        @DisplayName("gateway down → charge is never attempted and nobody is notified")
        void unavailableDoesNothing() {
            when(gateway.isAvailable()).thenReturn(false);

            assertThatThrownBy(() -> tasks.checkout(gateway, notifier, "acc-1", 500))
                    .isInstanceOf(IllegalStateException.class);

            verify(gateway, never()).charge(anyString(), anyLong());
            verifyNoInteractions(notifier);
        }

        @Test
        @DisplayName("amount and account are passed to the gateway unchanged")
        void passesArguments() {
            when(gateway.isAvailable()).thenReturn(true);
            when(gateway.charge("acc-9", 12_345)).thenReturn(ChargeStatus.APPROVED);

            tasks.checkout(gateway, notifier, "acc-9", 12_345);

            verify(gateway).charge("acc-9", 12_345);
            verify(notifier).send("acc-9", "Charged 12345 cents");
        }
    }

    // ── Task 2: chargeWithRetry ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement chargeWithRetry()")
    @DisplayName("Task 2 — chargeWithRetry()")
    class ChargeWithRetryTest {

        @Test
        @DisplayName("first attempt succeeds → one call")
        void firstAttempt() {
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.APPROVED);

            assertThat(tasks.chargeWithRetry(gateway, "acc-1", 500, 3))
                    .isEqualTo(ChargeStatus.APPROVED);

            verify(gateway, times(1)).charge("acc-1", 500);
        }

        @Test
        @DisplayName("throw, throw, APPROVED → APPROVED after 3 calls")
        void succeedsOnThirdAttempt() {
            when(gateway.charge("acc-1", 500))
                    .thenThrow(new GatewayException("t1"))
                    .thenThrow(new GatewayException("t2"))
                    .thenReturn(ChargeStatus.APPROVED);

            assertThat(tasks.chargeWithRetry(gateway, "acc-1", 500, 3))
                    .isEqualTo(ChargeStatus.APPROVED);

            verify(gateway, times(3)).charge("acc-1", 500);
        }

        @Test
        @DisplayName("every attempt throws → the LAST exception is rethrown")
        void allAttemptsFail() {
            when(gateway.charge("acc-1", 500))
                    .thenThrow(new GatewayException("t1"))
                    .thenThrow(new GatewayException("t2"))
                    .thenThrow(new GatewayException("t3"));

            assertThatThrownBy(() -> tasks.chargeWithRetry(gateway, "acc-1", 500, 3))
                    .isInstanceOf(GatewayException.class)
                    .hasMessage("t3");

            verify(gateway, times(3)).charge("acc-1", 500);
        }

        @Test
        @DisplayName("DECLINED is an answer, not an error → returned without retrying")
        void declinedIsNotRetried() {
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.DECLINED);

            assertThat(tasks.chargeWithRetry(gateway, "acc-1", 500, 5))
                    .isEqualTo(ChargeStatus.DECLINED);

            verify(gateway, times(1)).charge("acc-1", 500);
        }

        @Test
        @DisplayName("maxAttempts = 1 → a single failure is rethrown immediately")
        void singleAttempt() {
            when(gateway.charge("acc-1", 500)).thenThrow(new GatewayException("boom"));

            assertThatThrownBy(() -> tasks.chargeWithRetry(gateway, "acc-1", 500, 1))
                    .isInstanceOf(GatewayException.class)
                    .hasMessage("boom");

            verify(gateway, times(1)).charge("acc-1", 500);
        }

        @Test
        @DisplayName("maxAttempts < 1 → IllegalArgumentException and the gateway is untouched")
        void invalidAttempts() {
            assertThatThrownBy(() -> tasks.chargeWithRetry(gateway, "acc-1", 500, 0))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(gateway);
        }
    }

    // ── Task 3: chargeAll ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement chargeAll()")
    @DisplayName("Task 3 — chargeAll()")
    class ChargeAllTest {

        @Test
        @DisplayName("returns the declined account ids")
        void returnsDeclined() {
            when(gateway.charge("acc-1", 100)).thenReturn(ChargeStatus.APPROVED);
            when(gateway.charge("acc-2", 200)).thenReturn(ChargeStatus.DECLINED);
            when(gateway.charge("acc-3", 300)).thenReturn(ChargeStatus.APPROVED);

            Map<String, Long> amounts = new LinkedHashMap<>();
            amounts.put("acc-1", 100L);
            amounts.put("acc-2", 200L);
            amounts.put("acc-3", 300L);

            assertThat(tasks.chargeAll(gateway, amounts)).containsExactly("acc-2");
        }

        @Test
        @DisplayName("charges happen in map order, and a decline does not stop the run")
        void chargesInOrder() {
            when(gateway.charge("acc-1", 100)).thenReturn(ChargeStatus.DECLINED);
            when(gateway.charge("acc-2", 200)).thenReturn(ChargeStatus.APPROVED);
            when(gateway.charge("acc-3", 300)).thenReturn(ChargeStatus.DECLINED);

            Map<String, Long> amounts = new LinkedHashMap<>();
            amounts.put("acc-1", 100L);
            amounts.put("acc-2", 200L);
            amounts.put("acc-3", 300L);

            assertThat(tasks.chargeAll(gateway, amounts)).containsExactly("acc-1", "acc-3");

            InOrder order = inOrder(gateway);
            order.verify(gateway).charge("acc-1", 100);
            order.verify(gateway).charge("acc-2", 200);
            order.verify(gateway).charge("acc-3", 300);
        }

        @Test
        @DisplayName("all approved → empty list")
        void allApproved() {
            when(gateway.charge(anyString(), anyLong())).thenReturn(ChargeStatus.APPROVED);

            Map<String, Long> amounts = new LinkedHashMap<>();
            amounts.put("acc-1", 100L);
            amounts.put("acc-2", 200L);

            assertThat(tasks.chargeAll(gateway, amounts)).isEmpty();
        }

        @Test
        @DisplayName("empty map → empty list and the gateway is untouched")
        void emptyMap() {
            assertThat(tasks.chargeAll(gateway, new LinkedHashMap<String, Long>())).isEmpty();

            verifyNoInteractions(gateway);
        }
    }
}
