package com.vbforge.exception_handling;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CheckedVsUncheckedTasks — your implementations")
class CheckedVsUncheckedTasksTest {

    private CheckedVsUncheckedTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CheckedVsUncheckedTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement classify()")
    @DisplayName("1. classify(throwable)")
    class ClassifyTest {

        @Test
        @DisplayName("example from Javadoc: checked exceptions")
        void checked() {
            assertThat(tasks.classify(new IOException())).isEqualTo("checked");
            assertThat(tasks.classify(new Exception())).isEqualTo("checked");
        }

        @Test
        @DisplayName("example from Javadoc: runtime exceptions are unchecked")
        void unchecked() {
            assertThat(tasks.classify(new IllegalArgumentException())).isEqualTo("unchecked");
            assertThat(tasks.classify(new RuntimeException())).isEqualTo("unchecked");
            assertThat(tasks.classify(new NullPointerException())).isEqualTo("unchecked");
        }

        @Test
        @DisplayName("example from Javadoc: errors")
        void errors() {
            assertThat(tasks.classify(new StackOverflowError())).isEqualTo("error");
            assertThat(tasks.classify(new AssertionError())).isEqualTo("error");
        }

        @Test
        @DisplayName("a bare Throwable is neither RuntimeException nor Error, so it is checked")
        void bareThrowable() {
            assertThat(tasks.classify(new Throwable())).isEqualTo("checked");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement readOrDefault()")
    @DisplayName("2. readOrDefault(source, fallback)")
    class ReadOrDefaultTest {

        @Test
        @DisplayName("example from Javadoc: returns the source value when it succeeds")
        void success() {
            assertThat(tasks.readOrDefault(() -> "data", "n/a")).isEqualTo("data");
        }

        @Test
        @DisplayName("example from Javadoc: a checked exception gives the fallback")
        void checkedException() {
            assertThat(tasks.readOrDefault(() -> {
                throw new IOException("disk");
            }, "n/a")).isEqualTo("n/a");
        }

        @Test
        @DisplayName("example from Javadoc: an unchecked exception gives the fallback")
        void uncheckedException() {
            assertThat(tasks.readOrDefault(() -> {
                throw new IllegalStateException("bug");
            }, "n/a")).isEqualTo("n/a");
        }

        @Test
        @DisplayName("example from Javadoc: an Error is NOT swallowed")
        void errorPropagates() {
            assertThatThrownBy(() -> tasks.readOrDefault(() -> {
                throw new AssertionError("fatal");
            }, "n/a")).isInstanceOf(AssertionError.class).hasMessage("fatal");
        }

        @Test
        @DisplayName("a null result is a result, not a failure — the fallback is not used")
        void nullResultIsReturned() {
            assertThat(tasks.readOrDefault(() -> null, "n/a")).isNull();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement loadRequired()")
    @DisplayName("3. loadRequired(source)")
    class LoadRequiredTest {

        @Test
        @DisplayName("example from Javadoc: returns the value on success")
        void success() {
            assertThat(tasks.loadRequired(() -> "x")).isEqualTo("x");
        }

        @Test
        @DisplayName("example from Javadoc: a checked exception is wrapped with message and cause")
        void checkedIsWrapped() {
            IOException io = new IOException("io");

            assertThatThrownBy(() -> tasks.loadRequired(() -> {
                throw io;
            }))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("could not load")
                    .hasCause(io);
        }

        @Test
        @DisplayName("example from Javadoc: an unchecked exception passes through as the same instance")
        void uncheckedPassesThrough() {
            IllegalArgumentException original = new IllegalArgumentException("bad");

            assertThatThrownBy(() -> tasks.loadRequired(() -> {
                throw original;
            })).isSameAs(original);
        }

        @Test
        @DisplayName("a plain Exception counts as checked and is wrapped")
        void plainExceptionWrapped() {
            assertThatThrownBy(() -> tasks.loadRequired(() -> {
                throw new Exception("plain");
            })).isInstanceOf(IllegalStateException.class).hasCauseInstanceOf(Exception.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement collectFailures()")
    @DisplayName("4. collectFailures(inputs, function)")
    class CollectFailuresTest {

        private final ThrowingFunction<String, String> failsOnX = input -> {
            if (input.startsWith("x")) {
                throw new IOException("bad:" + input);
            }
            return input.toUpperCase();
        };

        @Test
        @DisplayName("example from Javadoc: collects messages of failures, in order")
        void collectsInOrder() {
            List<String> failures = tasks.collectFailures(Arrays.asList("a", "xb", "c", "xd"), failsOnX);

            assertThat(failures).containsExactly("bad:xb", "bad:xd");
        }

        @Test
        @DisplayName("example from Javadoc: no failures gives an empty list")
        void noFailures() {
            assertThat(tasks.collectFailures(Arrays.asList("a", "b"), failsOnX)).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: empty input gives an empty list")
        void emptyInput() {
            assertThat(tasks.collectFailures(Collections.<String>emptyList(), failsOnX)).isEmpty();
        }

        @Test
        @DisplayName("unchecked exceptions are collected too")
        void uncheckedCollected() {
            ThrowingFunction<String, String> boom = input -> {
                throw new IllegalStateException("boom:" + input);
            };

            assertThat(tasks.collectFailures(Arrays.asList("p", "q"), boom))
                    .containsExactly("boom:p", "boom:q");
        }

        @Test
        @DisplayName("the first input failing does not stop the others")
        void firstFailureDoesNotStop() {
            assertThat(tasks.collectFailures(Arrays.asList("x1", "x2", "ok", "x3"), failsOnX))
                    .containsExactly("bad:x1", "bad:x2", "bad:x3");
        }
    }
}
