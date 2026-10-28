package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.PersonPojo;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.PersonRecord;
import com.vbforge.json_jackson_basics.PojosAndRecordsDemo.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PojosAndRecordsTasksTest — tests for every method in {@link PojosAndRecordsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code PojosAndRecordsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=PojosAndRecordsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("PojosAndRecordsTasks — your implementations")
class PojosAndRecordsTasksTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private PojosAndRecordsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new PojosAndRecordsTasks();
    }

    private static JsonNode tree(String json) throws Exception {
        return MAPPER.readTree(json);
    }

    // ── Task 1: personToJson ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement personToJson()")
    @DisplayName("Task 1 — personToJson()")
    class PersonToJsonTest {

        @Test
        @DisplayName("Ann/30 → {\"name\":\"Ann\",\"age\":30}")
        void basic() throws Exception {
            String json = tasks.personToJson(new PersonRecord("Ann", 30));
            assertThat(tree(json)).isEqualTo(tree("{\"name\":\"Ann\",\"age\":30}"));
        }

        @Test
        @DisplayName("null name is written as JSON null")
        void nullName() throws Exception {
            String json = tasks.personToJson(new PersonRecord(null, 0));
            assertThat(tree(json)).isEqualTo(tree("{\"name\":null,\"age\":0}"));
        }
    }

    // ── Task 2: jsonToPerson ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement jsonToPerson()")
    @DisplayName("Task 2 — jsonToPerson()")
    class JsonToPersonTest {

        @Test
        @DisplayName("full object → record")
        void basic() throws Exception {
            assertThat(tasks.jsonToPerson("{\"name\":\"Ann\",\"age\":30}")).isEqualTo(new PersonRecord("Ann", 30));
        }

        @Test
        @DisplayName("missing age → 0")
        void missingAge() throws Exception {
            assertThat(tasks.jsonToPerson("{\"name\":\"Bob\"}")).isEqualTo(new PersonRecord("Bob", 0));
        }

        @Test
        @DisplayName("empty object → all defaults")
        void emptyObject() throws Exception {
            assertThat(tasks.jsonToPerson("{}")).isEqualTo(new PersonRecord(null, 0));
        }
    }

    // ── Task 3: jsonToPeople ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement jsonToPeople()")
    @DisplayName("Task 3 — jsonToPeople()")
    class JsonToPeopleTest {

        @Test
        @DisplayName("array of two → list of two records in order")
        void basic() throws Exception {
            List<PersonRecord> people = tasks.jsonToPeople(
                    "[{\"name\":\"Ann\",\"age\":30},{\"name\":\"Bob\",\"age\":41}]");
            assertThat(people).containsExactly(new PersonRecord("Ann", 30), new PersonRecord("Bob", 41));
        }

        @Test
        @DisplayName("empty array → empty list")
        void empty() throws Exception {
            assertThat(tasks.jsonToPeople("[]")).isEmpty();
        }

        @Test
        @DisplayName("elements are PersonRecord, not LinkedHashMap")
        void elementType() throws Exception {
            List<PersonRecord> people = tasks.jsonToPeople("[{\"name\":\"Ann\",\"age\":30}]");
            assertThat(people.get(0)).isInstanceOf(PersonRecord.class);
        }
    }

    // ── Task 4: personToMap ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement personToMap()")
    @DisplayName("Task 4 — personToMap()")
    class PersonToMapTest {

        @Test
        @DisplayName("Ann/30 → {name=Ann, age=30}")
        void basic() {
            Map<String, Object> map = tasks.personToMap(new PersonRecord("Ann", 30));
            assertThat(map).containsEntry("name", "Ann").containsEntry("age", 30).hasSize(2);
        }

        @Test
        @DisplayName("age is an Integer")
        void integerValue() {
            assertThat(tasks.personToMap(new PersonRecord("Ann", 30)).get("age")).isInstanceOf(Integer.class);
        }

        @Test
        @DisplayName("null name is kept as a null value")
        void nullName() {
            Map<String, Object> map = tasks.personToMap(new PersonRecord(null, 1));
            assertThat(map).containsKey("name");
            assertThat(map.get("name")).isNull();
        }
    }

    // ── Task 5: jsonToTeam ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement jsonToTeam()")
    @DisplayName("Task 5 — jsonToTeam()")
    class JsonToTeamTest {

        @Test
        @DisplayName("team with one member")
        void basic() throws Exception {
            Team team = tasks.jsonToTeam("{\"name\":\"Core\",\"members\":[{\"name\":\"Ann\",\"age\":30}]}");
            assertThat(team.name()).isEqualTo("Core");
            assertThat(team.members()).containsExactly(new PersonRecord("Ann", 30));
        }

        @Test
        @DisplayName("team with no members")
        void empty() throws Exception {
            Team team = tasks.jsonToTeam("{\"name\":\"Empty\",\"members\":[]}");
            assertThat(team).isEqualTo(new Team("Empty", Collections.<PersonRecord>emptyList()));
        }
    }

    // ── Task 6: jsonToPojo ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement jsonToPojo()")
    @DisplayName("Task 6 — jsonToPojo()")
    class JsonToPojoTest {

        @Test
        @DisplayName("all fields present")
        void basic() throws Exception {
            PersonPojo p = tasks.jsonToPojo("{\"name\":\"Ann\",\"age\":30,\"active\":true}");
            assertThat(p.getName()).isEqualTo("Ann");
            assertThat(p.getAge()).isEqualTo(30);
            assertThat(p.isActive()).isTrue();
        }

        @Test
        @DisplayName("missing fields keep defaults")
        void defaults() throws Exception {
            PersonPojo p = tasks.jsonToPojo("{\"name\":\"Bob\"}");
            assertThat(p.getName()).isEqualTo("Bob");
            assertThat(p.getAge()).isZero();
            assertThat(p.isActive()).isFalse();
        }

        @ParameterizedTest(name = "active={0} → {0}")
        @CsvSource({"true", "false"})
        @DisplayName("the active flag is read as given")
        void activeFlag(boolean active) throws Exception {
            PersonPojo p = tasks.jsonToPojo("{\"name\":\"X\",\"active\":" + active + "}");
            assertThat(p.isActive()).isEqualTo(active);
        }
    }
}
