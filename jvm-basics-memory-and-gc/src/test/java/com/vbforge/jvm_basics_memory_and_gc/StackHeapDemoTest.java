package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StackHeapDemo — API contracts")
class StackHeapDemoTest {

    @Nested
    @DisplayName("1. Pass-by-value")
    class PassByValue {

        @Test
        @DisplayName("incrementPrimitive returns the new value and cannot change the caller's variable")
        void primitiveCopy() {
            int number = 1;
            int result = StackHeapDemo.incrementPrimitive(number);
            assertThat(result).isEqualTo(2);
            assertThat(number).isEqualTo(1);
        }

        @Test
        @DisplayName("mutateArray writes through the copied reference and is visible to the caller")
        void mutationVisible() {
            int[] array = {1, 2, 3};
            StackHeapDemo.mutateArray(array);
            assertThat(array).containsExactly(99, 2, 3);
        }

        @Test
        @DisplayName("reassignArray re-points only the local copy of the reference")
        void reassignmentInvisible() {
            int[] original = {7, 8, 9};
            int[] returned = StackHeapDemo.reassignArray(original);
            assertThat(original).containsExactly(7, 8, 9);
            assertThat(returned).containsExactly(-1);
            assertThat(returned).isNotSameAs(original);
        }
    }

    @Nested
    @DisplayName("2. References, aliasing and identity")
    class Aliasing {

        @Test
        @DisplayName("a mutation through an alias is visible through the original name")
        void aliasSeesMutation() {
            assertThat(StackHeapDemo.aliasAppend()).isEqualTo("xy");
        }

        @Test
        @DisplayName("== compares identity, equals compares content, an alias is identical")
        void identityVsEquality() {
            boolean[] checks = StackHeapDemo.identityChecks();
            assertThat(checks[0]).as("a == b").isFalse();
            assertThat(checks[1]).as("a.equals(b)").isTrue();
            assertThat(checks[2]).as("a == c").isTrue();
        }
    }

    @Nested
    @DisplayName("3. Stack depth")
    class StackDepth {

        @Test
        @DisplayName("recursive and iterative sums agree for small n")
        void sumsAgree() {
            assertThat(StackHeapDemo.sumRecursive(1_000)).isEqualTo(500_500L);
            assertThat(StackHeapDemo.sumIterative(1_000)).isEqualTo(500_500L);
        }

        @Test
        @DisplayName("iterative sum handles n that would overflow the stack and an int")
        void iterativeLarge() {
            assertThat(StackHeapDemo.sumIterative(10_000_000)).isEqualTo(50_000_005_000_000L);
        }

        @Test
        @DisplayName("unbounded recursion ends in StackOverflowError after many frames")
        void overflowIsReached() {
            assertThat(StackHeapDemo.measureStackDepth()).isGreaterThan(1_000);
        }

        @Test
        @DisplayName("very deep recursion throws StackOverflowError")
        void deepRecursionThrows() {
            assertThatThrownBy(() -> StackHeapDemo.sumRecursive(100_000_000))
                    .isInstanceOf(StackOverflowError.class);
        }
    }

    @Nested
    @DisplayName("4. Heap statistics")
    class HeapStats {

        @Test
        @DisplayName("free <= total <= max and all are positive")
        void ordering() {
            long[] stats = StackHeapDemo.heapStats();
            assertThat(stats[2]).isPositive();
            assertThat(stats[2]).isLessThanOrEqualTo(stats[1]);
            assertThat(stats[1]).isLessThanOrEqualTo(stats[0]);
        }
    }

    @Nested
    @DisplayName("5. One stack per thread, one shared heap")
    class ThreadStacks {

        @Test
        @DisplayName("shared counter sums both threads, each local counter stays private")
        void sharedVsLocal() throws InterruptedException {
            int[] result = StackHeapDemo.countInTwoThreads();
            assertThat(result).containsExactly(2000, 1000, 1000);
        }
    }
}
