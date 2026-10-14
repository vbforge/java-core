package com.vbforge.oop_encapsulation_patterns;

import com.vbforge.oop_encapsulation_patterns.AccessModifiersTasks.LeakyPoint;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersTasks.Levels;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersTasks.Marker;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersTasks.Temperature;
import com.vbforge.oop_encapsulation_patterns.AccessModifiersTasks.TidyPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AccessModifiersTasksTest — tests for every method in {@link AccessModifiersTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code AccessModifiersTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=AccessModifiersTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("AccessModifiersTasks — your implementations")
class AccessModifiersTasksTest {

    private AccessModifiersTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new AccessModifiersTasks();
    }

    // ── Task 1: accessLevel ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement accessLevel()")
    @DisplayName("Task 1 — accessLevel()")
    class AccessLevelTest {

        @ParameterizedTest(name = "field {0} → {1}")
        @CsvSource({
                "secret, private",
                "shared, package-private",
                "family, protected",
                "open,   public",
        })
        @DisplayName("parametrized: fields of Levels")
        void fields(String field, String expected) throws Exception {
            assertThat(tasks.accessLevel(Levels.class.getDeclaredField(field))).isEqualTo(expected);
        }

        @Test
        @DisplayName("a public JDK method → public")
        void publicMethod() throws Exception {
            assertThat(tasks.accessLevel(String.class.getMethod("length"))).isEqualTo("public");
        }

        @Test
        @DisplayName("Object.clone() → protected")
        void protectedMethod() throws Exception {
            assertThat(tasks.accessLevel(Object.class.getDeclaredMethod("clone"))).isEqualTo("protected");
        }

        @Test
        @DisplayName("works for constructors too: the implicit constructor of a public class is public")
        void constructor() throws Exception {
            assertThat(tasks.accessLevel(Levels.class.getDeclaredConstructor())).isEqualTo("public");
        }
    }

    // ── Task 2: privateFieldNames ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement privateFieldNames()")
    @DisplayName("Task 2 — privateFieldNames()")
    class PrivateFieldNamesTest {

        @Test
        @DisplayName("TidyPoint → [x, y] (public constant ORIGIN excluded)")
        void tidyPoint() {
            assertThat(tasks.privateFieldNames(TidyPoint.class)).containsExactly("x", "y");
        }

        @Test
        @DisplayName("LeakyPoint → [y]")
        void leakyPoint() {
            assertThat(tasks.privateFieldNames(LeakyPoint.class)).containsExactly("y");
        }

        @Test
        @DisplayName("Levels → [secret]")
        void levels() {
            assertThat(tasks.privateFieldNames(Levels.class)).containsExactly("secret");
        }

        @Test
        @DisplayName("a class without fields → []")
        void noFields() {
            assertThat(tasks.privateFieldNames(Marker.class)).isEmpty();
        }
    }

    // ── Task 3: isEncapsulated ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isEncapsulated()")
    @DisplayName("Task 3 — isEncapsulated()")
    class IsEncapsulatedTest {

        @Test
        @DisplayName("TidyPoint → true (static constant ignored)")
        void tidyPoint() {
            assertThat(tasks.isEncapsulated(TidyPoint.class)).isTrue();
        }

        @Test
        @DisplayName("LeakyPoint → false (public x)")
        void leakyPoint() {
            assertThat(tasks.isEncapsulated(LeakyPoint.class)).isFalse();
        }

        @Test
        @DisplayName("Levels → false (shared, family and open are not private)")
        void levels() {
            assertThat(tasks.isEncapsulated(Levels.class)).isFalse();
        }

        @Test
        @DisplayName("no fields at all → true")
        void noFields() {
            assertThat(tasks.isEncapsulated(Marker.class)).isTrue();
        }
    }

    // ── Task 4: Temperature.isWarmerThan ──────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isWarmerThan()")
    @DisplayName("Task 4 — Temperature.isWarmerThan()")
    class IsWarmerThanTest {

        @ParameterizedTest(name = "{0} warmer than {1} → {2}")
        @CsvSource({
                "20.0,  10.0,  true",
                "10.0,  20.0,  false",
                "15.0,  15.0,  false",
                "-5.0,  -10.0, true",
                "0.0,   -0.5,  true",
        })
        @DisplayName("parametrized comparisons")
        void comparisons(double mine, double theirs, boolean expected) {
            assertThat(new Temperature(mine).isWarmerThan(new Temperature(theirs))).isEqualTo(expected);
        }

        @Test
        @DisplayName("a temperature is not warmer than itself")
        void notWarmerThanItself() {
            Temperature t = new Temperature(30);
            assertThat(t.isWarmerThan(t)).isFalse();
        }
    }

    // ── Task 5: publicApiSize ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement publicApiSize()")
    @DisplayName("Task 5 — publicApiSize()")
    class PublicApiSizeTest {

        @Test
        @DisplayName("Temperature → 2")
        void temperature() {
            assertThat(tasks.publicApiSize(Temperature.class)).isEqualTo(2);
        }

        @Test
        @DisplayName("TidyPoint → 2 (constructor and fields are not counted)")
        void tidyPoint() {
            assertThat(tasks.publicApiSize(TidyPoint.class)).isEqualTo(2);
        }

        @Test
        @DisplayName("Marker → 0")
        void marker() {
            assertThat(tasks.publicApiSize(Marker.class)).isZero();
        }

        @Test
        @DisplayName("interfaces: Runnable → 1, Comparable → 1")
        void interfaces() {
            assertThat(tasks.publicApiSize(Runnable.class)).isEqualTo(1);
            assertThat(tasks.publicApiSize(Comparable.class)).isEqualTo(1);
        }

        @Test
        @DisplayName("only DECLARED methods count: inherited ones are ignored")
        void inheritedIgnored() {
            assertThat(tasks.publicApiSize(LeakyPoint.class)).isEqualTo(1);   // y() only, not Object's methods
        }
    }
}
