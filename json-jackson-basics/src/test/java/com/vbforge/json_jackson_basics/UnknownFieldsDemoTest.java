package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.vbforge.json_jackson_basics.UnknownFieldsDemo.LenientSettings;
import com.vbforge.json_jackson_basics.UnknownFieldsDemo.Point;
import com.vbforge.json_jackson_basics.UnknownFieldsDemo.Settings;
import com.vbforge.json_jackson_basics.UnknownFieldsDemo.TrackedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * UnknownFieldsDemoTest — verifies every concept shown in {@link UnknownFieldsDemo}.
 *
 * <p>These tests are always GREEN.
 */
@DisplayName("UnknownFieldsDemo — API contracts")
class UnknownFieldsDemoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String WITH_EXTRA = "{\"theme\":\"dark\",\"fontSize\":14,\"colour\":\"red\"}";

    // ── 1. Default ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Default: fail on unknown")
    class FailOnUnknownTest {

        @Test
        @DisplayName("an unknown key throws UnrecognizedPropertyException")
        void throwsOnUnknown() {
            assertThatThrownBy(() -> MAPPER.readValue(WITH_EXTRA, Settings.class))
                    .isInstanceOf(UnrecognizedPropertyException.class);
        }

        @Test
        @DisplayName("the exception names the offending property")
        void propertyName() {
            assertThatThrownBy(() -> MAPPER.readValue(WITH_EXTRA, Settings.class))
                    .isInstanceOfSatisfying(UnrecognizedPropertyException.class,
                            e -> assertThat(e.getPropertyName()).isEqualTo("colour"));
        }

        @Test
        @DisplayName("the exception lists the known properties")
        void knownIds() {
            assertThatThrownBy(() -> MAPPER.readValue(WITH_EXTRA, Settings.class))
                    .isInstanceOfSatisfying(UnrecognizedPropertyException.class,
                            e -> assertThat(e.getKnownPropertyIds()).contains("theme", "fontSize"));
        }
    }

    // ── 2. Lenient mapper / reader ────────────────────────────────────────────

    @Nested
    @DisplayName("2. Lenient mapper / reader")
    class LenientMapperTest {

        @Test
        @DisplayName("disabling the feature on a mapper ignores unknown keys")
        void mapperWide() throws Exception {
            ObjectMapper lenient = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            Settings s = lenient.readValue(WITH_EXTRA, Settings.class);
            assertThat(s.getTheme()).isEqualTo("dark");
            assertThat(s.getFontSize()).isEqualTo(14);
        }

        @Test
        @DisplayName("a reader can relax the rule for a single call")
        void perReader() throws Exception {
            Settings s = MAPPER.readerFor(Settings.class)
                    .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(WITH_EXTRA);
            assertThat(s.getTheme()).isEqualTo("dark");
        }

        @Test
        @DisplayName("the shared mapper stays strict after using a derived reader")
        void sharedStaysStrict() throws Exception {
            MAPPER.readerFor(Settings.class)
                    .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(WITH_EXTRA);
            assertThatThrownBy(() -> MAPPER.readValue(WITH_EXTRA, Settings.class))
                    .isInstanceOf(UnrecognizedPropertyException.class);
        }
    }

    // ── 3. @JsonIgnoreProperties ──────────────────────────────────────────────

    @Nested
    @DisplayName("3. @JsonIgnoreProperties")
    class IgnoreUnknownAnnotationTest {

        @Test
        @DisplayName("ignoreUnknown = true makes a class tolerant for any mapper")
        void tolerantClass() throws Exception {
            LenientSettings s = MAPPER.readValue(
                    "{\"theme\":\"light\",\"fontSize\":12,\"extra\":true}", LenientSettings.class);
            assertThat(s.getTheme()).isEqualTo("light");
            assertThat(s.getFontSize()).isEqualTo(12);
        }
    }

    // ── 4. Any setter / getter ────────────────────────────────────────────────

    @Nested
    @DisplayName("4. @JsonAnySetter / @JsonAnyGetter")
    class AnySetterTest {

        @Test
        @DisplayName("unknown keys are collected into the extras map")
        void collects() throws Exception {
            TrackedEvent e = MAPPER.readValue("{\"type\":\"click\",\"x\":10,\"y\":20}", TrackedEvent.class);
            assertThat(e.getType()).isEqualTo("click");
            assertThat(e.getExtras()).containsEntry("x", 10).containsEntry("y", 20).hasSize(2);
        }

        @Test
        @DisplayName("extras are written back as top-level keys")
        void flattens() throws Exception {
            TrackedEvent e = MAPPER.readValue("{\"type\":\"click\",\"x\":10,\"y\":20}", TrackedEvent.class);
            JsonNode json = MAPPER.readTree(MAPPER.writeValueAsString(e));
            assertThat(json).isEqualTo(MAPPER.readTree("{\"type\":\"click\",\"x\":10,\"y\":20}"));
        }
    }

    // ── 5. Missing fields ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Missing fields")
    class MissingFieldsTest {

        @Test
        @DisplayName("a POJO keeps defaults for missing fields")
        void pojoDefaults() throws Exception {
            Settings s = MAPPER.readValue("{}", Settings.class);
            assertThat(s.getTheme()).isNull();
            assertThat(s.getFontSize()).isZero();
        }

        @Test
        @DisplayName("a record gets default values for missing components")
        void recordDefaults() throws Exception {
            assertThat(MAPPER.readValue("{\"x\":1}", Point.class)).isEqualTo(new Point(1, 0));
        }

        @Test
        @DisplayName("FAIL_ON_MISSING_CREATOR_PROPERTIES turns a missing component into an error")
        void strictRecord() {
            ObjectMapper strict = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
            assertThatThrownBy(() -> strict.readValue("{\"x\":1}", Point.class))
                    .isInstanceOf(JsonMappingException.class);
        }

        @Test
        @DisplayName("the feature does not affect setter-based POJOs")
        void strictDoesNotAffectPojo() throws Exception {
            ObjectMapper strict = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
            assertThat(strict.readValue("{}", Settings.class).getFontSize()).isZero();
        }
    }

    // ── Demo runs end-to-end ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Demo main()")
    class MainRunsTest {

        @Test
        @DisplayName("runs every section without throwing")
        void runs() {
            assertThatCode(() -> UnknownFieldsDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
