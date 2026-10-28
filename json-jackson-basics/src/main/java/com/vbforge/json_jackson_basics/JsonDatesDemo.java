package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * JsonDatesDemo — dates and times in JSON.
 *
 * <p>JSON has no date type, so every date is either a number or a string — and Jackson
 * has to be told which. Every example here is timezone-independent.
 *
 * <p>Topics:
 * <ol>
 *   <li>Without the JSR-310 module — {@code java.time} types fail</li>
 *   <li>With the module — but dates become numeric arrays by default</li>
 *   <li>ISO-8601 strings — disable {@code WRITE_DATES_AS_TIMESTAMPS}</li>
 *   <li>{@code @JsonFormat} — custom patterns per property</li>
 *   <li>Legacy {@link Date}</li>
 * </ol>
 */
public class JsonDatesDemo {

    /** Mapper with java.time support, Jackson's defaults otherwise (dates as timestamps). */
    private static final ObjectMapper TIMESTAMPS = new ObjectMapper().registerModule(new JavaTimeModule());

    /** Mapper with java.time support and ISO-8601 strings. */
    private static final ObjectMapper ISO = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // ── Model types ───────────────────────────────────────────────────────────

    /** Event with three java.time fields and no formatting annotations. */
    public static class Event {
        private String title;
        private LocalDate date;
        private LocalDateTime startsAt;
        private Instant createdAt;

        public Event() { }

