package com.vbforge.jit_and_warmup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InliningAndEscapeAnalysisDemo — API contracts")
class InliningAndEscapeAnalysisDemoTest {

    @Nested
    @DisplayName("1. Inlining basics")
    class InliningBasics {

        @ParameterizedTest
        @CsvSource({"0, 0", "1, 0", "4, 12", "1000, 999000"})
        @DisplayName("Small methods sum twice(i) for i in 0..n-1")
        void sumWithSmallMethods(int n, long expected) {
            assertThat(InliningAndEscapeAnalysisDemo.sumWithSmallMethods(n)).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({"0", "1", "4", "1000", "250000"})
        @DisplayName("Hand-inlined version returns exactly the same result")
        void inliningNeverChangesBehaviour(int n) {
            assertThat(InliningAndEscapeAnalysisDemo.sumInlinedByHand(n))
                    .isEqualTo(InliningAndEscapeAnalysisDemo.sumWithSmallMethods(n));
        }
    }

    @Nested
    @DisplayName("2. Inlining thresholds")
    class InliningThresholds {

        @Test
        @DisplayName("Default HotSpot limits are 35, 325 and 8000 bytecode bytes")
        void defaultLimits() {
            assertThat(InliningAndEscapeAnalysisDemo.MAX_INLINE_SIZE).isEqualTo(35);
            assertThat(InliningAndEscapeAnalysisDemo.FREQ_INLINE_SIZE).isEqualTo(325);
            assertThat(InliningAndEscapeAnalysisDemo.HUGE_METHOD_LIMIT).isEqualTo(8000);
        }

        @Test
        @DisplayName("Limits are ordered: always < hot < never compiled")
        void limitsAreOrdered() {
            assertThat(InliningAndEscapeAnalysisDemo.MAX_INLINE_SIZE)
                    .isLessThan(InliningAndEscapeAnalysisDemo.FREQ_INLINE_SIZE);
            assertThat(InliningAndEscapeAnalysisDemo.FREQ_INLINE_SIZE)
                    .isLessThan(InliningAndEscapeAnalysisDemo.HUGE_METHOD_LIMIT);
        }
    }

    @Nested
    @DisplayName("3. Monomorphic, bimorphic and megamorphic call sites")
    class CallSiteShapes {

        @Test
        @DisplayName("monomorphicOps uses one class")
        void monomorphicTypes() {
            assertThat(InliningAndEscapeAnalysisDemo.distinctTypes(
                    InliningAndEscapeAnalysisDemo.monomorphicOps(10))).isEqualTo(1);
        }

        @Test
        @DisplayName("bimorphicOps uses two classes")
        void bimorphicTypes() {
            assertThat(InliningAndEscapeAnalysisDemo.distinctTypes(
                    InliningAndEscapeAnalysisDemo.bimorphicOps(10))).isEqualTo(2);
        }

        @Test
        @DisplayName("megamorphicOps uses four classes")
        void megamorphicTypes() {
            assertThat(InliningAndEscapeAnalysisDemo.distinctTypes(
                    InliningAndEscapeAnalysisDemo.megamorphicOps(10))).isEqualTo(4);
        }

        @Test
        @DisplayName("Arrays have the requested length")
        void lengths() {
            assertThat(InliningAndEscapeAnalysisDemo.monomorphicOps(7)).hasSize(7);
            assertThat(InliningAndEscapeAnalysisDemo.bimorphicOps(0)).isEmpty();
        }

        @Test
        @DisplayName("applyAll over a monomorphic array adds 1 to each index")
        void applyAllMono() {
            // (0+1) + (1+1) + (2+1)
            assertThat(InliningAndEscapeAnalysisDemo.applyAll(
                    InliningAndEscapeAnalysisDemo.monomorphicOps(3))).isEqualTo(6L);
        }

        @Test
        @DisplayName("applyAll over a bimorphic array alternates inc and double")
        void applyAllBi() {
            // inc(0)=1, dbl(1)=2, inc(2)=3, dbl(3)=6
            assertThat(InliningAndEscapeAnalysisDemo.applyAll(
                    InliningAndEscapeAnalysisDemo.bimorphicOps(4))).isEqualTo(12L);
        }

