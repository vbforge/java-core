package com.vbforge.json_jackson_basics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vbforge.json_jackson_basics.JsonAnnotationsTasks.Credentials;
import com.vbforge.json_jackson_basics.JsonAnnotationsTasks.Employee;
import com.vbforge.json_jackson_basics.JsonAnnotationsTasks.GeoPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JsonAnnotationsTasksTest — tests for every method in {@link JsonAnnotationsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code JsonAnnotationsTasks.java}, implement a method AND annotate its model class.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=JsonAnnotationsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("JsonAnnotationsTasks — your implementations")
class JsonAnnotationsTasksTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonAnnotationsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new JsonAnnotationsTasks();
    }

    private static JsonNode tree(String json) throws Exception {
        return MAPPER.readTree(json);
    }

    // ── Task 1: employeeToJson ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement employeeToJson()")
    @DisplayName("Task 1 — employeeToJson()")
    class EmployeeToJsonTest {

        @Test
        @DisplayName("renames fullName, hides internalNotes, omits null nickname")
        void basic() throws Exception {
            String json = tasks.employeeToJson(new Employee(1, "Ann Lee", "ann@x.io", null, "vip"));
            assertThat(tree(json)).isEqualTo(tree("{\"id\":1,\"full_name\":\"Ann Lee\",\"email\":\"ann@x.io\"}"));
        }

        @Test
        @DisplayName("a non-null nickname is written")
        void withNickname() throws Exception {
            String json = tasks.employeeToJson(new Employee(2, "Bob", "b@x.io", "bobby", null));
            assertThat(tree(json)).isEqualTo(
                    tree("{\"id\":2,\"full_name\":\"Bob\",\"email\":\"b@x.io\",\"nickname\":\"bobby\"}"));
        }

        @Test
        @DisplayName("internalNotes never appears, even when set")
        void notesHidden() throws Exception {
            String json = tasks.employeeToJson(new Employee(3, "Cy", "c@x.io", "c", "secret notes"));
            assertThat(json).doesNotContain("internalNotes").doesNotContain("secret notes");
        }

        @Test
        @DisplayName("the Java name fullName is not used as a key")
        void oldNameGone() throws Exception {
            String json = tasks.employeeToJson(new Employee(4, "Di", "d@x.io", null, null));
            assertThat(tree(json).has("fullName")).isFalse();
        }
    }

    // ── Task 2: employeeFromJson ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement employeeFromJson()")
    @DisplayName("Task 2 — employeeFromJson()")
    class EmployeeFromJsonTest {

        @Test
        @DisplayName("reads full_name and email")
        void basic() throws Exception {
            Employee e = tasks.employeeFromJson("{\"id\":2,\"full_name\":\"Bob\",\"email\":\"b@x.io\"}");
            assertThat(e.getId()).isEqualTo(2);
            assertThat(e.getFullName()).isEqualTo("Bob");
            assertThat(e.getEmail()).isEqualTo("b@x.io");
        }

        @Test
        @DisplayName("accepts \"mail\" as an alias for email")
        void alias() throws Exception {
            Employee e = tasks.employeeFromJson("{\"id\":3,\"full_name\":\"Cy\",\"mail\":\"c@x.io\"}");
            assertThat(e.getEmail()).isEqualTo("c@x.io");
        }

        @Test
        @DisplayName("internalNotes in the input is skipped silently")
        void notesSkipped() throws Exception {
            Employee e = tasks.employeeFromJson("{\"id\":4,\"full_name\":\"Di\",\"internalNotes\":\"hack\"}");
            assertThat(e.getFullName()).isEqualTo("Di");
            assertThat(e.getInternalNotes()).isNull();
        }
    }

    // ── Task 3: credentialsToJson ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement credentialsToJson()")
    @DisplayName("Task 3 — credentialsToJson()")
    class CredentialsToJsonTest {

        @Test
        @DisplayName("ann/s3cret → {\"username\":\"ann\"}")
        void basic() throws Exception {
            String json = tasks.credentialsToJson(new Credentials("ann", "s3cret"));
            assertThat(tree(json)).isEqualTo(tree("{\"username\":\"ann\"}"));
        }

        @Test
        @DisplayName("the password text never leaks")
        void noLeak() throws Exception {
            String json = tasks.credentialsToJson(new Credentials("bob", "hunter2"));
            assertThat(json).doesNotContain("hunter2").doesNotContain("password");
        }
    }

    // ── Task 4: credentialsFromJson ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement credentialsFromJson()")
    @DisplayName("Task 4 — credentialsFromJson()")
    class CredentialsFromJsonTest {

        @Test
        @DisplayName("password is still read from the input")
        void basic() throws Exception {
            Credentials c = tasks.credentialsFromJson("{\"username\":\"ann\",\"password\":\"s3cret\"}");
            assertThat(c.getUsername()).isEqualTo("ann");
            assertThat(c.getPassword()).isEqualTo("s3cret");
        }

        @Test
        @DisplayName("missing password stays null")
        void missingPassword() throws Exception {
            Credentials c = tasks.credentialsFromJson("{\"username\":\"bob\"}");
            assertThat(c.getUsername()).isEqualTo("bob");
            assertThat(c.getPassword()).isNull();
        }
    }

    // ── Task 5: geoPointFromJson ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement geoPointFromJson()")
    @DisplayName("Task 5 — geoPointFromJson()")
    class GeoPointFromJsonTest {

        @Test
        @DisplayName("{\"lat\":50.45,\"lon\":30.52} → lat/lon")
        void basic() throws Exception {
            GeoPoint p = tasks.geoPointFromJson("{\"lat\":50.45,\"lon\":30.52}");
            assertThat(p.getLat()).isEqualTo(50.45);
            assertThat(p.getLon()).isEqualTo(30.52);
        }

        @Test
        @DisplayName("key order does not matter")
        void reversedOrder() throws Exception {
            GeoPoint p = tasks.geoPointFromJson("{\"lon\":1.0,\"lat\":2.0}");
            assertThat(p.getLat()).isEqualTo(2.0);
            assertThat(p.getLon()).isEqualTo(1.0);
        }

        @ParameterizedTest(name = "lat={0} lon={1}")
        @CsvSource({"0.0, 0.0", "-33.87, 151.21", "90.0, 180.0"})
        @DisplayName("boundary and negative coordinates")
        void parametrized(double lat, double lon) throws Exception {
            GeoPoint p = tasks.geoPointFromJson("{\"lat\":" + lat + ",\"lon\":" + lon + "}");
            assertThat(p.getLat()).isEqualTo(lat);
            assertThat(p.getLon()).isEqualTo(lon);
        }
    }
}
