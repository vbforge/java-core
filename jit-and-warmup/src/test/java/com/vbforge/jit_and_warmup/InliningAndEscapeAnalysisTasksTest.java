package com.vbforge.jit_and_warmup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("InliningAndEscapeAnalysisTasks — your implementations")
class InliningAndEscapeAnalysisTasksTest {

    private InliningAndEscapeAnalysisTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new InliningAndEscapeAnalysisTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isInlineCandidate()")
    @DisplayName("isInlineCandidate")
    class IsInlineCandidateTest {

        @ParameterizedTest
        @CsvSource({
                "0, false, true",
                "35, false, true",
                "36, false, false",
                "36, true, true",
                "10, true, true",
                "325, true, true",
                "326, true, false",
                "8000, true, false"
        })
        @DisplayName("Javadoc examples and both boundaries")
        void parametrized(int size, boolean hot, boolean expected) {
            assertThat(tasks.isInlineCandidate(size, hot)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Negative size is rejected")
        void negative() {
            assertThatThrownBy(() -> tasks.isInlineCandidate(-1, true))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement callSiteKind()")
    @DisplayName("callSiteKind")
    class CallSiteKindTest {

        @ParameterizedTest
        @CsvSource({
                "0, UNREACHED",
                "1, MONOMORPHIC",
                "2, BIMORPHIC",
                "3, MEGAMORPHIC",
                "40, MEGAMORPHIC"
        })
        @DisplayName("Javadoc examples")
        void parametrized(int types, String expected) {
            assertThat(tasks.callSiteKind(types)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Largest int is megamorphic")
        void maxValue() {
            assertThat(tasks.callSiteKind(Integer.MAX_VALUE)).isEqualTo("MEGAMORPHIC");
        }

        @Test
        @DisplayName("Negative count is rejected")
        void negative() {
            assertThatThrownBy(() -> tasks.callSiteKind(-1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement escapeState()")
    @DisplayName("escapeState")
    class EscapeStateTest {

        @ParameterizedTest
        @CsvSource({
                "false, false, false, NO_ESCAPE",
                "false, false, true, ARG_ESCAPE",
                "true, false, false, GLOBAL_ESCAPE",
                "false, true, false, GLOBAL_ESCAPE",
                "true, true, true, GLOBAL_ESCAPE"
        })
        @DisplayName("Javadoc examples")
        void parametrized(boolean stored, boolean returned, boolean passed, String expected) {
            assertThat(tasks.escapeState(stored, returned, passed)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Global escape wins over argument escape")
        void globalBeatsArg() {
            assertThat(tasks.escapeState(true, false, true)).isEqualTo("GLOBAL_ESCAPE");
            assertThat(tasks.escapeState(false, true, true)).isEqualTo("GLOBAL_ESCAPE");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement distanceSquaredSum()")
    @DisplayName("distanceSquaredSum")
    class DistanceSquaredSumTest {

        @Test
        @DisplayName("Javadoc example: (0,0) → (3,4) → (3,0)")
        void example() {
            assertThat(tasks.distanceSquaredSum(new int[]{0, 3, 3}, new int[]{0, 4, 0})).isEqualTo(41L);
        }

        @Test
        @DisplayName("One point has no neighbours")
        void singlePoint() {
            assertThat(tasks.distanceSquaredSum(new int[]{5}, new int[]{5})).isEqualTo(0L);
        }

        @Test
        @DisplayName("No points gives 0")
        void empty() {
            assertThat(tasks.distanceSquaredSum(new int[0], new int[0])).isEqualTo(0L);
        }

        @Test
        @DisplayName("Identical neighbours contribute 0")
        void identicalPoints() {
            assertThat(tasks.distanceSquaredSum(new int[]{2, 2, 2}, new int[]{7, 7, 7})).isEqualTo(0L);
        }

        @Test
        @DisplayName("Negative coordinates work")
        void negativeCoordinates() {
            // (-1,-1) → (2,3): dx=3, dy=4 → 25
            assertThat(tasks.distanceSquaredSum(new int[]{-1, 2}, new int[]{-1, 3})).isEqualTo(25L);
        }

        @Test
        @DisplayName("Large coordinates do not overflow int")
        void noOverflow() {
            // dx = 100_000 → 10_000_000_000 does not fit in an int
            assertThat(tasks.distanceSquaredSum(new int[]{0, 100_000}, new int[]{0, 0}))
                    .isEqualTo(10_000_000_000L);
        }

        @Test
        @DisplayName("Mismatched array lengths are rejected")
        void mismatch() {
            assertThatThrownBy(() -> tasks.distanceSquaredSum(new int[]{1, 2}, new int[]{1}))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