        public Event(String title, LocalDate date, LocalDateTime startsAt, Instant createdAt) {
            this.title = title;
            this.date = date;
            this.startsAt = startsAt;
            this.createdAt = createdAt;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public LocalDateTime getStartsAt() { return startsAt; }
        public void setStartsAt(LocalDateTime startsAt) { this.startsAt = startsAt; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    }

    /** Booking with custom patterns per property. */
    public static class Booking {
        @JsonFormat(pattern = "dd/MM/yyyy")
        private LocalDate checkIn;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime checkOutAt;
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
        private Date legacyDate;

        public Booking() { }

        public Booking(LocalDate checkIn, LocalDateTime checkOutAt, Date legacyDate) {
            this.checkIn = checkIn;
            this.checkOutAt = checkOutAt;
            this.legacyDate = legacyDate;
        }

        public LocalDate getCheckIn() { return checkIn; }
        public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
        public LocalDateTime getCheckOutAt() { return checkOutAt; }
        public void setCheckOutAt(LocalDateTime checkOutAt) { this.checkOutAt = checkOutAt; }
        public Date getLegacyDate() { return legacyDate; }
        public void setLegacyDate(Date legacyDate) { this.legacyDate = legacyDate; }
    }

    static Event sampleEvent() {
        return new Event("Release",
                LocalDate.of(2024, 1, 15),
                LocalDateTime.of(2024, 1, 15, 10, 30),
                Instant.parse("2024-01-15T08:00:00Z"));
    }

    public static void main(String[] args) throws Exception {
        missingModuleDemo();
        System.out.println("---");
        timestampsDemo();
        System.out.println("---");
        isoStringsDemo();
        System.out.println("---");
        formatDemo();
        System.out.println("---");
        legacyDateDemo();
    }

    // ── 1. Without the module ─────────────────────────────────────────────────

    /**
     * A plain {@link ObjectMapper} does not know {@code java.time}. Since Jackson 2.12 it
     * refuses to guess and throws {@link InvalidDefinitionException}.
     *
     * <p>WHY it matters: older versions silently serialised {@code LocalDate} as a bean
     * (year, month, chronology…) — useless output. Failing loudly is the safer default.
     *
     * <p>Fix: add {@code jackson-datatype-jsr310} and register {@link JavaTimeModule}.
     *
     * <p>Gotcha: this fails in both directions — writing <em>and</em> reading.
     */
    static void missingModuleDemo() throws Exception {
        System.out.println("=== Without JavaTimeModule ===");

        ObjectMapper plain = new ObjectMapper();
        try {
            plain.writeValueAsString(LocalDate.of(2024, 1, 15));
            System.out.println("write : ok (unexpected)");
        } catch (InvalidDefinitionException e) {
            System.out.println("write : " + e.getClass().getSimpleName());
        }
        try {
            plain.readValue("\"2024-01-15\"", LocalDate.class);
            System.out.println("read  : ok (unexpected)");
        } catch (InvalidDefinitionException e) {
            System.out.println("read  : " + e.getClass().getSimpleName());
        }
    }

    // ── 2. Module registered, default settings ────────────────────────────────

    /**
     * With the module registered but nothing else changed, Jackson still writes dates as
     * <em>numeric timestamps</em>: {@code LocalDate} becomes {@code [2024,1,15]},
     * {@code LocalDateTime} an array of its parts, {@code Instant} decimal seconds.
     *
     * <p>WHY it matters: this default surprises everyone once. Most APIs expect
     * ISO-8601 strings, so you almost always switch it off (next section).
     *
     * <p>Gotcha: the arrays are valid JSON, so nothing fails — a client just receives a
     * shape it did not expect.
     */
    static void timestampsDemo() throws Exception {
        System.out.println("=== Default: timestamps ===");

        System.out.println("LocalDate     : " + TIMESTAMPS.writeValueAsString(LocalDate.of(2024, 1, 15)));            // [2024,1,15]
        System.out.println("LocalDateTime : " + TIMESTAMPS.writeValueAsString(LocalDateTime.of(2024, 1, 15, 10, 30))); // [2024,1,15,10,30]
        System.out.println("Event         : " + TIMESTAMPS.writeValueAsString(sampleEvent()));
    }

    // ── 3. ISO-8601 strings ───────────────────────────────────────────────────

    /**
     * {@code disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)} switches every date to
     * an ISO-8601 string — {@code 2024-01-15}, {@code 2024-01-15T10:30:00},
     * {@code 2024-01-15T08:00:00Z}. Reading accepts the same strings.
     *
     * <p>WHY it matters: ISO-8601 is human-readable, sortable as text and understood by
     * every language and database.
     *
     * <p>Complexity: O(1) per value.
     *
     * <p>Gotcha: prefer {@link Instant} (always UTC) over {@code LocalDateTime} for moments
     * that happened at one real point in time — {@code LocalDateTime} carries no zone.
     */
    static void isoStringsDemo() throws Exception {
        System.out.println("=== ISO-8601 strings ===");

        String json = ISO.writeValueAsString(sampleEvent());
        System.out.println("Event json    : " + json);
        // {"title":"Release","date":"2024-01-15","startsAt":"2024-01-15T10:30:00","createdAt":"2024-01-15T08:00:00Z"}

        Event back = ISO.readValue(json, Event.class);
        System.out.println("date          : " + back.getDate());         // 2024-01-15
        System.out.println("startsAt      : " + back.getStartsAt());     // 2024-01-15T10:30
        System.out.println("createdAt     : " + back.getCreatedAt());    // 2024-01-15T08:00:00Z

        JsonNode tree = ISO.readTree(json);
        System.out.println("date is text  : " + tree.get("date").isTextual());   // true
    }

    // ── 4. @JsonFormat ────────────────────────────────────────────────────────

    /**
     * {@code @JsonFormat(pattern = "...")} sets a custom pattern for one property, in both
     * directions. A pattern forces string output even when timestamps are enabled.
     *
     * <p>WHY it matters: legacy systems and UIs often use formats like {@code dd/MM/yyyy}.
     * A per-property pattern avoids a global setting that would affect every date.
     *
     * <p>Complexity: O(1) per value; the formatter is cached per property.
     *
     * <p>Gotcha: patterns are case-sensitive — {@code MM} is month, {@code mm} is minute;
     * {@code yyyy} is year, {@code YYYY} is week-based year.
     */
    static void formatDemo() throws Exception {
        System.out.println("=== @JsonFormat ===");

        Booking booking = new Booking(LocalDate.of(2024, 1, 15), LocalDateTime.of(2024, 1, 17, 11, 0), new Date(0L));
        String json = TIMESTAMPS.writeValueAsString(booking);   // note: the *timestamp* mapper
        System.out.println("booking json  : " + json);
        // {"checkIn":"15/01/2024","checkOutAt":"2024-01-17 11:00","legacyDate":"1970-01-01"}

        Booking back = TIMESTAMPS.readValue(json, Booking.class);
        System.out.println("checkIn       : " + back.getCheckIn());      // 2024-01-15
    }

    // ── 5. Legacy java.util.Date ──────────────────────────────────────────────

    /**
     * {@link Date} needs no module. By default it is written as epoch milliseconds;
     * with timestamps disabled it becomes an ISO-8601 string in UTC.
     *
     * <p>WHY it matters: older code and libraries still hand you {@code Date}. Know what
     * Jackson does with it so you can convert at the boundary.
     *
     * <p>Gotcha: epoch millis are a plain number — the receiver cannot tell it is a date.
     */
    static void legacyDateDemo() throws Exception {
        System.out.println("=== java.util.Date ===");

        Date epoch = new Date(0L);
        System.out.println("default       : " + TIMESTAMPS.writeValueAsString(epoch));   // 0
        System.out.println("ISO           : " + ISO.writeValueAsString(epoch));          // "1970-01-01T00:00:00.000+00:00"

        Date back = ISO.readValue("\"1970-01-01T00:00:00.000+00:00\"", Date.class);
        System.out.println("round trip ms : " + back.getTime());                         // 0
    }
}
