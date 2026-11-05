package com.vbforge.class_loading_and_jvm_architecture;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MemoryAreasDemo — API contracts")
class MemoryAreasDemoTest {

    @Nested
    @DisplayName("1. Stacks and frames")
    class StacksTest {

        @Test
        @DisplayName("The call stack lists frames innermost first")
        void innermostFirst() {
            assertThat(MemoryAreasDemo.callStackNames())
                    .startsWith("levelThree", "levelTwo", "levelOne", "callStackNames");
        }

        @Test
        @DisplayName("The caller of the demo method is further down the stack")
        void callerBelowDemo() {
            assertThat(MemoryAreasDemo.callStackNames()).contains("callerBelowDemo");
        }

        @Test
        @DisplayName("Unbounded recursion ends in a catchable StackOverflowError after many frames")
        void overflowDepth() {
            assertThat(MemoryAreasDemo.stackOverflowDepth()).isGreaterThan(100);
        }

        @Test
        @DisplayName("The JVM stays usable after a StackOverflowError, so the demo can run twice")
        void overflowRepeatable() {
            assertThat(MemoryAreasDemo.stackOverflowDepth()).isGreaterThan(100);
            assertThat(MemoryAreasDemo.stackOverflowDepth()).isGreaterThan(100);
        }
    }

    @Nested
    @DisplayName("2. Stack vs heap")
    class StackVersusHeapTest {

        @Test
        @DisplayName("A primitive argument is copied: the caller's variable does not change")
        void primitiveCopied() {
            int number = 1;

            int result = MemoryAreasDemo.incrementPrimitive(number);

            assertThat(result).isEqualTo(2);
            assertThat(number).isEqualTo(1);
        }

        @Test
        @DisplayName("An object argument shares the heap object: mutations are visible to the caller")
        void objectMutated() {
            StringBuilder text = new StringBuilder("hello");

            MemoryAreasDemo.appendWorld(text);

            assertThat(text.toString()).isEqualTo("hello world");
        }

        @Test
        @DisplayName("Reassigning the parameter inside the method does not affect the caller")
        void reassignmentInvisible() {
            StringBuilder text = new StringBuilder("hello");
            StringBuilder sameReference = text;

            MemoryAreasDemo.reassignParameter(text);

            assertThat((Object) text).isSameAs(sameReference);
            assertThat(text.toString()).isEqualTo("hello");
        }
    }

    @Nested
    @DisplayName("3. Heap")
    class HeapTest {

        @Test
        @DisplayName("Committed heap never exceeds the maximum")
        void limitsConsistent() {
            assertThat(MemoryAreasDemo.heapLimitsAreConsistent()).isTrue();
        }

        @Test
        @DisplayName("The maximum heap is positive")
        void maxPositive() {
            assertThat(Runtime.getRuntime().maxMemory()).isPositive();
        }

        @Test
        @DisplayName("An array of Integer.MAX_VALUE bytes throws OutOfMemoryError")
        void oversizedArray() {
            assertThat(MemoryAreasDemo.oversizedArrayThrowsOutOfMemory()).isTrue();
        }

        @Test
        @DisplayName("String pool: literal != new String, but equals, and intern() returns the literal")
        void stringPool() {
            assertThat(MemoryAreasDemo.stringPoolIdentity()).containsExactly(false, true, true);
        }
    }

    @Nested
    @DisplayName("4. Class metadata and statics")
    class ClassMetadataTest {

        @Test
        @DisplayName("Instance fields are per object, the static field is shared by the class")
        void staticVersusInstance() {
            assertThat(MemoryAreasDemo.staticVersusInstanceFields()).containsExactly(2, 1, 3);
        }

        @Test
        @DisplayName("The static counter is reset on each run, so results are repeatable")
        void repeatable() {
            assertThat(MemoryAreasDemo.staticVersusInstanceFields())
                    .isEqualTo(MemoryAreasDemo.staticVersusInstanceFields());
        }

        @Test
        @DisplayName("Defining a class increases the JVM's loaded-class count")
        void loadedClassCountGrows() throws Exception {
            assertThat(MemoryAreasDemo.classesLoadedByDefiningOneClass()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Heap and non-heap (Metaspace, code cache) both report used bytes")
        void usedBytes() {
            long[] used = MemoryAreasDemo.heapAndNonHeapUsedBytes();

            assertThat(used[0]).isPositive();
            assertThat(used[1]).isPositive();
        }
    }
}
