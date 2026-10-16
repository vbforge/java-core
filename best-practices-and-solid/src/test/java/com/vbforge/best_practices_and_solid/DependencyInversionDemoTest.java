package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.DependencyInversionDemo.OrderNotifier;
import com.vbforge.best_practices_and_solid.DependencyInversionDemo.OrderNotifierBad;
import com.vbforge.best_practices_and_solid.DependencyInversionDemo.SessionToken;
import com.vbforge.best_practices_and_solid.DependencyInversionDemo.SessionTokenBad;
import com.vbforge.best_practices_and_solid.DependencyInversionDemo.SmsSender;
import com.vbforge.best_practices_and_solid.DependencyInversionDemo.SmtpSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DependencyInversionDemo — API contracts")
class DependencyInversionDemoTest {

    private static final Instant NOON = Instant.parse("2024-01-01T12:00:00Z");

    private static Clock clockAt(Instant instant) {
        return Clock.fixed(instant, ZoneOffset.UTC);
    }

    @Nested
    @DisplayName("1. Hard-wired dependency")
    class HardWiredSection {

        @Test
        @DisplayName("the bad notifier always talks to SMTP — there is no way to change that")
        void alwaysUsesSmtp() {
            assertThat(new OrderNotifierBad().confirm("ann@example.com", "A-1"))
                    .isEqualTo("SMTP to ann@example.com: Order A-1 confirmed");
        }
    }


    @Nested
    @DisplayName("2. Depend on an abstraction")
    class AbstractionSection {

        @Test
        @DisplayName("a lambda can stand in for the real sender in a test")
        void fakeSender() {
            List<String> calls = new ArrayList<>();
            OrderNotifier notifier = new OrderNotifier((recipient, message) -> {
                calls.add(recipient + "|" + message);
                return "ok";
            });

            String receipt = notifier.confirm("ann@example.com", "A-2");

            assertThat(receipt).isEqualTo("ok");
            assertThat(calls).containsExactly("ann@example.com|Order A-2 confirmed");
        }

        @Test
        @DisplayName("the same notifier works over SMTP")
        void smtpSender() {
            assertThat(new OrderNotifier(new SmtpSender()).confirm("ann@example.com", "A-3"))
                    .isEqualTo(new OrderNotifierBad().confirm("ann@example.com", "A-3"));
        }

        @Test
        @DisplayName("the same notifier works over SMS without being edited")
        void smsSender() {
            assertThat(new OrderNotifier(new SmsSender()).confirm("+380000000", "A-4"))
                    .isEqualTo("SMS to +380000000: Order A-4 confirmed");
        }
    }


    @Nested
    @DisplayName("3. Time is a dependency too")
    class ClockSection {

        @Test
        @DisplayName("one second before the expiry moment the token is still valid")
        void validBeforeExpiry() {
            assertThat(new SessionToken(NOON).isExpired(clockAt(NOON.minusSeconds(1)))).isFalse();
        }

        @Test
        @DisplayName("exactly at the expiry moment the token is expired")
        void expiredAtBoundary() {
            assertThat(new SessionToken(NOON).isExpired(clockAt(NOON))).isTrue();
        }

        @Test
        @DisplayName("after the expiry moment the token is expired")
        void expiredAfter() {
            assertThat(new SessionToken(NOON).isExpired(clockAt(NOON.plusSeconds(1)))).isTrue();
        }

        @Test
        @DisplayName("the real-clock version can only be tested with far-past or far-future dates")
        void realClockVersionIsCoarse() {
            assertThat(new SessionTokenBad(Instant.EPOCH).isExpired()).isTrue();
            assertThat(new SessionTokenBad(Instant.MAX).isExpired()).isFalse();
        }
    }


    @Nested
    @DisplayName("4. Composition root")
    class CompositionRootSection {

        @Test
        @DisplayName("useSms=false wires an SMTP sender")
        void wiresSmtp() {
            assertThat(DependencyInversionDemo.buildNotifier(false).confirm("ann@example.com", "A-5"))
                    .startsWith("SMTP to ");
        }

        @Test
        @DisplayName("useSms=true wires an SMS sender")
        void wiresSms() {
            assertThat(DependencyInversionDemo.buildNotifier(true).confirm("+380000000", "A-6"))
                    .startsWith("SMS to ");
        }
    }
}
