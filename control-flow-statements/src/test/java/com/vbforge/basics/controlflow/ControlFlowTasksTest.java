package com.vbforge.basics.controlflow;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 🧪 Tests for {@link ControlFlowTasks}.
 *
 * <p>All tests are RED until you implement the corresponding method.
 * Your goal: make every test GREEN without modifying this file.
 */
@Disabled("Tasks not implemented yet — remove this annotation when ready to solve")
@DisplayName("Control Flow Tasks — implement to make tests pass")
class ControlFlowTasksTest {

    private final ControlFlowTasks tasks = new ControlFlowTasks();


    // ── Task 1 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 1 — httpCategory(statusCode)")
    class Task1 {

        @ParameterizedTest(name = "{0} → success")
        @ValueSource(ints = {200, 201, 204})
        @DisplayName("2xx codes map to 'success'")
        void successCodes(int code) {
            assertThat(tasks.httpCategory(code)).isEqualTo("success");
        }

        @ParameterizedTest(name = "{0} → redirect")
        @ValueSource(ints = {301, 302})
        @DisplayName("3xx redirect codes map to 'redirect'")
        void redirectCodes(int code) {
            assertThat(tasks.httpCategory(code)).isEqualTo("redirect");
        }

        @ParameterizedTest(name = "{0} → client error")
        @ValueSource(ints = {400, 401, 403, 404})
        @DisplayName("4xx codes map to 'client error'")
        void clientErrorCodes(int code) {
            assertThat(tasks.httpCategory(code)).isEqualTo("client error");
        }

        @ParameterizedTest(name = "{0} → server error")
        @ValueSource(ints = {500, 502, 503})
        @DisplayName("5xx codes map to 'server error'")
        void serverErrorCodes(int code) {
            assertThat(tasks.httpCategory(code)).isEqualTo("server error");
        }

        @ParameterizedTest(name = "{0} → unknown")
        @ValueSource(ints = {0, 100, 299, 999})
        @DisplayName("unlisted codes map to 'unknown'")
        void unknownCodes(int code) {
            assertThat(tasks.httpCategory(code)).isEqualTo("unknown");
        }
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 2 — letterGrade(score)")
    class Task2 {

        @ParameterizedTest(name = "score {0} → A")
        @ValueSource(ints = {90, 95, 100})
        @DisplayName("90–100 maps to A")
        void gradeA(int score) {
            assertThat(tasks.letterGrade(score)).isEqualTo("A");
        }

        @ParameterizedTest(name = "score {0} → B")
        @ValueSource(ints = {80, 85, 89})
        @DisplayName("80–89 maps to B")
        void gradeB(int score) {
            assertThat(tasks.letterGrade(score)).isEqualTo("B");
        }

        @ParameterizedTest(name = "score {0} → C")
        @ValueSource(ints = {70, 75, 79})
        @DisplayName("70–79 maps to C")
        void gradeC(int score) {
            assertThat(tasks.letterGrade(score)).isEqualTo("C");
        }

        @ParameterizedTest(name = "score {0} → D")
        @ValueSource(ints = {60, 65, 69})
        @DisplayName("60–69 maps to D")
        void gradeD(int score) {
            assertThat(tasks.letterGrade(score)).isEqualTo("D");
        }

        @ParameterizedTest(name = "score {0} → F")
        @ValueSource(ints = {0, 30, 59})
        @DisplayName("0–59 maps to F")
        void gradeF(int score) {
            assertThat(tasks.letterGrade(score)).isEqualTo("F");
        }

        @Test
        @DisplayName("exact boundary scores map to the correct grade")
        void boundaries() {
            assertThat(tasks.letterGrade(90)).isEqualTo("A");
            assertThat(tasks.letterGrade(89)).isEqualTo("B");
            assertThat(tasks.letterGrade(80)).isEqualTo("B");
            assertThat(tasks.letterGrade(79)).isEqualTo("C");
            assertThat(tasks.letterGrade(70)).isEqualTo("C");
            assertThat(tasks.letterGrade(69)).isEqualTo("D");
            assertThat(tasks.letterGrade(60)).isEqualTo("D");
            assertThat(tasks.letterGrade(59)).isEqualTo("F");
            assertThat(tasks.letterGrade(0)).isEqualTo("F");
        }

        @Test
        @DisplayName("scores outside 0–100 throw IllegalArgumentException")
        void invalidScores() {
            assertThatThrownBy(() -> tasks.letterGrade(-1))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.letterGrade(101))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 3 — sumRange(start, end)")
    class Task3 {

        @ParameterizedTest(name = "sumRange({0}, {1}) = {2}")
        @CsvSource({
            "1,  5,  15",
            "1,  1,   1",
            "3,  3,   3",
            "0,  3,   6",
            "1, 10,  55"
        })
        @DisplayName("valid ranges produce correct inclusive sums")
        void validRanges(int start, int end, int expected) {
            assertThat(tasks.sumRange(start, end)).isEqualTo(expected);
        }

