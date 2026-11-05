package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StackHeapTasks — your implementations")
class StackHeapTasksTest {

    private StackHeapTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new StackHeapTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement swapFirstTwoInPlace()")
    @DisplayName("swapFirstTwoInPlace")
    class SwapFirstTwoInPlaceTest {

        @Test
        @DisplayName("[1, 2, 3] → [2, 1, 3]")
        void example() {
            assertThat(tasks.swapFirstTwoInPlace(new int[]{1, 2, 3})).containsExactly(2, 1, 3);
        }

        @Test
        @DisplayName("returns the very same array instance")
        void sameInstance() {
            int[] array = {1, 2, 3};
            assertThat(tasks.swapFirstTwoInPlace(array)).isSameAs(array);
            assertThat(array).containsExactly(2, 1, 3);
        }

        @Test
        @DisplayName("a single element is unchanged")
        void single() {
            assertThat(tasks.swapFirstTwoInPlace(new int[]{5})).containsExactly(5);
        }

        @Test
        @DisplayName("an empty array is unchanged")
        void empty() {
            assertThat(tasks.swapFirstTwoInPlace(new int[0])).isEmpty();
        }

        @Test
        @DisplayName("exactly two elements are swapped")
        void two() {
            assertThat(tasks.swapFirstTwoInPlace(new int[]{8, 9})).containsExactly(9, 8);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement appendCopy()")
    @DisplayName("appendCopy")
    class AppendCopyTest {

        @Test
        @DisplayName("[1, 2] + 3 → [1, 2, 3]")
        void example() {
            assertThat(tasks.appendCopy(new int[]{1, 2}, 3)).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("the source array is not modified and a new array is returned")
        void sourceUntouched() {
            int[] source = {1, 2};
            int[] result = tasks.appendCopy(source, 3);
            assertThat(result).isNotSameAs(source);
            assertThat(source).containsExactly(1, 2);
        }

        @Test
        @DisplayName("[] + 7 → [7]")
        void empty() {
            assertThat(tasks.appendCopy(new int[0], 7)).containsExactly(7);
        }

        @Test
        @DisplayName("negative values and duplicates are preserved")
        void duplicates() {
            assertThat(tasks.appendCopy(new int[]{-1, -1}, -1)).containsExactly(-1, -1, -1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumIteratively()")
    @DisplayName("sumIteratively")
    class SumIterativelyTest {

        @ParameterizedTest(name = "sumIteratively({0}) = {1}")
        @CsvSource({"0, 0", "1, 1", "4, 10", "100, 5050"})
        @DisplayName("small values")
        void small(int n, long expected) {
            assertThat(tasks.sumIteratively(n)).isEqualTo(expected);
        }

        @Test
        @DisplayName("5_000_000 → 12_500_002_500_000 (needs a long accumulator)")
        void overflowsInt() {
            assertThat(tasks.sumIteratively(5_000_000)).isEqualTo(12_500_002_500_000L);
        }

        @Test
        @DisplayName("Integer.MAX_VALUE does not overflow the stack or the accumulator")
        void huge() {
            assertThat(tasks.sumIteratively(Integer.MAX_VALUE)).isEqualTo(2_305_843_008_139_952_128L);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement overflowsStack()")
    @DisplayName("overflowsStack")
    class OverflowsStackTest {

        private void recurseForever() {
            recurseForever();
        }

        @Test
        @DisplayName("a normal action → false")
        void normal() {
            assertThat(tasks.overflowsStack(() -> { })).isFalse();
        }

        @Test
        @DisplayName("infinite recursion → true")
        void overflow() {
            assertThat(tasks.overflowsStack(this::recurseForever)).isTrue();
        }

        @Test
        @DisplayName("any other exception propagates")
        void otherExceptionPropagates() {
            assertThatThrownBy(() -> tasks.overflowsStack(() -> {
                throw new IllegalStateException("boom");
            })).isInstanceOf(IllegalStateException.class).hasMessage("boom");
        }

        @Test
        @DisplayName("an OutOfMemoryError is not swallowed")
        void otherErrorPropagates() {
            assertThatThrownBy(() -> tasks.overflowsStack(() -> {
                throw new OutOfMemoryError("test");
            })).isInstanceOf(OutOfMemoryError.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement heapHeadroomBytes()")
    @DisplayName("heapHeadroomBytes")
    class HeapHeadroomBytesTest {

        @ParameterizedTest(name = "max={0} total={1} free={2} → {3}")
        @CsvSource({
                "1000, 400, 100, 700",
                "1000, 1000, 0, 0",
                "1000, 200, 200, 1000",
                "4096, 1024, 24, 3096"
        })
        @DisplayName("max - (total - free)")
        void table(long max, long total, long free, long expected) {
            assertThat(tasks.heapHeadroomBytes(max, total, free)).isEqualTo(expected);
        }
    }
}
