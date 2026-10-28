package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.PersonPojo;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.PersonRecord;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.Point;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.PointRecord;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PojosAndRecordsDemoTest — verifies every concept shown in {@link PojosAndRecordsDemo}.
 *
 * <p>These tests are always GREEN. JSON is compared as trees so key order never matters.
 */
@DisplayName("PojosAndRecordsDemo — API contracts")
class PojosAndRecordsDemoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static JsonNode tree(String json) throws Exception {
        return MAPPER.readTree(json);
    }

    // ── 1. POJOs ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. POJOs")
    class PojoTest {

        @Test
        @DisplayName("getters define what is written; isActive() becomes \"active\"")
        void serialise() throws Exception {
            String json = MAPPER.writeValueAsString(new PersonPojo("Ann", 30, true));
            assertThat(tree(json)).isEqualTo(tree("{\"name\":\"Ann\",\"age\":30,\"active\":true}"));
        }

        @Test
        @DisplayName("a private field without accessor is not written")
        void hiddenField() throws Exception {
            String json = MAPPER.writeValueAsString(new PersonPojo("Ann", 30, true));
            assertThat(json).doesNotContain("secret");
        }

        @Test
        @DisplayName("no-arg constructor + setters populate the object on read")
        void deserialise() throws Exception {
            PersonPojo p = MAPPER.readValue("{\"name\":\"Bob\",\"age\":41,\"active\":false}", PersonPojo.class);
            assertThat(p.getName()).isEqualTo("Bob");
            assertThat(p.getAge()).isEqualTo(41);
            assertThat(p.isActive()).isFalse();
        }

        @Test
        @DisplayName("a key matching only an accessor-less private field is unknown on read")
        void unknownOnRead() {
            assertThatThrownBy(() -> MAPPER.readValue("{\"name\":\"Bob\",\"secret\":\"x\"}", PersonPojo.class))
                    .isInstanceOf(JsonMappingException.class);
        }
    }

    // ── 2. Records ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Records")
    class RecordTest {

        @Test
        @DisplayName("records serialise with no annotations")
        void serialise() throws Exception {
            String json = MAPPER.writeValueAsString(new PersonRecord("Ann", 30));
            assertThat(tree(json)).isEqualTo(tree("{\"name\":\"Ann\",\"age\":30}"));
        }

        @Test
        @DisplayName("records round-trip and keep value equality")
        void roundTrip() throws Exception {
            PersonRecord ann = new PersonRecord("Ann", 30);
            PersonRecord back = MAPPER.readValue(MAPPER.writeValueAsString(ann), PersonRecord.class);
            assertThat(back).isEqualTo(ann);
        }

        @Test
        @DisplayName("a missing component gets its default (0 for int)")
        void missingComponent() throws Exception {
            PersonRecord partial = MAPPER.readValue("{\"name\":\"Bob\"}", PersonRecord.class);
            assertThat(partial.name()).isEqualTo("Bob");
            assertThat(partial.age()).isZero();
        }
    }

    // ── 3. No default constructor ─────────────────────────────────────────────

    @Nested
    @DisplayName("3. No default constructor")
    class NoDefaultConstructorTest {

        @Test
        @DisplayName("a class without creator can still be WRITTEN")
        void writeWorks() throws Exception {
            String json = MAPPER.writeValueAsString(new Point(1, 2));
            assertThat(tree(json)).isEqualTo(tree("{\"x\":1,\"y\":2}"));
        }

        @Test
        @DisplayName("…but cannot be READ: InvalidDefinitionException")
        void readFails() {
            assertThatThrownBy(() -> MAPPER.readValue("{\"x\":1,\"y\":2}", Point.class))
                    .isInstanceOf(InvalidDefinitionException.class);
        }

        @Test
        @DisplayName("the same shape as a record reads fine")
        void recordReads() throws Exception {
            PointRecord p = MAPPER.readValue("{\"x\":1,\"y\":2}", PointRecord.class);
            assertThat(p).isEqualTo(new PointRecord(1, 2));
        }
    }

    // ── 4. Nested and collections ─────────────────────────────────────────────

    @Nested
    @DisplayName("4. Nested objects and collections")
    class NestedTest {

        @Test
        @DisplayName("a record containing a list of records round-trips")
        void team() throws Exception {
            Team team = new Team("Core", Arrays.asList(new PersonRecord("Ann", 30), new PersonRecord("Bob", 41)));
            String json = MAPPER.writeValueAsString(team);
            assertThat(tree(json)).isEqualTo(tree(
                    "{\"name\":\"Core\",\"members\":[{\"name\":\"Ann\",\"age\":30},{\"name\":\"Bob\",\"age\":41}]}"));
            assertThat(MAPPER.readValue(json, Team.class)).isEqualTo(team);
        }

        @Test
        @DisplayName("a top-level List<PersonRecord> needs a TypeReference")
        void list() throws Exception {
            List<PersonRecord> people = MAPPER.readValue(
                    "[{\"name\":\"Cy\",\"age\":5}]", new TypeReference<List<PersonRecord>>() { });
            assertThat(people).containsExactly(new PersonRecord("Cy", 5));
        }

        @Test
        @DisplayName("Map<String, PersonRecord> keeps record values")
        void map() throws Exception {
            Map<String, PersonRecord> byId = MAPPER.readValue(
                    "{\"a\":{\"name\":\"Di\",\"age\":7}}", new TypeReference<Map<String, PersonRecord>>() { });
            assertThat(byId.get("a")).isEqualTo(new PersonRecord("Di", 7));
        }
    }

    // ── 5. convertValue ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. convertValue / valueToTree")
    class ConvertValueTest {

        @Test
        @DisplayName("convertValue turns a POJO into a Map with Integer values")
        void pojoToMap() {
            Map<String, Object> map = MAPPER.convertValue(
                    new PersonPojo("Ann", 30, true), new TypeReference<Map<String, Object>>() { });
            assertThat(map).containsEntry("name", "Ann").containsEntry("age", 30).containsEntry("active", true);
        }

        @Test
        @DisplayName("record -> Map -> record is lossless")
        void recordRoundTrip() {
            PersonRecord rec = new PersonRecord("Bob", 41);
            Map<String, Object> map = MAPPER.convertValue(rec, new TypeReference<Map<String, Object>>() { });
            assertThat(MAPPER.convertValue(map, PersonRecord.class)).isEqualTo(rec);
        }

        @Test
        @DisplayName("valueToTree / treeToValue round-trip through a JsonNode")
        void tree() throws Exception {
            PersonRecord rec = new PersonRecord("Bob", 41);
            JsonNode node = MAPPER.valueToTree(rec);
            assertThat(node.get("name").asText()).isEqualTo("Bob");
            assertThat(MAPPER.treeToValue(node, PersonRecord.class)).isEqualTo(rec);
        }

        @Test
        @DisplayName("convertValue reports failures as IllegalArgumentException")
        void failureIsUnchecked() {
            Map<String, Object> mapWithExtra = MAPPER.convertValue(
                    new PersonPojo("Ann", 30, true), new TypeReference<Map<String, Object>>() { });
            assertThatThrownBy(() -> MAPPER.convertValue(mapWithExtra, PersonRecord.class))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Demo runs end-to-end ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Demo main()")
    class MainRunsTest {

        @Test
        @DisplayName("runs every section without throwing")
        void runs() {
            assertThatCode(() -> PojosAndRecordsDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
