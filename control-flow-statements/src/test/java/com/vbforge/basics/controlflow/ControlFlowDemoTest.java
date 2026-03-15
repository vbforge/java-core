package com.vbforge.basics.controlflow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Control Flow Statements")
class ControlFlowDemoTest {

    private final ControlFlowDemo demo = new ControlFlowDemo();


    // ── if-else ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("if / else if / else")
    class IfElse {

        @ParameterizedTest(name = "{0}°C → {1}")
        @CsvSource({
            "-10, freezing",
            " -1, freezing",
            "  0, cold",      // 0 < 0 is false → falls to next branch (< 10) → cold
            "  5, cold",
            " 15, cool",
            " 25, warm",
            " 35, hot"
        })
        @DisplayName("classifyTemperature covers all branches correctly")
        void classifyTemperature(int celsius, String expected) {
            assertThat(demo.classifyTemperature(celsius)).isEqualTo(expected);
        }

        @Test
        @DisplayName("boundary values fall into correct branches")
        void classifyTemperatureBoundaries() {
            assertThat(demo.classifyTemperature(-1)).isEqualTo("freezing"); // -1 < 0 → freezing
            assertThat(demo.classifyTemperature(0)).isEqualTo("cold");      // 0 < 0 false, 0 < 10 true → cold
            assertThat(demo.classifyTemperature(9)).isEqualTo("cold");      // 9 < 10 → cold
            assertThat(demo.classifyTemperature(10)).isEqualTo("cool");     // 10 < 10 false, 10 < 20 true → cool
            assertThat(demo.classifyTemperature(20)).isEqualTo("warm");     // 20 < 20 false, 20 < 30 true → warm
            assertThat(demo.classifyTemperature(30)).isEqualTo("hot");      // 30 < 30 false → hot
        }

        @Test
        @DisplayName("nested if-else: hasAccount=true, isVerified=true → welcome")
        void nestedIfBothTrue() {
            assertThat(demo.loginCheck(true, true)).isEqualTo("welcome");
        }

        @Test
        @DisplayName("nested if-else: hasAccount=true, isVerified=false → verify email")
        void nestedIfNotVerified() {
            assertThat(demo.loginCheck(true, false)).isEqualTo("verify your email");
        }

        @Test
        @DisplayName("nested if-else: hasAccount=false → please register")
        void nestedIfNoAccount() {
            assertThat(demo.loginCheck(false, true)).isEqualTo("please register");
            assertThat(demo.loginCheck(false, false)).isEqualTo("please register");
        }
    }


