package com.vbforge.enums_and_nested_classes;

import com.vbforge.enums_and_nested_classes.EnumTasks.Day;
import com.vbforge.enums_and_nested_classes.EnumTasks.Level;
import com.vbforge.enums_and_nested_classes.EnumTasks.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EnumTasks — your implementations")
class EnumTasksTest {

    private EnumTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new EnumTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parseLevel()")
    @DisplayName("Task 1 — parseLevel(text)")
    class ParseLevelTest {

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
                "high, HIGH",
                "LOW, LOW",
                "MeDiUm, MEDIUM",
                "'  Medium  ', MEDIUM",
                "'\tHIGH\t', HIGH"
        })
        @DisplayName("matches ignoring case and surrounding whitespace")
        void parametrized(String input, String expectedName) {
            assertThat(tasks.parseLevel(input)).isEqualTo(Level.valueOf(expectedName));
        }

        @Test
        @DisplayName("unknown text falls back to LOW")
        void unknownFallsBack() {
            assertThat(tasks.parseLevel("extreme")).isEqualTo(Level.LOW);
        }

        @Test
        @DisplayName("null and blank fall back to LOW")
        void nullAndBlankFallBack() {
            assertThat(tasks.parseLevel(null)).isEqualTo(Level.LOW);
            assertThat(tasks.parseLevel("")).isEqualTo(Level.LOW);
            assertThat(tasks.parseLevel("   ")).isEqualTo(Level.LOW);
        }

        @Test
        @DisplayName("works under a Turkish default locale (dotted/dotless i)")
        void localeIndependent() {
            Locale original = Locale.getDefault();
            Locale.setDefault(new Locale("tr", "TR"));
            try {
                assertThat(tasks.parseLevel("high")).isEqualTo(Level.HIGH);
                assertThat(tasks.parseLevel("medium")).isEqualTo(Level.MEDIUM);
            } finally {
                Locale.setDefault(original);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement nextLevel()")
    @DisplayName("Task 2 — nextLevel(current)")
    class NextLevelTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "LOW, MEDIUM",
                "MEDIUM, HIGH",
                "HIGH, LOW"
        })
        @DisplayName("moves forward and wraps after the last level")
        void parametrized(String current, String expected) {
            assertThat(tasks.nextLevel(Level.valueOf(current))).isEqualTo(Level.valueOf(expected));
        }

        @Test
        @DisplayName("applying it three times returns to the start")
        void fullCycle() {
            Level level = Level.MEDIUM;
            for (int i = 0; i < Level.values().length; i++) {
                level = tasks.nextLevel(level);
            }
            assertThat(level).isEqualTo(Level.MEDIUM);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countByLevel()")
    @DisplayName("Task 3 — countByLevel(levels)")
    class CountByLevelTest {

        @Test
        @DisplayName("counts occurrences — example from the Javadoc")
        void example() {
            Map<Level, Integer> counts = tasks.countByLevel(Arrays.asList(Level.LOW, Level.HIGH, Level.LOW));
            assertThat(counts).containsEntry(Level.LOW, 2);
            assertThat(counts).containsEntry(Level.MEDIUM, 0);
            assertThat(counts).containsEntry(Level.HIGH, 1);
        }

        @Test
        @DisplayName("empty input yields zero for every level")
        void emptyInput() {
            Map<Level, Integer> counts = tasks.countByLevel(Collections.<Level>emptyList());
            assertThat(counts).hasSize(3);
            assertThat(counts.values()).containsExactly(0, 0, 0);
        }

        @Test
        @DisplayName("result is an EnumMap iterating in declaration order")
        void enumMapInDeclarationOrder() {
            Map<Level, Integer> counts = tasks.countByLevel(Arrays.asList(Level.HIGH, Level.HIGH));
            assertThat(counts).isInstanceOf(EnumMap.class);
            assertThat(counts.keySet()).containsExactly(Level.LOW, Level.MEDIUM, Level.HIGH);
        }

        @Test
        @DisplayName("a single repeated level counts all of them")
        void singleLevelRepeated() {
            Map<Level, Integer> counts = tasks.countByLevel(
                    Arrays.asList(Level.MEDIUM, Level.MEDIUM, Level.MEDIUM, Level.MEDIUM));
            assertThat(counts).containsEntry(Level.MEDIUM, 4);
            assertThat(counts).containsEntry(Level.LOW, 0);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement daysBetween()")
    @DisplayName("Task 4 — daysBetween(from, to)")
    class DaysBetweenTest {

        @Test
        @DisplayName("inclusive range inside the week — example from the Javadoc")
        void example() {
            assertThat(tasks.daysBetween(Day.WEDNESDAY, Day.FRIDAY))
                    .containsExactly(Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY);
        }

        @Test
        @DisplayName("same day returns just that day")
        void sameDay() {
            assertThat(tasks.daysBetween(Day.SUNDAY, Day.SUNDAY)).containsExactly(Day.SUNDAY);
        }

        @Test
        @DisplayName("from after to returns an empty set (no wrap-around)")
        void reversedRangeIsEmpty() {
            assertThat(tasks.daysBetween(Day.FRIDAY, Day.MONDAY)).isEmpty();
        }

        @Test
        @DisplayName("the whole week")
        void wholeWeek() {
            assertThat(tasks.daysBetween(Day.MONDAY, Day.SUNDAY)).hasSize(7);
        }

        @Test
        @DisplayName("result is an EnumSet")
        void isEnumSet() {
            assertThat(tasks.daysBetween(Day.MONDAY, Day.TUESDAY)).isInstanceOf(EnumSet.class);
            assertThat(tasks.daysBetween(Day.TUESDAY, Day.MONDAY)).isInstanceOf(EnumSet.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement Operation.apply()")
    @DisplayName("Task 5 — Operation.apply(a, b)")
    class OperationApplyTest {

        @ParameterizedTest(name = "{0}.apply({1}, {2}) = {3}")
        @CsvSource({
                "ADD, 6, 3, 9",
                "SUBTRACT, 6, 3, 3",
                "MULTIPLY, 6, 3, 18",
                "DIVIDE, 7, 2, 3"
        })
        @DisplayName("each operation computes its result — Javadoc examples")
        void parametrized(String operation, int a, int b, int expected) {
            assertThat(Operation.valueOf(operation).apply(a, b)).isEqualTo(expected);
        }

        @Test
        @DisplayName("operand order matters for SUBTRACT")
        void subtractIsNotCommutative() {
            assertThat(Operation.SUBTRACT.apply(3, 6)).isEqualTo(-3);
        }

        @Test
        @DisplayName("DIVIDE by zero lets ArithmeticException escape")
        void divideByZero() {
            assertThatThrownBy(() -> Operation.DIVIDE.apply(1, 0)).isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("symbols are untouched")
        void symbolsAreUntouched() {
            assertThat(Operation.ADD.symbol()).isEqualTo("+");
            assertThat(Operation.DIVIDE.symbol()).isEqualTo("/");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement operationFromSymbol()")
    @DisplayName("Task 6 — operationFromSymbol(symbol)")
    class OperationFromSymbolTest {

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
                "+, ADD",
                "-, SUBTRACT",
                "*, MULTIPLY",
                "/, DIVIDE"
        })
        @DisplayName("finds the operation that owns the symbol")
        void parametrized(String symbol, String expected) {
            assertThat(tasks.operationFromSymbol(symbol)).isEqualTo(Operation.valueOf(expected));
        }

        @Test
        @DisplayName("an unknown symbol throws IllegalArgumentException")
        void unknownSymbol() {
            assertThatThrownBy(() -> tasks.operationFromSymbol("%")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.operationFromSymbol("")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("null throws IllegalArgumentException")
        void nullSymbol() {
            assertThatThrownBy(() -> tasks.operationFromSymbol(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("every operation can be found through its own symbol")
        void roundTrip() {
            for (Operation operation : Operation.values()) {
                assertThat(tasks.operationFromSymbol(operation.symbol())).isSameAs(operation);
            }
        }
    }
}
