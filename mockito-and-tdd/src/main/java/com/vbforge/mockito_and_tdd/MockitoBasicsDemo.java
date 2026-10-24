package com.vbforge.mockito_and_tdd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * MockitoBasicsDemo — annotated reference for Mockito's core workflow:
 * create a mock, stub it with {@code when}, run the code, check it with {@code verify}.
 *
 * <p>Mockito is a <em>test-scope</em> dependency, so this class (which lives in
 * {@code src/main}) cannot call it. Instead each section below runs the system under
 * test with a hand-made collaborator and prints what happens, and its Javadoc shows
 * the Mockito one-liner that does the same job. <b>The live Mockito code is in
 * {@code MockitoBasicsDemoTest}</b> — read the two files side by side.
 *
 * <p>Topics:
 * <ol>
 *   <li>Default answers — what an unconfigured mock returns</li>
 *   <li>Stubbing — {@code when/thenReturn/thenThrow}, {@code doThrow}, argument matchers</li>
 *   <li>Verification — {@code verify}, {@code times}, {@code never}, {@code InOrder}</li>
 *   <li>Strict stubs and {@code @InjectMocks} — keeping tests honest</li>
 *   <li>Spies — wrapping a real object and overriding only part of it</li>
 * </ol>
 *
 * <p>The system under test is {@link SubscriptionService}, which depends on a
 * {@link PaymentGateway} and a {@link Notifier}.
 */
public class MockitoBasicsDemo {

    public static void main(String[] args) {
        defaultAnswersDemo();
        System.out.println("---");
        stubbingDemo();
        System.out.println("---");
        verificationDemo();
        System.out.println("---");
        strictStubsDemo();
        System.out.println("---");
        spyDemo();
    }

    // ── Domain: collaborators and the system under test ───────────────────────

    public enum ChargeStatus { APPROVED, DECLINED }

