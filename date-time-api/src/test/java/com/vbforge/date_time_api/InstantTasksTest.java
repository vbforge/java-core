package com.vbforge.date_time_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InstantTasksTest — tests for every method in {@link InstantTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code InstantTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=InstantTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("InstantTasks — your implementations")
class InstantTasksTest {

    private InstantTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new InstantTasks();
    }

    // ── Task 1: epochMillisToIso ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement epochMillisToIso()")
    @DisplayName("Task 1 — epochMillisToIso()")
    class EpochMillisToIsoTest {

        @Test
        @DisplayName("0 → 1970-01-01T00:00:00Z")
        void epoch() {
            assertThat(tasks.epochMillisToIso(0)).isEqualTo("1970-01-01T00:00:00Z");
        }

        @Test
        @DisplayName("1_700_000_000_000 → 2023-11-14T22:13:20Z")
        void wholeSeconds() {
            assertThat(tasks.epochMillisToIso(1_700_000_000_000L)).isEqualTo("2023-11-14T22:13:20Z");
        }

        @Test
        @DisplayName("milliseconds are kept: …123 → .123Z")
        void withMillis() {
            assertThat(tasks.epochMillisToIso(1_700_000_000_123L)).isEqualTo("2023-11-14T22:13:20.123Z");
        }

        @Test
        @DisplayName("negative values are before 1970")
        void negative() {
            assertThat(tasks.epochMillisToIso(-1000)).isEqualTo("1969-12-31T23:59:59Z");
        }

        @Test
        @DisplayName("the result always ends with Z")
        void endsWithZ() {
            assertThat(tasks.epochMillisToIso(86_400_000L)).endsWith("Z");
        }
    }

    // ── Task 2: isExpired ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isExpired()")
    @DisplayName("Task 2 — isExpired()")
    class IsExpiredTest {

        private final Instant issuedAt = Instant.parse("2024-03-15T10:00:00Z");
        private final Duration ttl = Duration.ofHours(1);

        @Test
        @DisplayName("one second before expiry → false")
        void beforeExpiry() {
            assertThat(tasks.isExpired(issuedAt, ttl, Instant.parse("2024-03-15T10:59:59Z"))).isFalse();
        }

        @Test
        @DisplayName("exactly at expiry → still valid (false)")
        void exactlyAtExpiry() {
            assertThat(tasks.isExpired(issuedAt, ttl, Instant.parse("2024-03-15T11:00:00Z"))).isFalse();
        }

        @Test
        @DisplayName("one second after expiry → true")
        void afterExpiry() {
            assertThat(tasks.isExpired(issuedAt, ttl, Instant.parse("2024-03-15T11:00:01Z"))).isTrue();
        }

        @Test
        @DisplayName("zero TTL at the issue moment → false")
        void zeroTtl() {
            assertThat(tasks.isExpired(issuedAt, Duration.ZERO, issuedAt)).isFalse();
        }

        @Test
        @DisplayName("one nanosecond after expiry → true")
        void nanosecondPrecision() {
            Instant justAfter = Instant.parse("2024-03-15T11:00:00Z").plusNanos(1);
            assertThat(tasks.isExpired(issuedAt, ttl, justAfter)).isTrue();
        }

        @ParameterizedTest(name = "ttl={0}s now=+{1}s → expired={2}")
        @CsvSource({"60, 59, false", "60, 60, false", "60, 61, true", "0, 1, true", "86400, 86400, false"})
        @DisplayName("parametrized boundaries")
        void parametrized(long ttlSeconds, long elapsedSeconds, boolean expected) {
            Instant now = issuedAt.plusSeconds(elapsedSeconds);
            assertThat(tasks.isExpired(issuedAt, Duration.ofSeconds(ttlSeconds), now)).isEqualTo(expected);
        }
    }

    // ── Task 3: latest ────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement latest()")
    @DisplayName("Task 3 — latest()")
    class LatestTest {

        private final Instant ten = Instant.parse("2024-03-15T10:00:00Z");
        private final Instant eleven = Instant.parse("2024-03-15T11:00:00Z");
        private final Instant twelve = Instant.parse("2024-03-15T12:00:00Z");

        @Test
        @DisplayName("[10:00, 12:00, 11:00] → 12:00")
        void unordered() {
            assertThat(tasks.latest(Arrays.asList(ten, twelve, eleven))).contains(twelve);
        }

        @Test
        @DisplayName("single element → that element")
        void single() {
            assertThat(tasks.latest(Collections.singletonList(ten))).contains(ten);
        }

        @Test
        @DisplayName("duplicates → that moment")
        void duplicates() {
            assertThat(tasks.latest(Arrays.asList(ten, ten))).contains(ten);
        }

        @Test
        @DisplayName("empty list → Optional.empty()")
        void empty() {
            Optional<Instant> result = tasks.latest(new ArrayList<Instant>());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("the input list is not modified")
        void inputUntouched() {
            List<Instant> input = new ArrayList<>(Arrays.asList(twelve, ten, eleven));
            tasks.latest(input);
            assertThat(input).containsExactly(twelve, ten, eleven);
        }

        @Test
        @DisplayName("works on an immutable list")
        void immutableInput() {
            List<Instant> input = Collections.unmodifiableList(Arrays.asList(ten, eleven));
            assertThat(tasks.latest(input)).contains(eleven);
        }

        @Test
        @DisplayName("moments before 1970 are handled")
        void beforeEpoch() {
            Instant before = Instant.parse("1969-12-31T23:59:59Z");
            assertThat(tasks.latest(Arrays.asList(before, Instant.parse("1960-01-01T00:00:00Z")))).contains(before);
        }
    }

    // ── Task 4: addDays ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement addDays()")
    @DisplayName("Task 4 — addDays()")
    class AddDaysTest {

        @Test
        @DisplayName("+1 day")
        void plusOne() {
            assertThat(tasks.addDays(Instant.parse("2024-03-15T10:15:30Z"), 1))
                    .isEqualTo(Instant.parse("2024-03-16T10:15:30Z"));
        }

        @Test
        @DisplayName("2024-02-28 + 2 days = 2024-03-01 (leap year)")
        void leapYear() {
            assertThat(tasks.addDays(Instant.parse("2024-02-28T00:00:00Z"), 2))
                    .isEqualTo(Instant.parse("2024-03-01T00:00:00Z"));
        }

        @Test
        @DisplayName("-15 days goes back")
        void negative() {
            assertThat(tasks.addDays(Instant.parse("2024-03-15T10:15:30Z"), -15))
                    .isEqualTo(Instant.parse("2024-02-29T10:15:30Z"));
        }

        @Test
        @DisplayName("0 days returns an equal instant")
        void zero() {
            Instant instant = Instant.parse("2024-03-15T10:15:30Z");
            assertThat(tasks.addDays(instant, 0)).isEqualTo(instant);
        }

        @Test
        @DisplayName("a day is exactly 86 400 seconds")
        void exactlyOneDay() {
            Instant start = Instant.parse("2024-03-10T07:00:00Z");
            assertThat(Duration.between(start, tasks.addDays(start, 1))).isEqualTo(Duration.ofSeconds(86_400));
        }

        @Test
        @DisplayName("nanosecond part is preserved")
        void nanosPreserved() {
            Instant instant = Instant.parse("2024-03-15T10:15:30.123456789Z");
            assertThat(tasks.addDays(instant, 3).getNano()).isEqualTo(123_456_789);
        }
    }

    // ── Task 5: truncateToMinute ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement truncateToMinute()")
    @DisplayName("Task 5 — truncateToMinute()")
    class TruncateToMinuteTest {

        @Test
        @DisplayName("10:15:30.987 → 10:15:00")
        void basic() {
            assertThat(tasks.truncateToMinute(Instant.parse("2024-03-15T10:15:30.987Z")))
                    .isEqualTo(Instant.parse("2024-03-15T10:15:00Z"));
        }

        @Test
        @DisplayName("10:15:59 → 10:15:00")
        void lastSecond() {
            assertThat(tasks.truncateToMinute(Instant.parse("2024-03-15T10:15:59Z")))
                    .isEqualTo(Instant.parse("2024-03-15T10:15:00Z"));
        }

        @Test
        @DisplayName("already aligned stays unchanged")
        void aligned() {
            Instant aligned = Instant.parse("2024-03-15T10:15:00Z");
            assertThat(tasks.truncateToMinute(aligned)).isEqualTo(aligned);
        }

        @Test
        @DisplayName("before the epoch: 23:59:59 → 23:59:00")
        void beforeEpoch() {
            assertThat(tasks.truncateToMinute(Instant.parse("1969-12-31T23:59:59Z")))
                    .isEqualTo(Instant.parse("1969-12-31T23:59:00Z"));
        }

        @Test
        @DisplayName("the result is never after the input")
        void neverAfterInput() {
            Instant input = Instant.parse("2024-03-15T10:15:42.5Z");
            assertThat(tasks.truncateToMinute(input).isAfter(input)).isFalse();
        }
    }
}
