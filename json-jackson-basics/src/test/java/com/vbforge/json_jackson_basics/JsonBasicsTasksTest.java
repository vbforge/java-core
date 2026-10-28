package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JsonBasicsTasksTest — tests for every method in {@link JsonBasicsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code JsonBasicsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=JsonBasicsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 4 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("JsonBasicsTasks — your implementations")
class JsonBasicsTasksTest {

    private JsonBasicsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new JsonBasicsTasks();
    }

    // ── Task 1: toJson ────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toJson()")
    @DisplayName("Task 1 — toJson()")
    class ToJsonTest {

        @Test
        @DisplayName("{id:1, name:Ann} → {\"id\":1,\"name\":\"Ann\"}")
        void basic() throws Exception {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", 1);
            m.put("name", "Ann");
            assertThat(tasks.toJson(m)).isEqualTo("{\"id\":1,\"name\":\"Ann\"}");
        }

        @Test
        @DisplayName("lists are written as JSON arrays")
        void withList() throws Exception {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tags", Arrays.asList("a", "b"));
            assertThat(tasks.toJson(m)).isEqualTo("{\"tags\":[\"a\",\"b\"]}");
        }

        @Test
        @DisplayName("null values are written as JSON null")
        void nullValue() throws Exception {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("note", null);
            assertThat(tasks.toJson(m)).isEqualTo("{\"note\":null}");
        }

        @Test
        @DisplayName("empty map → {}")
        void emptyMap() throws Exception {
            assertThat(tasks.toJson(new LinkedHashMap<>())).isEqualTo("{}");
        }

        @Test
        @DisplayName("quotes inside strings are escaped")
        void escapes() throws Exception {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("q", "say \"hi\"");
            assertThat(tasks.toJson(m)).isEqualTo("{\"q\":\"say \\\"hi\\\"\"}");
        }
    }

    // ── Task 2: parseStringList ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement parseStringList()")
    @DisplayName("Task 2 — parseStringList()")
    class ParseStringListTest {

        @Test
        @DisplayName("[\"a\",\"b\",\"c\"] → [a, b, c]")
        void basic() throws Exception {
            List<String> result = tasks.parseStringList("[\"a\",\"b\",\"c\"]");
            assertThat(result).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("single element")
        void single() throws Exception {
            assertThat(tasks.parseStringList("[\"x\"]")).containsExactly("x");
        }

        @Test
        @DisplayName("empty array → empty list")
        void empty() throws Exception {
            assertThat(tasks.parseStringList("[]")).isEmpty();
        }

        @Test
        @DisplayName("an object instead of an array → JsonProcessingException")
        void notAnArray() {
            assertThatThrownBy(() -> tasks.parseStringList("{\"a\":1}"))
                    .isInstanceOf(JsonProcessingException.class);
        }
    }

    // ── Task 3: textAtPointer ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement textAtPointer()")
    @DisplayName("Task 3 — textAtPointer()")
    class TextAtPointerTest {

        @Test
        @DisplayName("/user/name → \"Ann\"")
        void textValue() throws Exception {
            assertThat(tasks.textAtPointer("{\"user\":{\"name\":\"Ann\"}}", "/user/name")).isEqualTo("Ann");
        }

        @Test
        @DisplayName("numbers come back as text: /user/age → \"30\"")
        void numberAsText() throws Exception {
            assertThat(tasks.textAtPointer("{\"user\":{\"age\":30}}", "/user/age")).isEqualTo("30");
        }

        @Test
        @DisplayName("array index: /tags/1 → \"b\"")
        void arrayIndex() throws Exception {
            assertThat(tasks.textAtPointer("{\"tags\":[\"a\",\"b\"]}", "/tags/1")).isEqualTo("b");
        }

        @Test
        @DisplayName("missing path → null")
        void missing() throws Exception {
            assertThat(tasks.textAtPointer("{\"user\":{\"name\":\"Ann\"}}", "/user/email")).isNull();
        }

        @Test
        @DisplayName("JSON null → null")
        void jsonNull() throws Exception {
            assertThat(tasks.textAtPointer("{\"user\":{\"email\":null}}", "/user/email")).isNull();
        }

        @Test
        @DisplayName("malformed JSON → JsonProcessingException")
        void malformed() {
            assertThatThrownBy(() -> tasks.textAtPointer("{\"user\":", "/user"))
                    .isInstanceOf(JsonProcessingException.class);
        }
    }

    // ── Task 4: isValidJson ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isValidJson()")
    @DisplayName("Task 4 — isValidJson()")
    class IsValidJsonTest {

        @ParameterizedTest(name = "valid: {0}")
        @ValueSource(strings = {"{\"a\":1}", "[1,2,3]", "42", "true", "\"text\""})
        @DisplayName("valid documents → true")
        void valid(String json) {
            assertThat(tasks.isValidJson(json)).isTrue();
        }

        @ParameterizedTest(name = "invalid: {0}")
        @ValueSource(strings = {"{\"a\":", "{a:1}", "{'a':1}", "not json", "[1,2"})
        @DisplayName("broken documents → false (never throws)")
        void invalid(String json) {
            assertThat(tasks.isValidJson(json)).isFalse();
        }
    }
}