    /** Thrown by a gateway that cannot reach the bank. Unchecked on purpose. */
    public static class GatewayException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public GatewayException(String message) {
            super(message);
        }
    }

    public interface PaymentGateway {
        boolean isAvailable();
        ChargeStatus charge(String accountId, long cents);
        void refund(String accountId, long cents);
    }

    public interface Notifier {
        void send(String accountId, String message);
    }

    /** A lookup service — used to show what unconfigured mocks return. */
    public interface AccountDirectory {
        Optional<String> findOwner(String accountId);
        List<String> accountsOf(String owner);
        int balanceOf(String accountId);
    }

    /** The system under test. */
    public static class SubscriptionService {
        private final PaymentGateway gateway;
        private final Notifier notifier;

        public SubscriptionService(PaymentGateway gateway, Notifier notifier) {
            this.gateway = gateway;
            this.notifier = notifier;
        }

        /** Charges the account; always tells the customer what happened. */
        public ChargeStatus renew(String accountId, long cents) {
            try {
                ChargeStatus status = gateway.charge(accountId, cents);
                notifier.send(accountId,
                        status == ChargeStatus.APPROVED ? "Renewed" : "Payment declined");
                return status;
            } catch (GatewayException e) {
                notifier.send(accountId, "Gateway error: " + e.getMessage());
                return ChargeStatus.DECLINED;
            }
        }

        /** Refunds first, and only then tells the customer. */
        public void cancel(String accountId, long refundCents) {
            gateway.refund(accountId, refundCents);
            notifier.send(accountId, "Cancelled");
        }
    }

    /** A real class with two methods that call each other — the spy playground. */
    public static class ReceiptPrinter {
        public String money(long cents) {
            long abs = Math.abs(cents);
            long rest = abs % 100;
            return (cents < 0 ? "-" : "") + "$" + (abs / 100) + "." + (rest < 10 ? "0" : "") + rest;
        }

        public String line(String label, long cents) {
            return label + ": " + money(cents);
        }
    }

    // ── Hand-made collaborators (what Mockito generates for you) ──────────────

    /** A gateway scripted to approve, decline or fail on every charge. */
    public static class ScriptedGateway implements PaymentGateway {
        private final ChargeStatus status;
        private final GatewayException failure;

        public ScriptedGateway(ChargeStatus status) {
            this.status = status;
            this.failure = null;
        }

        public ScriptedGateway(GatewayException failure) {
            this.status = null;
            this.failure = failure;
        }

        @Override public boolean isAvailable() { return true; }

        @Override
        public ChargeStatus charge(String accountId, long cents) {
            if (failure != null) throw failure;
            return status;
        }

        @Override public void refund(String accountId, long cents) { }
    }

    /** A notifier that remembers every message — a hand-written spy. */
    public static class RecordingNotifier implements Notifier {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void send(String accountId, String message) {
            messages.add(accountId + ": " + message);
        }

        public List<String> messages() {
            return Collections.unmodifiableList(messages);
        }
    }

    /** A gateway that records which of its methods were called. */
    public static class RecordingGateway implements PaymentGateway {
        private final List<String> calls = new ArrayList<>();

        @Override
        public boolean isAvailable() {
            calls.add("isAvailable");
            return true;
        }

        @Override
        public ChargeStatus charge(String accountId, long cents) {
            calls.add("charge");
            return ChargeStatus.APPROVED;
        }

        @Override
        public void refund(String accountId, long cents) {
            calls.add("refund");
        }

        public List<String> calls() {
            return Collections.unmodifiableList(calls);
        }
    }

    // ── 1. Default answers ────────────────────────────────────────────────────

    /**
     * A mock you have not stubbed still answers every call — with the "zero value" of
     * the return type.
     *
     * <p>Mockito's defaults ({@code RETURNS_DEFAULTS}):
     * <ul>
     *   <li>{@code boolean} → false, numbers → 0</li>
     *   <li>collections → empty collection, {@code Optional} → {@code Optional.empty()}</li>
     *   <li>everything else (enums, your own classes, {@code String}) → {@code null}</li>
     * </ul>
     *
     * <p>WHY this matters: it lets you mock a wide interface and stub only what the test
     * cares about. Mockito equivalent: {@code AccountDirectory d = mock(AccountDirectory.class);}
     *
     * <p>Common gotcha: an unstubbed method returning an enum or object gives {@code null}.
     * Below, the service quietly treats a {@code null} status as "declined" — the test
     * passes for the wrong reason unless you stub the gateway.
     */
    static void defaultAnswersDemo() {
        System.out.println("=== Default answers ===");

        // What RETURNS_DEFAULTS gives you, spelled out by hand
        AccountDirectory defaults = new AccountDirectory() {
            @Override public Optional<String> findOwner(String accountId) { return Optional.empty(); }
            @Override public List<String> accountsOf(String owner) { return Collections.emptyList(); }
            @Override public int balanceOf(String accountId) { return 0; }
        };
        System.out.println("findOwner     : " + defaults.findOwner("acc-1"));    // Optional.empty
        System.out.println("accountsOf    : " + defaults.accountsOf("ann"));     // []
        System.out.println("balanceOf     : " + defaults.balanceOf("acc-1"));    // 0

        PaymentGateway unstubbed = new PaymentGateway() {
            @Override public boolean isAvailable() { return false; }
            @Override public ChargeStatus charge(String accountId, long cents) { return null; }
            @Override public void refund(String accountId, long cents) { }
        };
        RecordingNotifier notifier = new RecordingNotifier();
        ChargeStatus status = new SubscriptionService(unstubbed, notifier).renew("acc-1", 500);
        System.out.println("renew result  : " + status);                          // null !
        System.out.println("notified      : " + notifier.messages());             // [acc-1: Payment declined]
    }

    // ── 2. Stubbing ───────────────────────────────────────────────────────────

    /**
     * Stubbing tells a mock how to answer. Mockito equivalents:
     * <pre>
     *   when(gateway.charge("acc-1", 500)).thenReturn(ChargeStatus.APPROVED);
     *   when(gateway.charge("acc-1", 500)).thenReturn(DECLINED, APPROVED);   // consecutive calls
     *   when(gateway.charge(anyString(), anyLong())).thenThrow(new GatewayException("timeout"));
     *   doThrow(new GatewayException("down")).when(gateway).refund("acc-1", 100);  // void method
     * </pre>
     *
     * <p>WHY this matters: you control the world the SUT sees — including failures
     * (timeouts, declines) that are hard or slow to trigger for real.
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>Void methods cannot be stubbed with {@code when(...)} — use
     *       {@code doThrow/doNothing(...).when(mock).method()}.</li>
     *   <li>If one argument uses a matcher ({@code anyString()}), <b>all</b> arguments
     *       must — wrap raw values in {@code eq(...)}. Mixing throws
     *       {@code InvalidUseOfMatchersException}.</li>
     * </ul>
     */
    static void stubbingDemo() {
        System.out.println("=== Stubbing ===");

        RecordingNotifier approved = new RecordingNotifier();
        System.out.println("approved      : "
                + new SubscriptionService(new ScriptedGateway(ChargeStatus.APPROVED), approved)
                        .renew("acc-1", 500) + " " + approved.messages());

        RecordingNotifier declined = new RecordingNotifier();
        System.out.println("declined      : "
                + new SubscriptionService(new ScriptedGateway(ChargeStatus.DECLINED), declined)
                        .renew("acc-1", 500) + " " + declined.messages());

        RecordingNotifier failed = new RecordingNotifier();
        System.out.println("gateway down  : "
                + new SubscriptionService(
                        new ScriptedGateway(new GatewayException("timeout")), failed)
                        .renew("acc-1", 500) + " " + failed.messages());
    }

    // ── 3. Verification ───────────────────────────────────────────────────────

    /**
     * Verification asks a mock "were you called like this?". Mockito equivalents:
     * <pre>
     *   verify(notifier).send("acc-1", "Renewed");              // exactly once (default)
     *   verify(notifier, times(2)).send("acc-1", "Renewed");
     *   verify(notifier, never()).send(anyString(), anyString());
     *   verifyNoMoreInteractions(notifier);                     // nothing else happened
     *   InOrder order = inOrder(gateway, notifier);
     *   order.verify(gateway).refund("acc-1", 100);             // refund BEFORE notify
     *   order.verify(notifier).send("acc-1", "Cancelled");
     * </pre>
     *
     * <p>WHY this matters: some outcomes leave no state behind (a message sent, a charge
     * attempted). Verification is how you assert on them.
     *
     * <p>Common gotcha: over-verifying. Every {@code verify} welds the test to the
     * implementation. Verify the interactions that <em>are</em> the requirement
     * (the customer was notified), not incidental ones (which helper was called).
     */
    static void verificationDemo() {
        System.out.println("=== Verification ===");

        RecordingNotifier notifier = new RecordingNotifier();
        SubscriptionService approvedService =
                new SubscriptionService(new ScriptedGateway(ChargeStatus.APPROVED), notifier);
        approvedService.renew("acc-1", 500);
        approvedService.renew("acc-1", 500);
        System.out.println("messages      : " + notifier.messages());
        System.out.println("times(2)      : " + notifier.messages().size());   // 2

        RecordingGateway gateway = new RecordingGateway();
        RecordingNotifier cancelNotifier = new RecordingNotifier();
        new SubscriptionService(gateway, cancelNotifier).cancel("acc-1", 100);
        System.out.println("gateway calls : " + gateway.calls());              // [refund]
        System.out.println("then notified : " + cancelNotifier.messages());    // [acc-1: Cancelled]
    }

    // ── 4. Strict stubs and @InjectMocks ──────────────────────────────────────

    /**
     * With {@code @ExtendWith(MockitoExtension.class)} Mockito runs in <em>strict stubs</em>
     * mode:
     * <ul>
     *   <li>a stub the code never used fails the test with {@code UnnecessaryStubbingException}</li>
     *   <li>calling a stubbed method with different arguments fails with
     *       {@code PotentialStubbingProblem}</li>
     * </ul>
     * Use {@code lenient().when(...)} to opt a single stub out.
     *
     * <p>(Those two failures are not demonstrated in {@code MockitoBasicsDemoTest}: a test cannot
     * assert on a failure raised by the very session that is running it.)
     *
     * <p>{@code @Mock} fields create the mocks; {@code @InjectMocks} builds the SUT and
     * passes them to its constructor:
     * <pre>
     *   &#64;Mock PaymentGateway gateway;
     *   &#64;Mock Notifier notifier;
     *   &#64;InjectMocks SubscriptionService service;   // = new SubscriptionService(gateway, notifier)
     * </pre>
     *
     * <p>WHY this matters: dead stubs are test-code rot — they say "this test needs X"
     * when it does not. Strictness deletes them for you.
     *
     * <p>The demo below shows the idea by hand: {@code renew} only ever calls
     * {@code charge}, so a stub for {@code isAvailable} would be dead setup.
     */
    static void strictStubsDemo() {
        System.out.println("=== Strict stubs ===");

        RecordingGateway gateway = new RecordingGateway();
        new SubscriptionService(gateway, new RecordingNotifier()).renew("acc-1", 500);
        System.out.println("renew called  : " + gateway.calls());   // [charge] — never isAvailable
    }

    // ── 5. Spies ──────────────────────────────────────────────────────────────

    /**
     * A Mockito spy wraps a <em>real</em> object: every method runs the real code unless
     * you stub it. Mockito equivalents:
     * <pre>
     *   ReceiptPrinter printer = spy(new ReceiptPrinter());
     *   doReturn("$$$").when(printer).money(1999);   // stub just one method
     *   verify(printer).money(1999);                 // real calls are recorded too
     * </pre>
     *
     * <p>WHY this matters: occasionally you need the real class but must neutralise one
     * awkward method. Treat a spy as a last resort — needing one often means the class
     * does too much.
     *
     * <p>Common gotcha: always stub spies with {@code doReturn(...).when(spy).method()}.
     * The {@code when(spy.method())} form <em>executes the real method</em> while stubbing
     * — harmless here, an exception (or a side effect) on real objects.
     */
    static void spyDemo() {
        System.out.println("=== Spies ===");

        ReceiptPrinter printer = new ReceiptPrinter();
        System.out.println("line          : " + printer.line("Total", 1999));   // Total: $19.99
        System.out.println("money(5)      : " + printer.money(5));              // $0.05
        System.out.println("money(-50)    : " + printer.money(-50));            // -$0.50
    }
}