        @Test
        @DisplayName("applyAll over a megamorphic array cycles four operations")
        void applyAllMega() {
            // inc(0)=1, dbl(1)=2, neg(2)=-2, sq(3)=9
            assertThat(InliningAndEscapeAnalysisDemo.applyAll(
                    InliningAndEscapeAnalysisDemo.megamorphicOps(4))).isEqualTo(10L);
        }

        @Test
        @DisplayName("applyAll on an empty array is 0")
        void applyAllEmpty() {
            assertThat(InliningAndEscapeAnalysisDemo.applyAll(
                    InliningAndEscapeAnalysisDemo.megamorphicOps(0))).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("4. Escape analysis and scalar replacement")
    class EscapeAnalysis {

        @Test
        @DisplayName("Allocating and allocation-free distances agree")
        void sameDistance() {
            assertThat(InliningAndEscapeAnalysisDemo.distanceSquaredAllocating(3, 4, 0, 0)).isEqualTo(25L);
            assertThat(InliningAndEscapeAnalysisDemo.distanceSquaredByHand(3, 4, 0, 0)).isEqualTo(25L);
            assertThat(InliningAndEscapeAnalysisDemo.distanceSquaredAllocating(-2, 5, 4, -3))
                    .isEqualTo(InliningAndEscapeAnalysisDemo.distanceSquaredByHand(-2, 5, 4, -3));
        }

        @Test
        @DisplayName("Squares are computed in long, so large coordinates do not overflow int")
        void noIntOverflow() {
            assertThat(InliningAndEscapeAnalysisDemo.distanceSquaredByHand(100_000, 0, 0, 0))
                    .isEqualTo(10_000_000_000L);
            assertThat(InliningAndEscapeAnalysisDemo.distanceSquaredAllocating(100_000, 0, 0, 0))
                    .isEqualTo(10_000_000_000L);
        }

        @Test
        @DisplayName("sumDistances adds the squared length of (i, i+1)")
        void sumDistances() {
            // 0+1, 1+4, 4+9
            assertThat(InliningAndEscapeAnalysisDemo.sumDistances(3)).isEqualTo(19L);
            assertThat(InliningAndEscapeAnalysisDemo.sumDistances(0)).isEqualTo(0L);
        }

        @Test
        @DisplayName("A point stored in a static field is the very same object that was returned")
        void globalEscape() {
            InliningAndEscapeAnalysisDemo.Point returned = InliningAndEscapeAnalysisDemo.storeGlobally(3, 4);
            assertThat(InliningAndEscapeAnalysisDemo.escapedPoint()).isSameAs(returned);
            assertThat(returned.lengthSquared()).isEqualTo(25L);
        }

        @Test
        @DisplayName("Each storeGlobally call replaces the escaped point")
        void escapedPointReplaced() {
            InliningAndEscapeAnalysisDemo.Point first = InliningAndEscapeAnalysisDemo.storeGlobally(1, 1);
            InliningAndEscapeAnalysisDemo.Point second = InliningAndEscapeAnalysisDemo.storeGlobally(2, 2);
            assertThat(InliningAndEscapeAnalysisDemo.escapedPoint()).isSameAs(second).isNotSameAs(first);
        }

        @Test
        @DisplayName("Point.minus returns a new object")
        void minusAllocates() {
            InliningAndEscapeAnalysisDemo.Point a = new InliningAndEscapeAnalysisDemo.Point(5, 7);
            InliningAndEscapeAnalysisDemo.Point b = new InliningAndEscapeAnalysisDemo.Point(2, 3);
            InliningAndEscapeAnalysisDemo.Point diff = a.minus(b);
            assertThat(diff).isNotSameAs(a).isNotSameAs(b);
            assertThat(diff.x).isEqualTo(3);
            assertThat(diff.y).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("5. Lock elision")
    class LockElision {

        @Test
        @DisplayName("Local StringBuffer joins with commas")
        void joins() {
            assertThat(InliningAndEscapeAnalysisDemo.joinWithLocalBuffer(new String[]{"a", "b", "c"}))
                    .isEqualTo("a,b,c");
        }

        @Test
        @DisplayName("Single part has no separator")
        void single() {
            assertThat(InliningAndEscapeAnalysisDemo.joinWithLocalBuffer(new String[]{"only"}))
                    .isEqualTo("only");
        }

        @Test
        @DisplayName("No parts gives an empty string")
        void empty() {
            assertThat(InliningAndEscapeAnalysisDemo.joinWithLocalBuffer(new String[0])).isEmpty();
        }
    }
}
