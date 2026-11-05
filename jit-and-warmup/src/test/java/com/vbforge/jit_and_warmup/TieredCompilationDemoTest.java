package com.vbforge.jit_and_warmup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TieredCompilationDemo — API contracts")
class TieredCompilationDemoTest {

    @Nested
    @DisplayName("1. The five tiers")
    class Tiers {

        @Test
        @DisplayName("Tier 0 is the interpreter")
        void tierZero() {
            assertThat(TieredCompilationDemo.describeTier(0)).contains("Interpreter");
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        @DisplayName("Tiers 1 to 3 are C1 tiers")
        void c1Tiers(int level) {
            assertThat(TieredCompilationDemo.describeTier(level)).startsWith("C1");
        }

        @Test
        @DisplayName("Only tier 3 does full profiling")
        void onlyTierThreeFullProfiling() {
            assertThat(TieredCompilationDemo.describeTier(3)).contains("full profiling");
            assertThat(TieredCompilationDemo.describeTier(1)).doesNotContain("full profiling");
            assertThat(TieredCompilationDemo.describeTier(2)).doesNotContain("full profiling");
        }

        @Test
        @DisplayName("Tier 4 is C2")
        void tierFour() {
            assertThat(TieredCompilationDemo.describeTier(4)).startsWith("C2");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 5, 100})
        @DisplayName("Levels outside 0..4 are rejected")
        void invalidLevels(int level) {
            assertThatThrownBy(() -> TieredCompilationDemo.describeTier(level))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("2. The warm-up curve")
    class WarmupCurve {

        @ParameterizedTest
        @CsvSource({"0, 0", "1, 0", "2, 1", "3, 33", "4, 1026", "5, 31810"})
        @DisplayName("checksum gives the documented values")
        void checksumKnownValues(int n, long expected) {
            assertThat(TieredCompilationDemo.checksum(n)).isEqualTo(expected);
        }

        @Test
        @DisplayName("checksum is deterministic, so warm and cold runs agree")
        void checksumDeterministic() {
            long cold = TieredCompilationDemo.checksum(1_000);
            for (int i = 0; i < 20_000; i++) {
                TieredCompilationDemo.checksum(1_000);
            }
            long warm = TieredCompilationDemo.checksum(1_000);
            assertThat(warm).isEqualTo(cold);
        }

        @Test
        @DisplayName("measureBatches returns one non-negative timing per batch")
        void measureBatchesShape() {
            long[] nanos = TieredCompilationDemo.measureBatches(4, 10, 100);
            assertThat(nanos).hasSize(4);
            for (long n : nanos) {
                assertThat(n).isGreaterThanOrEqualTo(0L);
            }
        }

        @Test
        @DisplayName("measureBatches rejects invalid arguments")
        void measureBatchesInvalid() {
            assertThatThrownBy(() -> TieredCompilationDemo.measureBatches(0, 10, 100))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> TieredCompilationDemo.measureBatches(3, 0, 100))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> TieredCompilationDemo.measureBatches(3, 10, -1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("3. On-stack replacement")
    class OnStackReplacement {

        @ParameterizedTest
        @CsvSource({"0, 0", "1, 0", "10, 45", "100000, 4999950000"})
        @DisplayName("singleCallHotLoop sums 0..n-1")
        void sums(long n, long expected) {
            assertThat(TieredCompilationDemo.singleCallHotLoop(n)).isEqualTo(expected);
        }

        @Test
        @DisplayName("A non-positive iteration count gives 0")
        void nonPositive() {
            assertThat(TieredCompilationDemo.singleCallHotLoop(-5)).isEqualTo(0L);
        }

        @Test
        @DisplayName("Result does not depend on whether the loop was compiled mid-run")
        void sameResultWhateverTheTier() {
            long once = TieredCompilationDemo.singleCallHotLoop(3_000_000L);
            assertThat(once).isEqualTo(3_000_000L * 2_999_999L / 2);
        }
    }

    @Nested
    @DisplayName("4. Looking at the JIT from the outside")
    class Introspection {

        @Test
        @DisplayName("jitName is never blank")
        void jitNameNotBlank() {
            assertThat(TieredCompilationDemo.jitName()).isNotBlank();
        }

        @Test
        @DisplayName("The cheat sheet lists the key flags")
        void cheatSheetKeys() {
            Map<String, String> flags = TieredCompilationDemo.jitFlagCheatSheet();
            assertThat(flags).containsKeys("-Xint", "-Xcomp", "-XX:TieredStopAtLevel=1",
                    "-XX:-TieredCompilation", "-XX:+PrintCompilation");
        }

        @Test
        @DisplayName("Every flag has an explanation")
        void cheatSheetValues() {
            for (String description : TieredCompilationDemo.jitFlagCheatSheet().values()) {
                assertThat(description).isNotBlank();
            }
        }

        @Test
        @DisplayName("Cheat sheet keeps its insertion order")
        void cheatSheetOrder() {
            String firstFlag = TieredCompilationDemo.jitFlagCheatSheet().keySet().iterator().next();
            assertThat(firstFlag).isEqualTo("-Xint");
        }
    }
}
