package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JsonBasicsDemoTest — verifies every concept shown in {@link JsonBasicsDemo}.
 *
 * <p>These tests are always GREEN. Read them as an executable specification of the
 * core Jackson API.
 */
@DisplayName("JsonBasicsDemo — API contracts")
class JsonBasicsDemoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static String q(String singleQuoted) {
        return singleQuoted.replace('\'', '"');
    }

    // ── 1. ObjectMapper basics ────────────────────────────────────────────────

    @Nested
    @DisplayName("1. ObjectMapper basics")
    class ObjectMapperBasicsTest {

        @Test
        @DisplayName("writeValueAsString turns a Map into compact JSON in iteration order")
        void mapToJson() throws Exception {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", 1);
            m.put("name", "Ann");
            m.put("tags", Arrays.asList("a", "b"));
            assertThat(MAPPER.writeValueAsString(m)).isEqualTo("{\"id\":1,\"name\":\"Ann\",\"tags\":[\"a\",\"b\"]}");
        }

        @Test
        @DisplayName("readValue into Map gives LinkedHashMap and ArrayList")
        void untypedRead() throws Exception {
            Map<?, ?> m = MAPPER.readValue(q("{'id':1,'tags':['a']}"), Map.class);
            assertThat(m).isInstanceOf(LinkedHashMap.class);
            assertThat(m.get("tags")).isInstanceOf(ArrayList.class);
        }

        @Test
        @DisplayName("untyped numbers are Integer, Long or Double depending on size")
        void numberTypes() throws Exception {
            Map<?, ?> m = MAPPER.readValue(q("{'small':1,'big':12345678901,'dec':1.5}"), Map.class);
            assertThat(m.get("small")).isInstanceOf(Integer.class);
            assertThat(m.get("big")).isInstanceOf(Long.class);
            assertThat(m.get("dec")).isInstanceOf(Double.class);
        }

        @Test
        @DisplayName("strings are written with quotes")
        void stringLiteral() throws Exception {
            assertThat(MAPPER.writeValueAsString("hi")).isEqualTo("\"hi\"");
        }

        @Test
        @DisplayName("scalars are valid documents: readValue(\"42\") and null")
        void scalars() throws Exception {
            assertThat(MAPPER.readValue("42", Integer.class)).isEqualTo(42);
            assertThat(MAPPER.writeValueAsString(null)).isEqualTo("null");
        }

        @Test
        @DisplayName("quotes inside strings are escaped")
        void escaping() throws Exception {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("q", "say \"hi\"");
            assertThat(MAPPER.writeValueAsString(m)).isEqualTo("{\"q\":\"say \\\"hi\\\"\"}");
        }
    }

    // ── 2. TypeReference ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. TypeReference")
    class TypeReferenceTest {

        @Test
        @DisplayName("List.class loses the element type — elements are LinkedHashMap")
        void erasure() throws Exception {
            List<?> raw = MAPPER.readValue(q("[{'id':1}]"), List.class);
            assertThat(raw.get(0)).isInstanceOf(LinkedHashMap.class);
        }

        @Test
        @DisplayName("TypeReference keeps generics: List<Map<String,Integer>>")
        void typed() throws Exception {
            List<Map<String, Integer>> typed = MAPPER.readValue(
                    q("[{'id':1},{'id':2}]"), new TypeReference<List<Map<String, Integer>>>() { });
            assertThat(typed).hasSize(2);
            assertThat(typed.get(1).get("id")).isEqualTo(2);
        }

        @Test
        @DisplayName("nested generics: Map<String, List<Integer>>")
        void nested() throws Exception {
            Map<String, List<Integer>> scores = MAPPER.readValue(
                    q("{'ann':[1,2],'bob':[3]}"), new TypeReference<Map<String, List<Integer>>>() { });
            assertThat(scores.get("ann")).containsExactly(1, 2);
            assertThat(scores.get("bob")).containsExactly(3);
        }

        @Test
        @DisplayName("arrays carry their element type — no TypeReference needed")
        void arrays() throws Exception {
            assertThat(MAPPER.readValue("[1,2,3]", int[].class)).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("TypeFactory.constructCollectionType is the programmatic alternative")
        void typeFactory() throws Exception {
            List<Integer> list = MAPPER.readValue("[4,5]",
                    MAPPER.getTypeFactory().constructCollectionType(List.class, Integer.class));
            assertThat(list).containsExactly(4, 5);
        }
    }

    // ── 3. Tree model ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Tree model")
    class TreeModelTest {

        private JsonNode root() throws Exception {
            return MAPPER.readTree(q("{'user':{'name':'Ann','age':30,'address':null},'tags':['a','b']}"));
        }

        @Test
        @DisplayName("get(...) navigates and asText/asInt convert values")
        void navigate() throws Exception {
            JsonNode root = root();
            assertThat(root.get("user").get("name").asText()).isEqualTo("Ann");
            assertThat(root.get("user").get("age").asInt()).isEqualTo(30);
            assertThat(root.get("user").get("age").isInt()).isTrue();
        }

        @Test
        @DisplayName("get(missing) returns Java null; path(missing) returns a MissingNode")
        void getVsPath() throws Exception {
            JsonNode root = root();
            assertThat(root.get("nope")).isNull();
            assertThat(root.path("nope").isMissingNode()).isTrue();
        }

        @Test
        @DisplayName("path(...) chains safely and asText(default) supplies a fallback")
        void safeChain() throws Exception {
            assertThat(root().path("nope").path("deeper").asText("n/a")).isEqualTo("n/a");
        }

        @Test
        @DisplayName("JSON null is a NullNode, not Java null")
        void nullNode() throws Exception {
            JsonNode address = root().get("user").get("address");
            assertThat(address).isNotNull();
            assertThat(address.isNull()).isTrue();
        }

        @Test
        @DisplayName("at(pointer) reaches into objects and arrays")
        void pointer() throws Exception {
            JsonNode root = root();
            assertThat(root.at("/tags/1").asText()).isEqualTo("b");
            assertThat(root.at("/user/zip").isMissingNode()).isTrue();
        }

        @Test
        @DisplayName("fieldNames() iterates keys in document order")
        void fieldNames() throws Exception {
            List<String> names = new ArrayList<>();
            root().get("user").fieldNames().forEachRemaining(names::add);
            assertThat(names).containsExactly("name", "age", "address");
        }

        @Test
        @DisplayName("ObjectNode can be built by hand")
        void buildTree() {
            ObjectNode out = MAPPER.createObjectNode();
            out.put("id", 7);
            out.put("ok", true);
            out.putArray("tags").add("x").add("y");
            out.set("child", MAPPER.createObjectNode().put("k", "v"));
            assertThat(out.toString())
                    .isEqualTo("{\"id\":7,\"ok\":true,\"tags\":[\"x\",\"y\"],\"child\":{\"k\":\"v\"}}");
        }
    }

    // ── 4. Pretty printing and errors ─────────────────────────────────────────

    @Nested
    @DisplayName("4. Pretty printing and errors")
    class PrettyAndErrorsTest {

        @Test
        @DisplayName("default output is compact — no whitespace between tokens")
        void compact() throws Exception {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("a", 1);
            assertThat(MAPPER.writeValueAsString(m)).isEqualTo("{\"a\":1}");
        }

        @Test
        @DisplayName("writerWithDefaultPrettyPrinter adds line breaks and indentation")
        void pretty() throws Exception {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("a", 1);
            String pretty = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(m);
            assertThat(pretty).contains("\n").contains("  \"a\" : 1");
        }

        @Test
        @DisplayName("unquoted field names are rejected")
        void unquotedNames() {
            assertThatThrownBy(() -> MAPPER.readTree("{a:1}")).isInstanceOf(JsonParseException.class);
        }

        @Test
        @DisplayName("single quotes are rejected")
        void singleQuotes() {
            assertThatThrownBy(() -> MAPPER.readTree("{'a':1}")).isInstanceOf(JsonParseException.class);
        }

        @Test
        @DisplayName("non-JSON text is rejected")
        void notJson() {
            assertThatThrownBy(() -> MAPPER.readTree("not json")).isInstanceOf(JsonParseException.class);
        }

        @Test
        @DisplayName("truncated JSON is rejected with a JsonProcessingException")
        void truncated() {
            assertThatThrownBy(() -> MAPPER.readTree("{\"a\":")).isInstanceOf(JsonProcessingException.class);
        }

        @Test
        @DisplayName("JsonProcessingException is a checked IOException")
        void checked() {
            assertThat(IOException.class).isAssignableFrom(JsonProcessingException.class);
        }
    }

    // ── Demo runs end-to-end ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Demo main()")
    class MainRunsTest {

        @Test
        @DisplayName("runs every section without throwing")
        void runs() {
            assertThatCode(() -> JsonBasicsDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
