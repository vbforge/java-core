package com.vbforge.exception_handling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CheckedVsUncheckedDemo — API contracts")
class CheckedVsUncheckedDemoTest {

    @Nested
    @DisplayName("1. The hierarchy")
    class HierarchyTest {

        @Test
        @DisplayName("Exception subclasses outside RuntimeException are checked")
        void checkedTypes() {
            assertThat(CheckedVsUncheckedDemo.isCheckedDemo(new IOException())).isTrue();
            assertThat(CheckedVsUncheckedDemo.isCheckedDemo(new SQLException())).isTrue();
            assertThat(CheckedVsUncheckedDemo.isCheckedDemo(new Exception())).isTrue();
        }

        @Test
        @DisplayName("RuntimeException and its subclasses are unchecked")
        void uncheckedTypes() {
            assertThat(CheckedVsUncheckedDemo.isCheckedDemo(new RuntimeException())).isFalse();
            assertThat(CheckedVsUncheckedDemo.isCheckedDemo(new IllegalArgumentException())).isFalse();
            assertThat(CheckedVsUncheckedDemo.isCheckedDemo(new NullPointerException())).isFalse();
        }

        @Test
        @DisplayName("Errors are unchecked too")
        void errorsAreUnchecked() {
            assertThat(CheckedVsUncheckedDemo.isCheckedDemo(new StackOverflowError())).isFalse();
            assertThat(CheckedVsUncheckedDemo.isCheckedDemo(new AssertionError())).isFalse();
        }

        @Test
        @DisplayName("IOException is not a RuntimeException — that is what makes it checked")
        void ioExceptionIsNotRuntime() {
            assertThat(RuntimeException.class.isAssignableFrom(IOException.class)).isFalse();
            assertThat(Exception.class.isAssignableFrom(IOException.class)).isTrue();
        }
    }

    @Nested
    @DisplayName("2. Checked: handle or declare")
    class CheckedTest {

        @Test
        @DisplayName("loadDemo returns a value for a valid key")
        void validKey() throws IOException {
            assertThat(CheckedVsUncheckedDemo.loadDemo("a")).isEqualTo("value-of-a");
        }

        @Test
        @DisplayName("loadDemo throws the declared checked IOException for an empty key")
        void emptyKeyThrows() {
            assertThatThrownBy(() -> CheckedVsUncheckedDemo.loadDemo(""))
                    .isInstanceOf(IOException.class)
                    .hasMessage("empty key");
        }

        @Test
        @DisplayName("loadOrDefault handles the exception and returns a default")
        void handled() {
            assertThat(CheckedVsUncheckedDemo.loadOrDefaultDemo("a")).isEqualTo("value-of-a");
            assertThat(CheckedVsUncheckedDemo.loadOrDefaultDemo("")).isEqualTo("default");
            assertThat(CheckedVsUncheckedDemo.loadOrDefaultDemo(null)).isEqualTo("default");
        }
    }

    @Nested
    @DisplayName("3. Unchecked: no declaration required")
    class UncheckedTest {

        @Test
        @DisplayName("a valid argument is returned")
        void validArgument() {
            assertThat(CheckedVsUncheckedDemo.requirePositiveDemo(5)).isEqualTo(5);
        }

        @Test
        @DisplayName("an illegal argument throws IllegalArgumentException with a helpful message")
        void illegalArgument() {
            assertThatThrownBy(() -> CheckedVsUncheckedDemo.requirePositiveDemo(-3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("n must be positive: -3");
        }

        @Test
        @DisplayName("zero is rejected as well")
        void zeroRejected() {
            assertThatThrownBy(() -> CheckedVsUncheckedDemo.requirePositiveDemo(0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("4. Catching a parent type")
    class CatchParentTest {

        @Test
        @DisplayName("a catch for IOException also catches its subclass FileNotFoundException")
        void parentCatchesChild() {
            assertThat(CheckedVsUncheckedDemo.catchParentDemo())
                    .isEqualTo("caught as IOException: FileNotFoundException");
        }

        @Test
        @DisplayName("FileNotFoundException really is an IOException")
        void isASubclass() {
            assertThat(IOException.class.isAssignableFrom(FileNotFoundException.class)).isTrue();
        }
    }

    @Nested
    @DisplayName("5. Converting checked to unchecked")
    class WrapTest {

        @Test
        @DisplayName("a valid key passes through")
        void validKey() {
            assertThat(CheckedVsUncheckedDemo.wrapDemo("k")).isEqualTo("value-of-k");
        }

        @Test
        @DisplayName("the checked IOException is wrapped in an UncheckedIOException")
        void wrapped() {
            assertThatThrownBy(() -> CheckedVsUncheckedDemo.wrapDemo(""))
                    .isInstanceOf(UncheckedIOException.class);
        }

        @Test
        @DisplayName("the original exception is kept as the cause")
        void causeKept() {
            assertThatThrownBy(() -> CheckedVsUncheckedDemo.wrapDemo(""))
                    .hasCauseInstanceOf(IOException.class)
                    .hasStackTraceContaining("empty key");
        }
    }

    @Nested
    @DisplayName("6. Checked exceptions and lambdas")
    class LambdaTest {

        @Test
        @DisplayName("all keys valid: values are mapped in order")
        void allValid() {
            List<String> result = CheckedVsUncheckedDemo.loadAllDemo(Arrays.asList("x", "y"));

            assertThat(result).containsExactly("value-of-x", "value-of-y");
        }

        @Test
        @DisplayName("empty list gives an empty list")
        void emptyList() {
            assertThat(CheckedVsUncheckedDemo.loadAllDemo(Collections.<String>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("one bad key surfaces as UncheckedIOException with the IOException as cause")
        void badKey() {
            assertThatThrownBy(() -> CheckedVsUncheckedDemo.loadAllDemo(Arrays.asList("x", "")))
                    .isInstanceOf(UncheckedIOException.class)
                    .hasCauseInstanceOf(IOException.class);
        }
    }
}