        @Test
        @DisplayName("start > end returns 0")
        void startGreaterThanEnd() {
            assertThat(tasks.sumRange(5, 1)).isEqualTo(0);
            assertThat(tasks.sumRange(10, 0)).isEqualTo(0);
        }

        @Test
        @DisplayName("negative ranges are summed correctly")
        void negativeRanges() {
            assertThat(tasks.sumRange(-3, -1)).isEqualTo(-6);  // -3 + -2 + -1
            assertThat(tasks.sumRange(-2, 2)).isEqualTo(0);    // -2 + -1 + 0 + 1 + 2
        }
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 4 — floorPowerOfTwo(n)")
    class Task4 {

        @ParameterizedTest(name = "floorPowerOfTwo({0}) = {1}")
        @CsvSource({
            "  1,   1",
            "  2,   2",
            "  3,   2",
            "  4,   4",
            "  5,   4",
            "  7,   4",
            "  8,   8",
            "  9,   8",
            "100,  64",
            "128, 128"
        })
        @DisplayName("returns correct floor power of two for various inputs")
        void floorPow2(int n, int expected) {
            assertThat(tasks.floorPowerOfTwo(n)).isEqualTo(expected);
        }

        @Test
        @DisplayName("exact powers of two return themselves")
        void exactPowersReturnThemselves() {
            assertThat(tasks.floorPowerOfTwo(1)).isEqualTo(1);
            assertThat(tasks.floorPowerOfTwo(16)).isEqualTo(16);
            assertThat(tasks.floorPowerOfTwo(1024)).isEqualTo(1024);
        }

        @Test
        @DisplayName("n < 1 throws IllegalArgumentException")
        void invalidInputThrows() {
            assertThatThrownBy(() -> tasks.floorPowerOfTwo(0))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.floorPowerOfTwo(-5))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 5 — filterPositive(numbers)")
    class Task5 {

        @Test
        @DisplayName("keeps only strictly positive elements, preserves order")
        void filtersCorrectly() {
            assertThat(tasks.filterPositive(List.of(1, -2, 3, 0, 4)))
                .containsExactly(1, 3, 4);
        }

        @Test
        @DisplayName("zero is excluded — only strictly positive values pass")
        void zeroExcluded() {
            assertThat(tasks.filterPositive(List.of(0, 0, 0))).isEmpty();
            assertThat(tasks.filterPositive(List.of(-1, 0, 1))).containsExactly(1);
        }

        @Test
        @DisplayName("all negative list returns empty list")
        void allNegative() {
            assertThat(tasks.filterPositive(List.of(-1, -2, -3))).isEmpty();
        }

        @Test
        @DisplayName("all positive list returns all elements")
        void allPositive() {
            assertThat(tasks.filterPositive(List.of(1, 2, 3))).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("empty list returns empty list")
        void emptyList() {
            assertThat(tasks.filterPositive(List.of())).isEmpty();
        }

        @Test
        @DisplayName("single element lists work correctly")
        void singleElement() {
            assertThat(tasks.filterPositive(List.of(5))).containsExactly(5);
            assertThat(tasks.filterPositive(List.of(-5))).isEmpty();
            assertThat(tasks.filterPositive(List.of(0))).isEmpty();
        }
    }


    // ── Task 6 ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Task 6 — formatTyped(Object)")
    class Task6 {

        @Test
        @DisplayName("Integer formats as 'int(n)'")
        void integer() {
            assertThat(tasks.formatTyped(42)).isEqualTo("int(42)");
            assertThat(tasks.formatTyped(0)).isEqualTo("int(0)");
            assertThat(tasks.formatTyped(-7)).isEqualTo("int(-7)");
        }

        @Test
        @DisplayName("Double formats as 'double(n)'")
        void doubleVal() {
            assertThat(tasks.formatTyped(3.14)).isEqualTo("double(3.14)");
            assertThat(tasks.formatTyped(0.0)).isEqualTo("double(0.0)");
        }

        @Test
        @DisplayName("String formats as 'str(len):value'")
        void string() {
            assertThat(tasks.formatTyped("hi")).isEqualTo("str(2):hi");
            assertThat(tasks.formatTyped("")).isEqualTo("str(0):");
            assertThat(tasks.formatTyped("hello")).isEqualTo("str(5):hello");
        }

        @Test
        @DisplayName("Boolean true formats as 'yes', false as 'no'")
        void booleans() {
            assertThat(tasks.formatTyped(true)).isEqualTo("yes");
            assertThat(tasks.formatTyped(false)).isEqualTo("no");
        }

        @Test
        @DisplayName("null formats as 'null'")
        void nullValue() {
            assertThat(tasks.formatTyped(null)).isEqualTo("null");
        }

        @Test
        @DisplayName("unknown types format as '?'")
        void unknownType() {
            assertThat(tasks.formatTyped(List.of(1, 2))).isEqualTo("?");
            assertThat(tasks.formatTyped(new int[]{1})).isEqualTo("?");
        }
    }
}
