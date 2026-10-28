package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbforge.json_jackson_basics.JsonDatesTasks.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JsonDatesTasksTest — tests for every method in {@link JsonDatesTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code JsonDatesTasks.java}, implement a method (and annotate {@code Invoice} where asked).</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=JsonDatesTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("JsonDatesTasks — your implementations")
class JsonDatesTasksTest {

    private static final ObjectMapper PLAIN = new ObjectMapper();

    private JsonDatesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new JsonDatesTasks();
    }

    private static JsonNode tree(String json) throws Exception {
        return PLAIN.readTree(json);
    }

    // ── Task 1: createDateMapper ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement createDateMapper()")
    @DisplayName("Task 1 — createDateMapper()")
    class CreateDateMapperTest {

        @Test
        @DisplayName("writes LocalDate as an ISO string")
        void localDate() throws Exception {
            ObjectMapper mapper = tasks.createDateMapper();
            assertThat(mapper.writeValueAsString(LocalDate.of(2024, 1, 15))).isEqualTo("\"2024-01-15\"");
        }

        @Test
        @DisplayName("writes LocalDateTime as an ISO string, not an array")
        void localDateTime() throws Exception {
            ObjectMapper mapper = tasks.createDateMapper();
            assertThat(mapper.writeValueAsString(LocalDateTime.of(2024, 1, 15, 10, 30, 45)))
                    .isEqualTo("\"2024-01-15T10:30:45\"");
        }

        @Test
        @DisplayName("reads \"2024-01-15\" as LocalDate")
        void readsLocalDate() throws Exception {
            ObjectMapper mapper = tasks.createDateMapper();
            assertThat(mapper.readValue("\"2024-01-15\"", LocalDate.class)).isEqualTo(LocalDate.of(2024, 1, 15));
        }
    }

    // ── Task 2: instantToJson ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement instantToJson()")
    @DisplayName("Task 2 — instantToJson()")
    class InstantToJsonTest {

        @Test
        @DisplayName("2024-01-15T10:30:00Z → \"2024-01-15T10:30:00Z\"")
        void basic() throws Exception {
            assertThat(tasks.instantToJson(Instant.parse("2024-01-15T10:30:00Z")))
                    .isEqualTo("\"2024-01-15T10:30:00Z\"");
        }

        @Test
        @DisplayName("epoch → \"1970-01-01T00:00:00Z\"")
        void epoch() throws Exception {
            assertThat(tasks.instantToJson(Instant.EPOCH)).isEqualTo("\"1970-01-01T00:00:00Z\"");
        }

        @Test
        @DisplayName("fractions of a second are kept")
        void fraction() throws Exception {
            assertThat(tasks.instantToJson(Instant.parse("2024-01-15T10:30:00.500Z")))
                    .isEqualTo("\"2024-01-15T10:30:00.500Z\"");
        }
    }

    // ── Task 3: jsonToLocalDate ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement jsonToLocalDate()")
    @DisplayName("Task 3 — jsonToLocalDate()")
    class JsonToLocalDateTest {

        @Test
        @DisplayName("\"2024-01-15\" → 2024-01-15")
        void basic() throws Exception {
            assertThat(tasks.jsonToLocalDate("\"2024-01-15\"")).isEqualTo(LocalDate.of(2024, 1, 15));
        }

        @Test
        @DisplayName("leap day 2024-02-29")
        void leapDay() throws Exception {
            assertThat(tasks.jsonToLocalDate("\"2024-02-29\"")).isEqualTo(LocalDate.of(2024, 2, 29));
        }

        @Test
        @DisplayName("invalid text → JsonProcessingException")
        void invalid() {
            assertThatThrownBy(() -> tasks.jsonToLocalDate("\"not-a-date\""))
                    .isInstanceOf(JsonProcessingException.class);
        }
    }

    // ── Task 4: invoiceToJson ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement invoiceToJson()")
    @DisplayName("Task 4 — invoiceToJson()")
    class InvoiceToJsonTest {

        @Test
        @DisplayName("Invoice(7, 2024-01-15) → issuedOn \"15.01.2024\"")
        void basic() throws Exception {
            String json = tasks.invoiceToJson(new Invoice(7, LocalDate.of(2024, 1, 15)));
            assertThat(tree(json)).isEqualTo(tree("{\"id\":7,\"issuedOn\":\"15.01.2024\"}"));
        }

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "2023-12-01, 01.12.2023",
                "2024-02-29, 29.02.2024",
                "2000-01-09, 09.01.2000",
        })
        @DisplayName("day and month are zero-padded")
        void parametrized(String iso, String expected) throws Exception {
            String json = tasks.invoiceToJson(new Invoice(8, LocalDate.parse(iso)));
            assertThat(tree(json).get("issuedOn").asText()).isEqualTo(expected);
        }
    }

    // ── Task 5: jsonToInvoice ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement jsonToInvoice()")
    @DisplayName("Task 5 — jsonToInvoice()")
    class JsonToInvoiceTest {

        @Test
        @DisplayName("issuedOn \"15.01.2024\" → 2024-01-15")
        void basic() throws Exception {
            Invoice inv = tasks.jsonToInvoice("{\"id\":7,\"issuedOn\":\"15.01.2024\"}");
            assertThat(inv.getId()).isEqualTo(7);
            assertThat(inv.getIssuedOn()).isEqualTo(LocalDate.of(2024, 1, 15));
        }

        @Test
        @DisplayName("issuedOn \"01.12.2023\" → 2023-12-01")
        void december() throws Exception {
            assertThat(tasks.jsonToInvoice("{\"id\":8,\"issuedOn\":\"01.12.2023\"}").getIssuedOn())
                    .isEqualTo(LocalDate.of(2023, 12, 1));
        }

        @Test
        @DisplayName("an ISO date does not match the pattern → JsonProcessingException")
        void wrongFormat() {
            assertThatThrownBy(() -> tasks.jsonToInvoice("{\"id\":9,\"issuedOn\":\"2024-01-15\"}"))
                    .isInstanceOf(JsonProcessingException.class);
        }
    }
}
