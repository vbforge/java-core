package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.AccountDirectory;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.ChargeStatus;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.GatewayException;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.Notifier;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.PaymentGateway;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.ReceiptPrinter;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.SubscriptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * MockitoBasicsDemoTest — verifies every concept shown in {@link MockitoBasicsDemo}.
 *
 * <p>These tests are always GREEN. This is where the live Mockito code lives — the
 * Demo class cannot use Mockito because it is a test-scope dependency. Read these
 * tests as the executable version of the Javadoc snippets in the Demo.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MockitoBasicsDemo — API contracts")
class MockitoBasicsDemoTest {

    @Mock
    private PaymentGateway gateway;

    @Mock
    private Notifier notifier;

    @Mock
    private AccountDirectory directory;

    @InjectMocks
    private SubscriptionService service;

    // ── 1. Default answers ────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Default answers")
    class DefaultAnswersTest {

        @Test
        @DisplayName("@Mock creates a mock object")
        void isAMock() {
            assertThat(mockingDetails(gateway).isMock()).isTrue();
        }

        @Test
        @DisplayName("unstubbed boolean → false, int → 0")
        void primitivesDefault() {
            assertThat(gateway.isAvailable()).isFalse();
            assertThat(directory.balanceOf("acc-1")).isZero();
        }

        @Test
        @DisplayName("unstubbed Optional → empty, List → empty")
        void containersDefault() {
            assertThat(directory.findOwner("acc-1")).isEmpty();
            assertThat(directory.accountsOf("ann")).isEmpty();
        }

        @Test
        @DisplayName("unstubbed enum return → null")
        void objectsDefaultToNull() {
            assertThat(gateway.charge("acc-1", 500)).isNull();
        }

        @Test
        @DisplayName("gotcha: a null status silently looks like a decline")
        void nullStatusLooksDeclined() {
            ChargeStatus result = service.renew("acc-1", 500);

            assertThat(result).isNull();
            verify(notifier).send("acc-1", "Payment declined");
        }
    }

    // ── 2. Stubbing ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Stubbing")
    class StubbingTest {

        @Test
        @DisplayName("when/thenReturn dictates the answer")
        void thenReturn() {
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.APPROVED);

            assertThat(service.renew("acc-1", 500)).isEqualTo(ChargeStatus.APPROVED);
            verify(notifier).send("acc-1", "Renewed");
        }

        @Test
        @DisplayName("consecutive answers: DECLINED, then APPROVED forever after")
        void consecutiveAnswers() {
            when(gateway.charge("acc-1", 500))
                    .thenReturn(ChargeStatus.DECLINED, ChargeStatus.APPROVED);

            assertThat(service.renew("acc-1", 500)).isEqualTo(ChargeStatus.DECLINED);
            assertThat(service.renew("acc-1", 500)).isEqualTo(ChargeStatus.APPROVED);
            assertThat(service.renew("acc-1", 500)).isEqualTo(ChargeStatus.APPROVED);
        }

        @Test
        @DisplayName("thenThrow simulates a failing collaborator")
        void thenThrow() {
            when(gateway.charge(anyString(), anyLong())).thenThrow(new GatewayException("timeout"));

            assertThat(service.renew("acc-1", 500)).isEqualTo(ChargeStatus.DECLINED);
            verify(notifier).send("acc-1", "Gateway error: timeout");
        }

        @Test
        @DisplayName("argument matchers: eq() for one argument, anyLong() for another")
        void matchers() {
            when(gateway.charge(eq("vip"), anyLong())).thenReturn(ChargeStatus.APPROVED);

            assertThat(service.renew("vip", 1)).isEqualTo(ChargeStatus.APPROVED);
            assertThat(service.renew("vip", 999_999)).isEqualTo(ChargeStatus.APPROVED);
        }

        @Test
        @DisplayName("void methods are stubbed with doThrow(...).when(mock)")
        void doThrowForVoid() {
            doThrow(new GatewayException("down")).when(gateway).refund("acc-1", 100);

            assertThatThrownBy(() -> service.cancel("acc-1", 100))
                    .isInstanceOf(GatewayException.class)
                    .hasMessage("down");
            verifyNoInteractions(notifier);   // refund failed, so nobody was told "Cancelled"
        }