    // ── Classic switch ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Classic switch statement")
    class ClassicSwitch {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
            "Monday,    weekday",
            "Tuesday,   weekday",
            "Wednesday, weekday",
            "Thursday,  weekday",
            "Friday,    weekday",
            "Saturday,  weekend",
            "Sunday,    weekend"
        })
        @DisplayName("getDayType classifies all 7 days correctly")
        void getDayType(String day, String expected) {
            assertThat(demo.getDayType(day)).isEqualTo(expected);
        }

        @Test
        @DisplayName("getDayType returns unknown for invalid input")
        void getDayTypeUnknown() {
            assertThat(demo.getDayType("Funday")).isEqualTo("unknown");
        }

        @Test
        @DisplayName("getDayType is case-insensitive (converts to upper internally)")
        void getDayTypeCaseInsensitive() {
            assertThat(demo.getDayType("monday")).isEqualTo("weekday");
            assertThat(demo.getDayType("SUNDAY")).isEqualTo("weekend");
        }

        @Test
        @DisplayName("fall-through: ADMIN gets cumulative level 111")
        void permissionLevelAdmin() {
            assertThat(demo.getPermissionLevel("ADMIN")).isEqualTo(111);
        }

        @Test
        @DisplayName("fall-through: MODERATOR gets level 11")
        void permissionLevelModerator() {
            assertThat(demo.getPermissionLevel("MODERATOR")).isEqualTo(11);
        }

        @Test
        @DisplayName("no fall-through: USER gets level 1 only")
        void permissionLevelUser() {
            assertThat(demo.getPermissionLevel("USER")).isEqualTo(1);
        }

        @Test
        @DisplayName("default case: unknown role gets level 0")
        void permissionLevelDefault() {
            assertThat(demo.getPermissionLevel("GUEST")).isEqualTo(0);
        }
    }


    // ── Switch expression ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Switch expression (Java 14+)")
    class SwitchExpression {

        @ParameterizedTest(name = "month {0} → {1}")
        @CsvSource({
            " 1, winter", " 2, winter", "12, winter",
            " 3, spring", " 4, spring", " 5, spring",
            " 6, summer", " 7, summer", " 8, summer",
            " 9, autumn", "10, autumn", "11, autumn"
        })
        @DisplayName("getSeasonArrow covers all 12 months across 4 seasons")
        void getSeasonArrow(int month, String expected) {
            assertThat(demo.getSeasonArrow(month)).isEqualTo(expected);
        }

        @Test
        @DisplayName("getSeasonArrow throws IllegalArgumentException for invalid month")
        void getSeasonArrowInvalidMonth() {
            assertThatThrownBy(() -> demo.getSeasonArrow(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid month");

            assertThatThrownBy(() -> demo.getSeasonArrow(13))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("getWorkingDays: February in leap year has 29 days")
        void workingDaysFebLeapYear() {
            assertThat(demo.getWorkingDays(2, 2024)).isEqualTo(29); // 2024 is leap
            assertThat(demo.getWorkingDays(2, 2000)).isEqualTo(29); // 2000 is leap (div by 400)
        }

        @Test
        @DisplayName("getWorkingDays: February in non-leap year has 28 days")
        void workingDaysFebNonLeap() {
            assertThat(demo.getWorkingDays(2, 2023)).isEqualTo(28);
            assertThat(demo.getWorkingDays(2, 1900)).isEqualTo(28); // 1900 not leap (div by 100 but not 400)
        }

        @ParameterizedTest(name = "month {0} has {1} days")
        @CsvSource({"4, 30", "6, 30", "9, 30", "11, 30", "1, 31", "3, 31", "7, 31"})
        @DisplayName("getWorkingDays returns correct days for 30/31-day months")
        void workingDaysOtherMonths(int month, int expected) {
            assertThat(demo.getWorkingDays(month, 2024)).isEqualTo(expected);
        }
    }


    // ── Pattern matching instanceof ───────────────────────────────────────────

    @Nested
    @DisplayName("Pattern matching — instanceof (Java 16+)")
    class PatternMatchingInstanceof {

        @Test
        @DisplayName("String input returns description with its length")
        void stringPattern() {
            assertThat(demo.describeObject("hello")).isEqualTo("String of length 5");
            assertThat(demo.describeObject("")).isEqualTo("String of length 0");
        }

        @Test
        @DisplayName("Positive Integer is described as positive")
        void integerPositive() {
            assertThat(demo.describeObject(42)).isEqualTo("Integer: positive");
        }

        @Test
        @DisplayName("Negative Integer is described as negative")
        void integerNegative() {
            assertThat(demo.describeObject(-7)).isEqualTo("Integer: negative");
        }

        @Test
        @DisplayName("Zero Integer is described as zero")
        void integerZero() {
            assertThat(demo.describeObject(0)).isEqualTo("Integer: zero");
        }

        @Test
        @DisplayName("int array returns description with its length")
        void intArrayPattern() {
            assertThat(demo.describeObject(new int[]{1, 2, 3})).isEqualTo("int array of length 3");
        }

        @Test
        @DisplayName("null input returns 'null'")
        void nullInput() {
            assertThat(demo.describeObject(null)).isEqualTo("null");
        }

        @Test
        @DisplayName("unknown type returns class simple name")
        void unknownType() {
            assertThat(demo.describeObject(3.14)).contains("Double");
        }
    }


    // ── Switch pattern matching ───────────────────────────────────────────────

    @Nested
    @DisplayName("Switch pattern matching with guards (Java 21+)")
    class SwitchPatternMatching {

        @Test
        @DisplayName("negative Integer is prefixed with 'negative int'")
        void negativeInteger() {
            assertThat(demo.formatValue(-5)).isEqualTo("negative int: -5");
        }

        @Test
        @DisplayName("positive Integer is prefixed with 'positive int'")
        void positiveInteger() {
            assertThat(demo.formatValue(42)).isEqualTo("positive int: 42");
        }

        @Test
        @DisplayName("zero Integer falls into positive int branch (not negative)")
        void zeroInteger() {
            assertThat(demo.formatValue(0)).isEqualTo("positive int: 0");
        }

        @Test
        @DisplayName("NaN double is detected via guard")
        void nanDouble() {
            assertThat(demo.formatValue(Double.NaN)).isEqualTo("NaN double");
        }

        @Test
        @DisplayName("regular double is formatted correctly")
        void regularDouble() {
            assertThat(demo.formatValue(3.14)).isEqualTo("double: 3.14");
        }

        @Test
        @DisplayName("empty string is detected via guard")
        void emptyString() {
            assertThat(demo.formatValue("")).isEqualTo("empty string");
        }

        @Test
        @DisplayName("non-empty string is wrapped in quotes")
        void nonEmptyString() {
            assertThat(demo.formatValue("Java")).isEqualTo("string: \"Java\"");
        }

        @Test
        @DisplayName("null is handled explicitly")
        void nullValue() {
            assertThat(demo.formatValue(null)).isEqualTo("null");
        }
    }


    // ── Loops ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("for, for-each, while, do-while loops")
    class Loops {

        @Test
        @DisplayName("sumArray: classic for loop sums all elements")
        void sumArray() {
            assertThat(demo.sumArray(new int[]{1, 2, 3, 4, 5})).isEqualTo(15);
            assertThat(demo.sumArray(new int[]{-1, -2, 3})).isEqualTo(0);
            assertThat(demo.sumArray(new int[]{})).isEqualTo(0);
        }

        @Test
        @DisplayName("reverseArray: iterating backwards produces reversed array")
        void reverseArray() {
            assertThat(demo.reverseArray(new int[]{1, 2, 3})).containsExactly(3, 2, 1);
            assertThat(demo.reverseArray(new int[]{5})).containsExactly(5);
        }

        @Test
        @DisplayName("sumList: for-each loop over List produces correct sum")
        void sumList() {
            assertThat(demo.sumList(java.util.List.of(1, 2, 3, 4))).isEqualTo(10);
            assertThat(demo.sumList(java.util.List.of())).isEqualTo(0);
        }

        @ParameterizedTest(name = "{0} has {1} digit(s)")
        @CsvSource({
            "     0,  1",
            "     5,  1",
            "    99,  2",
            "   100,  3",
            " 12345,  5",
            "    -1,  1",
            " -9999,  4"
        })
        @DisplayName("countDigits: while loop counts correctly for positive and negative")
        void countDigits(int number, int expected) {
            assertThat(demo.countDigits(number)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "collatz({0}) takes {1} steps")
        @CsvSource({
            " 1, 0",   // already 1
            " 2, 1",   // 2 → 1
            " 3, 7",   // 3 → 10 → 5 → 16 → 8 → 4 → 2 → 1
            " 6, 8",
            "27, 111"  // famous long chain
        })
        @DisplayName("collatzSteps: do-while runs at least once, reaches 1 correctly")
        void collatzSteps(int n, int expected) {
            assertThat(demo.collatzSteps(n)).isEqualTo(expected);
        }

        @Test
        @DisplayName("collatzSteps throws for non-positive input")
        void collatzStepsInvalidInput() {
            assertThatThrownBy(() -> demo.collatzSteps(0))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> demo.collatzSteps(-1))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }


    // ── break and continue ────────────────────────────────────────────────────

    @Nested
    @DisplayName("break, continue, labeled break/continue")
    class BreakAndContinue {

        @Test
        @DisplayName("findFirst: break exits at first match, returns correct index")
        void findFirstBreak() {
            assertThat(demo.findFirst(new int[]{3, 7, 2, 7, 9}, 7)).isEqualTo(1); // first 7
            assertThat(demo.findFirst(new int[]{1, 2, 3}, 3)).isEqualTo(2);
            assertThat(demo.findFirst(new int[]{1, 2, 3}, 9)).isEqualTo(-1);      // not found
        }

        @Test
        @DisplayName("sumEvens: continue skips odd numbers, sums only evens")
        void sumEvensContinue() {
            assertThat(demo.sumEvens(new int[]{1, 2, 3, 4, 5, 6})).isEqualTo(12); // 2+4+6
            assertThat(demo.sumEvens(new int[]{1, 3, 5})).isEqualTo(0);           // all odd
            assertThat(demo.sumEvens(new int[]{2, 4, 6})).isEqualTo(12);          // all even
        }

        @Test
        @DisplayName("findInMatrix: labeled break finds element and returns position")
        void findInMatrix() {
            int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
            assertThat(demo.findInMatrix(matrix, 5)).containsExactly(1, 1);
            assertThat(demo.findInMatrix(matrix, 1)).containsExactly(0, 0);
            assertThat(demo.findInMatrix(matrix, 9)).containsExactly(2, 2);
        }

        @Test
        @DisplayName("findInMatrix: returns {-1,-1} when target not found")
        void findInMatrixNotFound() {
            int[][] matrix = {{1, 2}, {3, 4}};
            assertThat(demo.findInMatrix(matrix, 99)).containsExactly(-1, -1);
        }

        @Test
        @DisplayName("sumPositiveRows: labeled continue skips rows with negative values")
        void sumPositiveRows() {
            int[][] matrix = {
                {1,  2,  3},    // all positive → included (sum 6)
                {4, -1,  6},    // has negative  → skipped
                {7,  8,  9}     // all positive → included (sum 24)
            };
            assertThat(demo.sumPositiveRows(matrix)).isEqualTo(30); // 6 + 24
        }

        @Test
        @DisplayName("sumPositiveRows: all positive rows are summed")
        void sumPositiveRowsAllPositive() {
            int[][] matrix = {{1, 2}, {3, 4}};
            assertThat(demo.sumPositiveRows(matrix)).isEqualTo(10);
        }

        @Test
        @DisplayName("sumPositiveRows: all rows with negatives → sum is 0")
        void sumPositiveRowsAllNegative() {
            int[][] matrix = {{-1, 2}, {3, -4}};
            assertThat(demo.sumPositiveRows(matrix)).isEqualTo(0);
        }

        @ParameterizedTest(name = "findFirst in [5,3,8,3,1] for target={0} → index {1}")
        @CsvSource({"5, 0", "3, 1", "8, 2", "1, 4", "9, -1"})
        @DisplayName("findFirst parameterized — correct index for each target")
        void findFirstParameterized(int target, int expectedIndex) {
            int[] arr = {5, 3, 8, 3, 1};
            assertThat(demo.findFirst(arr, target)).isEqualTo(expectedIndex);
        }
    }
}
