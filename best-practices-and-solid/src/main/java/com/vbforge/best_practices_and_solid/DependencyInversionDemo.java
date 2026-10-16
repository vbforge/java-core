package com.vbforge.best_practices_and_solid;

import java.time.Clock;
import java.time.Instant;

/**
 * <b>D — Dependency Inversion Principle (DIP).</b>
 *
 * <p>"High-level policy should not depend on low-level detail; both should depend on
 * abstractions." In practice: a class receives its collaborators (constructor
 * injection) instead of creating them with {@code new}.
 *
 * <p>Sections:
 * <ol>
 *   <li>A class that builds its own low-level dependency</li>
 *   <li>Depending on an abstraction (constructor injection)</li>
 *   <li>Time as a dependency: {@link Clock}</li>
 *   <li>The composition root — the one place that calls {@code new}</li>
 * </ol>
 *
 * <p>Note: DIP (a design rule) is not the same as Dependency <i>Injection</i> (a
 * technique) or a framework such as Spring (a tool). Injection is the simplest way
 * to follow DIP; you need no framework to do it.
 */
public class DependencyInversionDemo {

    // ── 1. Hard-wired dependency ──────────────────────────────────────────────
    /**
     * What it is: {@link OrderNotifierBad} creates {@link SmtpClient} itself.
     *
     * <p>Why it hurts: you cannot test it without sending real mail, cannot switch to
     * SMS without editing it, and the business rule ("confirm an order") is welded to a
     * delivery detail.
     */
    public static class SmtpClient {
        public String sendMail(String to, String body) {
            return "SMTP to " + to + ": " + body;
        }
    }

    public static class OrderNotifierBad {
        private final SmtpClient client = new SmtpClient();     // hard-wired

        public String confirm(String email, String orderId) {
            return client.sendMail(email, "Order " + orderId + " confirmed");
        }
    }


    // ── 2. Depend on an abstraction ───────────────────────────────────────────
    /**
     * What it is: {@link OrderNotifier} knows only {@link MessageSender}. Real senders,
     * test fakes and lambdas are interchangeable.
     *
     * <p>Why it matters: the arrow of source-code dependency now points <i>from</i> the
     * detail <i>to</i> the abstraction — "inverted" compared with section 1.
     *
     * <p>Gotcha: an interface with exactly one implementation and no test double is
     * usually noise. Introduce the abstraction where a second implementation (or a fake) exists.
     */
    @FunctionalInterface
    public interface MessageSender {
        /** @return a receipt describing what was sent */
        String send(String recipient, String message);
    }

    public static class SmtpSender implements MessageSender {
        @Override
        public String send(String recipient, String message) {
            return "SMTP to " + recipient + ": " + message;
        }
    }

    public static class SmsSender implements MessageSender {
        @Override
        public String send(String recipient, String message) {
            return "SMS to " + recipient + ": " + message;
        }
    }

    public static class OrderNotifier {
        private final MessageSender sender;

        public OrderNotifier(MessageSender sender) {
            this.sender = sender;
        }

        public String confirm(String recipient, String orderId) {
            return sender.send(recipient, "Order " + orderId + " confirmed");
        }
    }


    // ── 3. Time is a dependency too ───────────────────────────────────────────
    /**
     * What it is: {@code Instant.now()} reads a hidden global (the system clock).
     * Passing a {@link Clock} makes time an explicit input.
     *
     * <p>Why it matters: tests can use {@code Clock.fixed(...)} and check the exact
     * boundary ("expires at 12:00:00") — impossible with the real clock without sleeping.
     * Tests also stop depending on the machine's date and time zone.
     */
    public static class SessionTokenBad {
        private final Instant expiresAt;

        public SessionTokenBad(Instant expiresAt) {
            this.expiresAt = expiresAt;
        }

        public boolean isExpired() {
            return !Instant.now().isBefore(expiresAt);          // hidden dependency on the real clock
        }
    }

    public static class SessionToken {
        private final Instant expiresAt;

        public SessionToken(Instant expiresAt) {
            this.expiresAt = expiresAt;
        }

        public boolean isExpired(Clock clock) {
            return !clock.instant().isBefore(expiresAt);
        }
    }


    // ── 4. Composition root ───────────────────────────────────────────────────
    /**
     * What it is: the single place where concrete classes are chosen and wired.
     * Everything below it only sees interfaces.
     *
     * <p>Why it matters: switching from e-mail to SMS is a one-line change here and
     * touches no business class. Frameworks like Spring automate exactly this wiring.
     */
    public static OrderNotifier buildNotifier(boolean useSms) {
        return new OrderNotifier(useSms ? new SmsSender() : new SmtpSender());
    }


    // ── Demo runners ──────────────────────────────────────────────────────────
    static void hardWiredDemo() {
        System.out.println(new OrderNotifierBad().confirm("ann@example.com", "A-1"));
    }

    static void abstractionDemo() {
        OrderNotifier withFake = new OrderNotifier((recipient, message) -> "FAKE -> " + recipient);
        System.out.println(withFake.confirm("ann@example.com", "A-2"));
        System.out.println(new OrderNotifier(new SmsSender()).confirm("+380000000", "A-3"));
    }

    static void clockDemo() {
        Instant noon = Instant.parse("2024-01-01T12:00:00Z");
        SessionToken token = new SessionToken(noon);
        System.out.println("one second before expiry -> " + token.isExpired(Clock.fixed(noon.minusSeconds(1), java.time.ZoneOffset.UTC)));
        System.out.println("exactly at expiry        -> " + token.isExpired(Clock.fixed(noon, java.time.ZoneOffset.UTC)));
    }

    static void compositionRootDemo() {
        System.out.println(buildNotifier(false).confirm("ann@example.com", "A-4"));
        System.out.println(buildNotifier(true).confirm("+380000000", "A-5"));
    }

    public static void main(String[] args) {
        hardWiredDemo();
        System.out.println("---");
        abstractionDemo();
        System.out.println("---");
        clockDemo();
        System.out.println("---");
        compositionRootDemo();
    }
}
