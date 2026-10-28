package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbforge.json_jackson_basics.UnknownFieldsDemo.Point;
import com.vbforge.json_jackson_basics.UnknownFieldsDemo.Settings;
import com.vbforge.json_jackson_basics.UnknownFieldsTasks.Envelope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * UnknownFieldsTasksTest — tests for every method in {@link UnknownFieldsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code UnknownFieldsTasks.java}, implement a method (and annotate {@code Envelope} where asked).</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=UnknownFieldsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("UnknownFieldsTasks — your implementations")
class UnknownFieldsTasksTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private UnknownFieldsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new UnknownFieldsTasks();
    }

    private static JsonNode tree(String json) throws Exception {
        return MAPPER.readTree(json);
    }

    // ── Task 1: parseLenient ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement parseLenient()")
    @DisplayName("Task 1 — parseLenient()")
    class ParseLenientTest {

        @Test
        @DisplayName("known fields only")
        void known() throws Exception {
            Settings s = tasks.parseLenient("{\"theme\":\"dark\",\"fontSize\":14}");
            assertThat(s.getTheme()).isEqualTo("dark");
            assertThat(s.getFontSize()).isEqualTo(14);
        }

        @Test
        @DisplayName("an extra key is ignored")
        void extraKey() throws Exception {
            Settings s = tasks.parseLenient("{\"theme\":\"dark\",\"fontSize\":14,\"colour\":\"red\"}");
            assertThat(s.getTheme()).isEqualTo("dark");
            assertThat(s.getFontSize()).isEqualTo(14);
        }

        @Test
        @DisplayName("only unrelated keys → defaults")
        void onlyUnrelated() throws Exception {
            Settings s = tasks.parseLenient("{\"unrelated\":1}");
            assertThat(s.getTheme()).isNull();
            assertThat(s.getFontSize()).isZero();
        }
    }

    // ── Task 2: findUnknownProperty ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement findUnknownProperty()")
    @DisplayName("Task 2 — findUnknownProperty()")
    class FindUnknownPropertyTest {

        @Test
        @DisplayName("all keys known → null")
        void allKnown() throws Exception {
            assertThat(tasks.findUnknownProperty("{\"theme\":\"dark\",\"fontSize\":14}")).isNull();
        }

        @Test
        @DisplayName("one unknown key → its name")
        void oneUnknown() throws Exception {
            assertThat(tasks.findUnknownProperty("{\"theme\":\"dark\",\"colour\":\"red\"}")).isEqualTo("colour");
        }

        @Test
        @DisplayName("several unknown keys → the first one")
        void firstUnknown() throws Exception {
            assertThat(tasks.findUnknownProperty("{\"a\":1,\"b\":2}")).isEqualTo("a");
        }

        @Test
        @DisplayName("empty object → null")
        void emptyObject() throws Exception {
            assertThat(tasks.findUnknownProperty("{}")).isNull();
        }
    }

    // ── Task 3: parseEnvelope ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement parseEnvelope()")
    @DisplayName("Task 3 — parseEnvelope()")
    class ParseEnvelopeTest {

        @Test
        @DisplayName("unknown keys land in extras")
        void basic() throws Exception {
            Envelope e = tasks.parseEnvelope("{\"type\":\"click\",\"x\":10,\"y\":20}");
            assertThat(e.getType()).isEqualTo("click");
            assertThat(e.getExtras()).containsEntry("x", 10).containsEntry("y", 20).hasSize(2);
        }

        @Test
        @DisplayName("no extra keys → empty extras")
        void noExtras() throws Exception {
            Envelope e = tasks.parseEnvelope("{\"type\":\"ping\"}");
            assertThat(e.getType()).isEqualTo("ping");
            assertThat(e.getExtras()).isEmpty();
        }

        @Test
        @DisplayName("extras can hold nested values")
        void nestedExtra() throws Exception {
            Envelope e = tasks.parseEnvelope("{\"type\":\"t\",\"meta\":{\"k\":\"v\"}}");
            assertThat(e.getExtras()).containsKey("meta");
            assertThat(e.getExtras().get("meta")).isInstanceOf(java.util.Map.class);
        }
    }

    // ── Task 4: envelopeToJson ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement envelopeToJson()")
    @DisplayName("Task 4 — envelopeToJson()")
    class EnvelopeToJsonTest {

        @Test
        @DisplayName("extras are written as top-level keys")
        void flattened() throws Exception {
            Envelope e = new Envelope();
            e.setType("click");
            e.addExtra("x", 10);
            e.addExtra("y", 20);
            assertThat(tree(tasks.envelopeToJson(e))).isEqualTo(tree("{\"type\":\"click\",\"x\":10,\"y\":20}"));
        }

        @Test
        @DisplayName("no extras → only type")
        void noExtras() throws Exception {
            Envelope e = new Envelope();
            e.setType("ping");
            assertThat(tree(tasks.envelopeToJson(e))).isEqualTo(tree("{\"type\":\"ping\"}"));
        }

        @Test
        @DisplayName("there is no nested \"extras\" key")
        void noNesting() throws Exception {
            Envelope e = new Envelope();
            e.setType("t");
            e.addExtra("k", "v");
            assertThat(tree(tasks.envelopeToJson(e)).has("extras")).isFalse();
        }

        @Test
        @DisplayName("parse then write round-trips")
        void roundTrip() throws Exception {
            String original = "{\"type\":\"click\",\"x\":10,\"y\":20}";
            assertThat(tree(tasks.envelopeToJson(tasks.parseEnvelope(original)))).isEqualTo(tree(original));
        }
    }

    // ── Task 5: parseRequiredPoint ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement parseRequiredPoint()")
    @DisplayName("Task 5 — parseRequiredPoint()")
    class ParseRequiredPointTest {

        @Test
        @DisplayName("{\"x\":1,\"y\":2} → Point[x=1, y=2]")
        void basic() throws Exception {
            assertThat(tasks.parseRequiredPoint("{\"x\":1,\"y\":2}")).isEqualTo(new Point(1, 2));
        }

        @Test
        @DisplayName("key order does not matter")
        void reversed() throws Exception {
            assertThat(tasks.parseRequiredPoint("{\"y\":2,\"x\":1}")).isEqualTo(new Point(1, 2));
        }

        @ParameterizedTest(name = "missing coordinate: {0}")
        @ValueSource(strings = {"{\"y\":2}", "{\"x\":1}", "{}"})
        @DisplayName("a missing x or y → JsonMappingException")
        void missing(String json) {
            assertThatThrownBy(() -> tasks.parseRequiredPoint(json)).isInstanceOf(JsonMappingException.class);
        }
    }
}
