package com.vbforge.class_loading_and_jvm_architecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("MemoryAreasTasks — your implementations")
class MemoryAreasTasksTest {

    private MemoryAreasTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new MemoryAreasTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement currentCallStack()")
    @DisplayName("currentCallStack")
    class CurrentCallStackTest {

        private List<String> levelTwo() {
            return tasks.currentCallStack();
        }

        private List<String> levelOne() {
            return levelTwo();
        }

        @Test
        @DisplayName("Lists the calling methods innermost first")
        void innermostFirst() {
            assertThat(levelOne()).startsWith("levelTwo", "levelOne", "innermostFirst");
        }

        @Test
        @DisplayName("Starts with the direct caller when called straight from the test")
        void directCaller() {
            assertThat(tasks.currentCallStack()).startsWith("directCaller");
        }

        @Test
        @DisplayName("Does not include itself or the JDK method used to read the stack")
        void excludesOwnFrames() {
            assertThat(tasks.currentCallStack()).doesNotContain("currentCallStack", "getStackTrace");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement recursionDepth()")
    @DisplayName("recursionDepth")
    class RecursionDepthTest {

        @ParameterizedTest(name = "limit {0} → depth {1}")
        @CsvSource({"0,0", "1,1", "10,10", "500,500"})
        @DisplayName("Reaches the limit when it fits on the stack")
        void reachesLimit(int limit, int expected) {
            assertThat(tasks.recursionDepth(limit)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Stops at a StackOverflowError when the limit is huge")
        void stopsAtOverflow() {
            int depth = tasks.recursionDepth(Integer.MAX_VALUE);

            assertThat(depth).isGreaterThan(100).isLessThan(Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("Can be called again after an overflow")
        void repeatable() {
            tasks.recursionDepth(Integer.MAX_VALUE);

            assertThat(tasks.recursionDepth(25)).isEqualTo(25);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement swapContents()")
    @DisplayName("swapContents")
    class SwapContentsTest {

        @Test
        @DisplayName("Swaps the text of two builders")
        void swaps() {
            StringBuilder a = new StringBuilder("left");
            StringBuilder b = new StringBuilder("right");

            tasks.swapContents(a, b);

            assertThat(a.toString()).isEqualTo("right");
            assertThat(b.toString()).isEqualTo("left");
        }

        @Test
        @DisplayName("Mutates the same objects the caller passed in")
        void sameObjects() {
            StringBuilder a = new StringBuilder("x");
            StringBuilder b = new StringBuilder("y");
            StringBuilder originalA = a;
            StringBuilder originalB = b;

            tasks.swapContents(a, b);

            assertThat((Object) a).isSameAs(originalA);
            assertThat((Object) b).isSameAs(originalB);
        }

        @Test
        @DisplayName("Works when one builder is empty")
        void emptyBuilder() {
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder("abc");

            tasks.swapContents(a, b);

            assertThat(a.toString()).isEqualTo("abc");
            assertThat(b.toString()).isEmpty();
        }

        @Test
        @DisplayName("Works with different lengths")
        void differentLengths() {
            StringBuilder a = new StringBuilder("a");
            StringBuilder b = new StringBuilder("much longer text");

            tasks.swapContents(a, b);

            assertThat(a.toString()).isEqualTo("much longer text");
            assertThat(b.toString()).isEqualTo("a");
        }

        @Test
        @DisplayName("Leaves identical contents unchanged")
        void identicalContents() {
            StringBuilder a = new StringBuilder("same");
            StringBuilder b = new StringBuilder("same");

            tasks.swapContents(a, b);

            assertThat(a.toString()).isEqualTo("same");
            assertThat(b.toString()).isEqualTo("same");
        }
    }
}
