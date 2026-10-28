package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;

/**
 * JsonDatesTasks — implement every method below to make {@code JsonDatesTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code JsonDatesDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=JsonDatesTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Tasks 4 and 5 also require you to <em>add an annotation</em> to the {@link Invoice}
 * model at the bottom of this file. Do not change method signatures.
 */
public class JsonDatesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Creates an {@link ObjectMapper} that handles {@code java.time} types and writes
     * dates as ISO-8601 <em>strings</em> (never as numeric arrays or timestamps).
     *
     * <p>Examples (what the returned mapper must do):
     * <pre>
     *   write LocalDate.of(2024, 1, 15)                → "2024-01-15"
     *   write LocalDateTime.of(2024, 1, 15, 10, 30, 45) → "2024-01-15T10:30:45"
     *   read  "2024-01-15" as LocalDate                → 2024-01-15
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Two steps: register a module, and switch off one serialisation feature.</li>
     * </ul>
     *
     * @return a configured mapper
     */
    public ObjectMapper createDateMapper() {
        throw new UnsupportedOperationException("TODO: implement createDateMapper");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Writes an {@link Instant} as a JSON string literal in ISO-8601 UTC form
     * (including the surrounding double quotes).
     *
     * <p>Examples:
     * <pre>
     *   instantToJson(2024-01-15T10:30:00Z)      → "2024-01-15T10:30:00Z"
     *   instantToJson(Instant.EPOCH)             → "1970-01-01T00:00:00Z"
     *   instantToJson(2024-01-15T10:30:00.500Z)  → "2024-01-15T10:30:00.500Z"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reuse your task 1 mapper.</li>
     * </ul>
     *
     * @param instant the moment to write
     * @return a JSON string literal, quotes included
     * @throws JsonProcessingException if serialisation fails
     */
    public String instantToJson(Instant instant) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement instantToJson");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Reads a JSON string literal (quotes included) holding an ISO date into a
     * {@link LocalDate}. Text that is not a valid date makes the exception propagate.
     *
     * <p>Examples:
     * <pre>
     *   jsonToLocalDate("2024-01-15")    → 2024-01-15
     *   jsonToLocalDate("2024-02-29")    → 2024-02-29   (leap day)
     *   jsonToLocalDate("not-a-date")    → throws JsonProcessingException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The input is a complete JSON document that happens to be a single string.</li>
     * </ul>
     *
     * @param json a JSON string literal such as {@code "2024-01-15"} (with quotes)
     * @return the parsed date
     * @throws JsonProcessingException if the text is not a valid ISO date
     */
    public LocalDate jsonToLocalDate(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement jsonToLocalDate");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Serialises an {@link Invoice}. Its {@code issuedOn} date must be written in the
     * pattern {@code dd.MM.yyyy} — apply that by annotating {@code Invoice.issuedOn}.
     *
     * <p>Examples:
     * <pre>
     *   Invoice(7, 2024-01-15)  → {"id":7,"issuedOn":"15.01.2024"}
     *   Invoice(8, 2023-12-01)  → {"id":8,"issuedOn":"01.12.2023"}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One annotation with a {@code pattern} attribute; remember {@code MM} is month.</li>
     * </ul>
     *
     * @param invoice the invoice to write
     * @return JSON text (key order is not significant)
     * @throws JsonProcessingException if serialisation fails
     */
    public String invoiceToJson(Invoice invoice) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement invoiceToJson");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Parses JSON into an {@link Invoice}, reading {@code issuedOn} in the
     * {@code dd.MM.yyyy} pattern (same annotation as task 4).
     *
     * <p>Examples:
     * <pre>
     *   {"id":7,"issuedOn":"15.01.2024"}  → Invoice(7, 2024-01-15)
     *   {"id":8,"issuedOn":"01.12.2023"}  → Invoice(8, 2023-12-01)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The plain {@code ObjectMapper} cannot read dates — use the one from task 1.</li>
     * </ul>
     *
     * @param json JSON text of an invoice
     * @return the parsed invoice
     * @throws JsonProcessingException if the text is malformed or the date does not match
     */
    public Invoice jsonToInvoice(String json) throws JsonProcessingException {
        throw new UnsupportedOperationException("TODO: implement jsonToInvoice");
    }

    // ── Model you must annotate ────────────────────────────────────────────────

    /** Invoice — annotate {@code issuedOn} for tasks 4 and 5. */
    public static class Invoice {
        private int id;
        private LocalDate issuedOn;

        public Invoice() { }

        public Invoice(int id, LocalDate issuedOn) {
            this.id = id;
            this.issuedOn = issuedOn;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public LocalDate getIssuedOn() { return issuedOn; }
        public void setIssuedOn(LocalDate issuedOn) { this.issuedOn = issuedOn; }
    }
}
