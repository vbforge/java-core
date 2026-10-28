package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vbforge.json_jackson_basics.JsonDatesDemo.Booking;
import com.vbforge.json_jackson_basics.JsonDatesDemo.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JsonDatesDemoTest — verifies every concept shown in {@link JsonDatesDemo}.
 *
 * <p>These tests are always GREEN and independent of the machine's timezone or locale.
 */
@DisplayName("JsonDatesDemo — API contracts")
class JsonDatesDemoTest {

    private static final ObjectMapper TIMESTAMPS = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final ObjectMapper ISO = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static JsonNode tree(String json) throws Exception {
        return TIMESTAMPS.readTree(json);
    }

    private static Event sampleEvent() {
        return new Event("Release",
                LocalDate.of(2024, 1, 15),
                LocalDateTime.of(2024, 1, 15, 10, 30),
                Instant.parse("2024-01-15T08:00:00Z"));
    }

    // ── 1. Without the module ─────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Without JavaTimeModule")
    class MissingModuleTest {

        @Test
        @DisplayName("writing a LocalDate fails with InvalidDefinitionException")
        void writeFails() {
            assertThatThrownBy(() -> new ObjectMapper().writeValueAsString(LocalDate.of(2024, 1, 15)))
                    .isInstanceOf(InvalidDefinitionException.class)
                    .hasMessageContaining("java.time.LocalDate");
        }

        @Test
        @DisplayName("reading a LocalDate fails with InvalidDefinitionException")
        void readFails() {
            assertThatThrownBy(() -> new ObjectMapper().readValue("\"2024-01-15\"", LocalDate.class))
                    .isInstanceOf(InvalidDefinitionException.class);
        }
    }

    // ── 2. Default timestamps ─────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Default: timestamps")
    class TimestampsTest {

        @Test
        @DisplayName("LocalDate becomes [year, month, day]")
        void localDateArray() throws Exception {
            assertThat(TIMESTAMPS.writeValueAsString(LocalDate.of(2024, 1, 15))).isEqualTo("[2024,1,15]");
        }

        @Test
        @DisplayName("LocalDateTime becomes an array of its parts")
        void localDateTimeArray() throws Exception {
            assertThat(TIMESTAMPS.writeValueAsString(LocalDateTime.of(2024, 1, 15, 10, 30)))
                    .isEqualTo("[2024,1,15,10,30]");
        }

        @Test
        @DisplayName("Instant becomes a number")
        void instantNumber() throws Exception {
            JsonNode json = tree(TIMESTAMPS.writeValueAsString(sampleEvent()));
            assertThat(json.get("createdAt").isNumber()).isTrue();
            assertThat(json.get("date").isArray()).isTrue();
        }
    }

    // ── 3. ISO strings ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. ISO-8601 strings")
    class IsoStringsTest {

        @Test
        @DisplayName("disabled timestamps write ISO strings")
        void isoOutput() throws Exception {
            assertThat(tree(ISO.writeValueAsString(sampleEvent()))).isEqualTo(tree(
                    "{\"title\":\"Release\",\"date\":\"2024-01-15\","
                            + "\"startsAt\":\"2024-01-15T10:30:00\",\"createdAt\":\"2024-01-15T08:00:00Z\"}"));
        }

        @Test
        @DisplayName("an Event round-trips with equal field values")
        void roundTrip() throws Exception {
            Event back = ISO.readValue(ISO.writeValueAsString(sampleEvent()), Event.class);
            assertThat(back.getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
            assertThat(back.getStartsAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
            assertThat(back.getCreatedAt()).isEqualTo(Instant.parse("2024-01-15T08:00:00Z"));
        }

        @Test
        @DisplayName("a lone LocalDate is a quoted ISO string")
        void loneDate() throws Exception {
            assertThat(ISO.writeValueAsString(LocalDate.of(2024, 1, 15))).isEqualTo("\"2024-01-15\"");
        }
    }

    // ── 4. @JsonFormat ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. @JsonFormat")
    class FormatTest {

        private Booking booking() {
            return new Booking(LocalDate.of(2024, 1, 15), LocalDateTime.of(2024, 1, 17, 11, 0), new Date(0L));
        }

        @Test
        @DisplayName("patterns apply even when the mapper writes timestamps")
        void patternOnWrite() throws Exception {
            assertThat(tree(TIMESTAMPS.writeValueAsString(booking()))).isEqualTo(tree(
                    "{\"checkIn\":\"15/01/2024\",\"checkOutAt\":\"2024-01-17 11:00\",\"legacyDate\":\"1970-01-01\"}"));
        }

        @Test
        @DisplayName("the same patterns are used when reading")
        void patternOnRead() throws Exception {
            Booking back = TIMESTAMPS.readValue(
                    "{\"checkIn\":\"15/01/2024\",\"checkOutAt\":\"2024-01-17 11:00\",\"legacyDate\":\"1970-01-01\"}",
                    Booking.class);
            assertThat(back.getCheckIn()).isEqualTo(LocalDate.of(2024, 1, 15));
            assertThat(back.getCheckOutAt()).isEqualTo(LocalDateTime.of(2024, 1, 17, 11, 0));
            assertThat(back.getLegacyDate().getTime()).isZero();
        }

        @Test
        @DisplayName("input in a different format than the pattern is rejected")
        void wrongFormat() {
            assertThatThrownBy(() -> TIMESTAMPS.readValue("{\"checkIn\":\"2024-01-15\"}", Booking.class))
                    .isInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
        }
    }

    // ── 5. Legacy Date ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. java.util.Date")
    class LegacyDateTest {

        @Test
        @DisplayName("Date is written as epoch milliseconds by default")
        void defaultMillis() throws Exception {
            assertThat(TIMESTAMPS.writeValueAsString(new Date(0L))).isEqualTo("0");
        }

        @Test
        @DisplayName("with timestamps disabled Date becomes an ISO string in UTC")
        void isoString() throws Exception {
            assertThat(ISO.writeValueAsString(new Date(0L))).startsWith("\"1970-01-01T00:00:00.000");
        }

        @Test
        @DisplayName("the ISO string reads back to the same instant")
        void roundTrip() throws Exception {
            Date back = ISO.readValue(ISO.writeValueAsString(new Date(86_400_000L)), Date.class);
            assertThat(back.getTime()).isEqualTo(86_400_000L);
        }
    }

    // ── Demo runs end-to-end ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Demo main()")
    class MainRunsTest {

        @Test
        @DisplayName("runs every section without throwing")
        void runs() {
            assertThatCode(() -> JsonDatesDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