        @Test
        @DisplayName("gotcha: mixing a matcher with a raw value throws InvalidUseOfMatchersException")
        void mixingMatchersAndValues() {
            assertThatThrownBy(() -> when(gateway.charge(anyString(), 500L)))
                    .isInstanceOf(InvalidUseOfMatchersException.class);
        }
    }

    // ── 3. Verification ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Verification")
    class VerificationTest {

        @Test
        @DisplayName("verify(mock) means exactly once")
        void defaultIsOnce() {
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.APPROVED);

            service.renew("acc-1", 500);

            verify(notifier).send("acc-1", "Renewed");
        }

        @Test
        @DisplayName("times(n) checks the exact number of calls")
        void timesN() {
            when(gateway.charge(anyString(), anyLong())).thenReturn(ChargeStatus.APPROVED);

            service.renew("acc-1", 500);
            service.renew("acc-1", 500);

            verify(notifier, times(2)).send("acc-1", "Renewed");
            verify(gateway, times(2)).charge("acc-1", 500);
        }

        @Test
        @DisplayName("never() asserts a call did not happen")
        void neverCalled() {
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.DECLINED);

            service.renew("acc-1", 500);

            verify(notifier, never()).send("acc-1", "Renewed");
        }

        @Test
        @DisplayName("verifyNoMoreInteractions fails on calls you did not verify")
        void noMoreInteractions() {
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.APPROVED);

            service.renew("acc-1", 500);

            verify(notifier).send("acc-1", "Renewed");
            verifyNoMoreInteractions(notifier);
        }

        @Test
        @DisplayName("InOrder checks that refund happens before the notification")
        void inOrderVerification() {
            service.cancel("acc-1", 100);

            InOrder order = inOrder(gateway, notifier);
            order.verify(gateway).refund("acc-1", 100);
            order.verify(notifier).send("acc-1", "Cancelled");
        }

        @Test
        @DisplayName("matchers work inside verify — all arguments must be matchers")
        void matchersInVerify() {
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.APPROVED);

            service.renew("acc-1", 500);

            verify(notifier).send(eq("acc-1"), startsWith("Ren"));
        }

        @Test
        @DisplayName("a failed verification throws WantedButNotInvoked")
        void failedVerification() {
            assertThatThrownBy(() -> verify(notifier).send("acc-1", "never sent"))
                    .isInstanceOf(WantedButNotInvoked.class);
        }
    }

    // ── 4. Strict stubs and @InjectMocks ──────────────────────────────────────

    @Nested
    @DisplayName("4. Strict stubs and @InjectMocks")
    class StrictStubsTest {

        @Test
        @DisplayName("@InjectMocks builds the service with the @Mock fields")
        void injectMocks() {
            when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.APPROVED);

            service.renew("acc-1", 500);

            verify(gateway).charge("acc-1", 500);
            verify(notifier).send("acc-1", "Renewed");
        }

        @Test
        @DisplayName("lenient() opts a single stub out of the unused-stub check")
        void lenientStub() {
            lenient().when(gateway.isAvailable()).thenReturn(true);   // never used — and that is fine

            assertThat(service.renew("acc-1", 500)).isNull();
        }
    }

    // ── 5. Spies ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Spies")
    class SpiesTest {

        @Test
        @DisplayName("a spy runs the real methods by default")
        void realMethodsByDefault() {
            ReceiptPrinter printer = spy(new ReceiptPrinter());

            assertThat(printer.line("Total", 1999)).isEqualTo("Total: $19.99");
            assertThat(printer.money(5)).isEqualTo("$0.05");
            assertThat(printer.money(-50)).isEqualTo("-$0.50");
        }

        @Test
        @DisplayName("doReturn stubs one method; the rest stay real")
        void partialStubbing() {
            ReceiptPrinter printer = spy(new ReceiptPrinter());
            doReturn("$$$").when(printer).money(1999);

            assertThat(printer.line("Total", 1999)).isEqualTo("Total: $$$");
            assertThat(printer.money(5)).isEqualTo("$0.05");
        }

        @Test
        @DisplayName("calls made inside the spied object are recorded too")
        void verifyInternalCalls() {
            ReceiptPrinter printer = spy(new ReceiptPrinter());

            printer.line("Total", 1999);

            verify(printer).money(1999);
        }

        @Test
        @DisplayName("gotcha: when(spy.method()) runs the REAL method while stubbing")
        void whenOnSpyRunsRealMethod() {
            List<String> spyList = spy(new ArrayList<String>());

            assertThatThrownBy(() -> when(spyList.get(0)).thenReturn("x"))
                    .isInstanceOf(IndexOutOfBoundsException.class);

            doReturn("x").when(spyList).get(0);   // safe form: the real get(0) is never called
            assertThat(spyList.get(0)).isEqualTo("x");
        }
    }
}
