package com.vbforge.best_practices_and_solid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DependencyInversionTasks — your implementations")
class DependencyInversionTasksTest {

    private DependencyInversionTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new DependencyInversionTasks();
    }

    private static Clock utcClockAt(String isoInstant) {
        return Clock.fixed(Instant.parse(isoInstant), ZoneOffset.UTC);
    }

    @Nested
    @Disabled("Remove @Disabled when you implement notifyAll()")
    @DisplayName("notifyAll(recipients, message, sender)")
    class NotifyAllTest {

        @Test
        @DisplayName("returns one receipt per recipient, in order")
        void receiptsInOrder() {
            assertThat(tasks.notifyAll(Arrays.asList("a", "b"), "hi", (to, msg) -> to + "|" + msg))
                    .containsExactly("a|hi", "b|hi");
        }

        @Test
        @DisplayName("an empty recipient list sends nothing")
        void emptyRecipients() {
            List<String> calls = new ArrayList<>();

            List<String> receipts = tasks.notifyAll(Collections.<String>emptyList(), "hi", (to, msg) -> {
                calls.add(to);
                return "x";
            });

            assertThat(receipts).isEmpty();
            assertThat(calls).isEmpty();
        }

        @Test
        @DisplayName("the sender is called exactly once per recipient with the same message")
        void senderCalledOncePerRecipient() {
            List<String> calls = new ArrayList<>();

            tasks.notifyAll(Arrays.asList("a", "b", "c"), "hello", (to, msg) -> {
                calls.add(to + ":" + msg);
                return "ok";
            });

            assertThat(calls).containsExactly("a:hello", "b:hello", "c:hello");
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement isExpired()")
    @DisplayName("isExpired(expiresAt, clock)")
    class IsExpiredTest {

        private final Instant expiresAt = Instant.parse("2024-01-01T12:00:00Z");

        @ParameterizedTest(name = "now = {0} -> expired = {1}")
        @CsvSource({
                "2024-01-01T11:59:59Z, false",
                "2024-01-01T12:00:00Z, true",
                "2024-01-01T12:00:01Z, true"
        })
        @DisplayName("expired exactly when now is not before the expiry moment")
        void boundary(String now, boolean expected) {
            assertThat(tasks.isExpired(expiresAt, utcClockAt(now))).isEqualTo(expected);
        }

        @Test
        @DisplayName("long before expiry it is not expired")
        void longBefore() {
            assertThat(tasks.isExpired(expiresAt, utcClockAt("2000-01-01T00:00:00Z"))).isFalse();
        }

        @Test
        @DisplayName("long after expiry it is expired")
        void longAfter() {
            assertThat(tasks.isExpired(expiresAt, utcClockAt("2099-01-01T00:00:00Z"))).isTrue();
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement greeting()")
    @DisplayName("greeting(clock)")
    class GreetingTest {

        @ParameterizedTest(name = "{0} -> {1}")
        @CsvSource({
                "05:00, Good morning",
                "09:00, Good morning",
                "11:59, Good morning",
                "12:00, Good afternoon",
                "17:59, Good afternoon",
                "18:00, Good evening",
                "23:30, Good evening",
                "00:00, Good evening",
                "04:59, Good evening"
        })
        @DisplayName("picks the greeting from the hour of day")
        void byHour(String time, String expected) {
            assertThat(tasks.greeting(utcClockAt("2024-01-01T" + time + ":00Z"))).isEqualTo(expected);
        }

        @Test
        @DisplayName("uses the clock's time zone, not the machine's")
        void usesClockZone() {
            Instant instant = Instant.parse("2024-01-01T10:00:00Z");

            assertThat(tasks.greeting(Clock.fixed(instant, ZoneOffset.UTC))).isEqualTo("Good morning");
            assertThat(tasks.greeting(Clock.fixed(instant, ZoneOffset.ofHours(2)))).isEqualTo("Good afternoon");
        }
    }
}
