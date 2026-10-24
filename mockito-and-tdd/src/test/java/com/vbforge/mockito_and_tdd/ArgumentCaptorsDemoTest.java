package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.AuditLog;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.BatchAuditor;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.EventPublisher;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.Order;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.OrderLine;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.OrderRepository;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.OrderService;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.RecordingAuditLog;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.RecordingEventPublisher;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.RecordingOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * ArgumentCaptorsDemoTest — verifies every concept shown in {@link ArgumentCaptorsDemo}.
 *
 * <p>These tests are always GREEN. This is where the live {@code ArgumentCaptor} code
 * lives — read it next to the hand-written recorders in the Demo.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ArgumentCaptorsDemo — API contracts")
class ArgumentCaptorsDemoTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private EventPublisher publisher;

    @Mock
    private AuditLog auditLog;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Captor
    private ArgumentCaptor<List<String>> listCaptor;   // @Captor keeps the generic type

    private static List<OrderLine> pensAndInk() {
        return Arrays.asList(new OrderLine("pen", 3, 150), new OrderLine("ink", 1, 250));
    }

    // ── 1. Capturing one argument ─────────────────────────────────────────────

    @Nested
    @DisplayName("1. Capturing one argument")
    class CaptureOneTest {

        @Test
        @DisplayName("captures the Order the service built internally")
        void capturesBuiltOrder() {
            new OrderService(repository, publisher).placeOrder("cust-1", pensAndInk());

            verify(repository).save(orderCaptor.capture());
            Order saved = orderCaptor.getValue();

            assertThat(saved.getCustomerId()).isEqualTo("cust-1");
            assertThat(saved.getTotalCents()).isEqualTo(700);
            assertThat(saved.getLines()).containsExactlyElementsOf(pensAndInk());
        }

        @Test
        @DisplayName("captures String arguments of another collaborator")
        void capturesStrings() {
            new OrderService(repository, publisher).placeOrder("cust-1", pensAndInk());

            verify(publisher).publish(eq("orders.placed"), stringCaptor.capture());

            assertThat(stringCaptor.getValue()).isEqualTo("cust-1:700");
        }

        @Test
        @DisplayName("the hand-written recorder sees the same data as the captor")
        void recorderMatchesCaptor() {
            RecordingOrderRepository recorder = new RecordingOrderRepository();
            RecordingEventPublisher events = new RecordingEventPublisher();
            new OrderService(recorder, events).placeOrder("cust-1", pensAndInk());

            assertThat(recorder.saved()).hasSize(1);
            assertThat(recorder.saved().get(0).getTotalCents()).isEqualTo(700);
            assertThat(events.topics()).containsExactly("orders.placed");
            assertThat(events.payloads()).containsExactly("cust-1:700");
        }

        @Test
        @DisplayName("an empty order is rejected before anything reaches the repository")
        void emptyOrderRejected() {
            OrderService service = new OrderService(repository, publisher);

            assertThatThrownBy(() -> service.placeOrder("cust-1", Collections.<OrderLine>emptyList()))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(repository, publisher);
        }
    }

    // ── 2. Capturing many calls ───────────────────────────────────────────────

    @Nested
    @DisplayName("2. Capturing many calls")
    class CaptureManyTest {

        private List<List<OrderLine>> twoOrders() {
            List<List<OrderLine>> batch = new ArrayList<>();
            batch.add(Collections.singletonList(new OrderLine("pen", 2, 100)));
            batch.add(Collections.singletonList(new OrderLine("ink", 1, 900)));
            return batch;
        }

        @Test
        @DisplayName("getAllValues returns every captured argument in call order")
        void allValues() {
            new OrderService(repository, publisher).placeBatch("cust-1", twoOrders());

            verify(repository, times(2)).save(orderCaptor.capture());

            List<Order> saved = orderCaptor.getAllValues();
            assertThat(saved).hasSize(2);
            assertThat(saved.get(0).getTotalCents()).isEqualTo(200);
            assertThat(saved.get(1).getTotalCents()).isEqualTo(900);
        }

        @Test
        @DisplayName("getValue returns only the LAST captured argument")
        void getValueIsLast() {
            new OrderService(repository, publisher).placeBatch("cust-1", twoOrders());

            verify(repository, times(2)).save(orderCaptor.capture());

            assertThat(orderCaptor.getValue().getTotalCents()).isEqualTo(900);
        }

        @Test
        @DisplayName("gotcha: verify() with the default times(1) fails when called twice")
        void wrongTimes() {
            new OrderService(repository, publisher).placeBatch("cust-1", twoOrders());

            assertThatThrownBy(() -> verify(repository).save(orderCaptor.capture()))
                    .isInstanceOf(TooManyActualInvocations.class);
        }

        @Test
        @DisplayName("gotcha: getValue on a captor that captured nothing throws MockitoException")
        void nothingCaptured() {
            assertThatThrownBy(() -> orderCaptor.getValue())
                    .isInstanceOf(MockitoException.class);
        }
    }

    // ── 3. Captor vs matcher ──────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Captor vs matcher")
    class CaptorVsMatcherTest {

        @Test
        @DisplayName("argThat gives a yes/no check")
        void argThatMatcher() {
            new OrderService(repository, publisher)
                    .placeOrder("cust-1", Collections.singletonList(new OrderLine("pen", 7, 100)));

            verify(repository).save(argThat(order -> order.getTotalCents() == 700));
        }

        @Test
        @DisplayName("a captor gives the object for any assertion")
        void captorInspection() {
            new OrderService(repository, publisher)
                    .placeOrder("cust-1", Collections.singletonList(new OrderLine("pen", 7, 100)));

            verify(repository).save(orderCaptor.capture());

            OrderLine only = orderCaptor.getValue().getLines().get(0);
            assertThat(only.getSku()).isEqualTo("pen");
            assertThat(only.getQuantity()).isEqualTo(7);
            assertThat(only.lineTotalCents()).isEqualTo(700);
        }

        @Test
        @DisplayName("captor.capture() can sit next to eq() in the same call")
        void captorWithOtherMatchers() {
            new OrderService(repository, publisher).placeOrder("cust-1", pensAndInk());

            verify(publisher).publish(eq("orders.placed"), stringCaptor.capture());

            assertThat(stringCaptor.getValue()).startsWith("cust-1:");
        }
    }

    // ── 4. Gotcha: references, not snapshots ──────────────────────────────────

    @Nested
    @DisplayName("4. References, not snapshots")
    class ReferenceNotSnapshotTest {

        @Test
        @DisplayName("a reused buffer makes every captured value look the same")
        void reusedBuffer() {
            new BatchAuditor(auditLog).auditReusingBuffer(Arrays.asList("a", "b", "c"));

            verify(auditLog, times(2)).record(listCaptor.capture());

            List<List<String>> captured = listCaptor.getAllValues();
            assertThat(captured.get(0)).isSameAs(captured.get(1));
            assertThat(captured.get(0)).containsExactly("c");   // the final state, not ["a","b"]
        }

        @Test
        @DisplayName("passing copies keeps each batch intact")
        void copiesAreSafe() {
            new BatchAuditor(auditLog).auditWithCopies(Arrays.asList("a", "b", "c"));

            verify(auditLog, times(2)).record(listCaptor.capture());

            assertThat(listCaptor.getAllValues()).containsExactly(
                    Arrays.asList("a", "b"),
                    Collections.singletonList("c"));
        }

        @Test
        @DisplayName("the hand-written recorder shows the same aliasing")
        void recorderAliasing() {
            RecordingAuditLog shared = new RecordingAuditLog();
            new BatchAuditor(shared).auditReusingBuffer(Arrays.asList("a", "b", "c"));

            assertThat(shared.received()).hasSize(2);
            assertThat(shared.received().get(0)).isSameAs(shared.received().get(1));

            RecordingAuditLog copies = new RecordingAuditLog();
            new BatchAuditor(copies).auditWithCopies(Arrays.asList("a", "b", "c"));

            assertThat(copies.received()).containsExactly(
                    Arrays.asList("a", "b"),
                    Collections.singletonList("c"));
        }
    }
}
