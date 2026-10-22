package com.vbforge.exception_handling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TryCatchFinallyDemo — API contracts")
class TryCatchFinallyDemoTest {

    @Nested
    @DisplayName("1. try / catch basics")
    class TryCatchTest {

        @Test
        @DisplayName("valid text is parsed")
        void validText() {
            assertThat(TryCatchFinallyDemo.parseOrDefaultDemo("42", 0)).isEqualTo(42);
        }

        @Test
        @DisplayName("invalid text gives the default instead of an exception")
        void invalidText() {
            assertThat(TryCatchFinallyDemo.parseOrDefaultDemo("abc", 7)).isEqualTo(7);
        }

        @Test
        @DisplayName("null text is handled too (parseInt reports it as NumberFormatException)")
        void nullText() {
            assertThat(TryCatchFinallyDemo.parseOrDefaultDemo(null, -1)).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("2. Multi-catch")
    class MultiCatchTest {

        @Test
        @DisplayName("a numeric String is parsed")
        void numericString() {
            assertThat(TryCatchFinallyDemo.toIntOrMinusOneDemo("12")).isEqualTo(12);
        }

        @Test
        @DisplayName("a non-numeric String is handled (NumberFormatException)")
        void nonNumericString() {
            assertThat(TryCatchFinallyDemo.toIntOrMinusOneDemo("x")).isEqualTo(-1);
        }

        @Test
        @DisplayName("a non-String object is handled (ClassCastException)")
        void nonString() {
            assertThat(TryCatchFinallyDemo.toIntOrMinusOneDemo(42)).isEqualTo(-1);
        }

        @Test
        @DisplayName("null is handled (NumberFormatException)")
        void nullValue() {
            assertThat(TryCatchFinallyDemo.toIntOrMinusOneDemo(null)).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("3. Catch order")
    class CatchOrderTest {

        @Test
        @DisplayName("no exception gives \"none\"")
        void noException() {
            assertThat(TryCatchFinallyDemo.classifyDemo(() -> { })).isEqualTo("none");
        }

        @Test
        @DisplayName("the specific ArithmeticException block wins")
        void specificWins() {
            int zero = 0;

            assertThat(TryCatchFinallyDemo.classifyDemo(() -> {
                int result = 1 / zero;
                assertThat(result).isNotNegative();
            })).isEqualTo("arithmetic");
        }

        @Test
        @DisplayName("other RuntimeExceptions fall through to the general block")
        void generalCatches() {
            assertThat(TryCatchFinallyDemo.classifyDemo(() -> {
                throw new IllegalStateException();
            })).isEqualTo("runtime");
        }

        @Test
        @DisplayName("an Error is not a RuntimeException and escapes both blocks")
        void errorEscapes() {
            assertThatThrownBy(() -> TryCatchFinallyDemo.classifyDemo(() -> {
                throw new AssertionError("not caught");
            })).isInstanceOf(AssertionError.class);
        }
    }

    @Nested
    @DisplayName("4. finally always runs")
    class FinallyTest {

        @Test
        @DisplayName("without an exception: try, try-end, finally (no catch)")
        void noException() {
            List<String> log = new ArrayList<String>();

            TryCatchFinallyDemo.finallyOrderDemo(false, log);

            assertThat(log).containsExactly("try", "try-end", "finally");
        }

        @Test
        @DisplayName("with an exception: try, catch, finally — in that order")
        void withException() {
            List<String> log = new ArrayList<String>();

            TryCatchFinallyDemo.finallyOrderDemo(true, log);

            assertThat(log).containsExactly("try", "catch", "finally");
        }

        @Test
        @DisplayName("finally runs even though the try block returned")
        void finallyAfterReturn() {
            List<String> log = new ArrayList<String>();

            String result = TryCatchFinallyDemo.finallyAfterReturnDemo(log);

            assertThat(result).isEqualTo("from-try");
            assertThat(log).containsExactly("try", "finally");
        }
    }

    @Nested
    @DisplayName("5. finally gotchas")
    class FinallyGotchasTest {

        @Test
        @DisplayName("changing the variable in finally does not change the returned value")
        void returnedValueIsFixed() {
            assertThat(TryCatchFinallyDemo.finallyDoesNotChangeReturnedValueDemo()).isEqualTo(1);
        }

        @Test
        @DisplayName("return in finally discards the exception that was thrown")
        void returnSwallowsException() {
            assertThat(TryCatchFinallyDemo.returnInFinallyDemo()).isEqualTo("finally wins");
        }

        @Test
        @DisplayName("an exception thrown in finally replaces the original one")
        void finallyExceptionReplacesOriginal() {
            assertThatThrownBy(TryCatchFinallyDemo::finallyThrowsDemo)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("from finally")
                    .hasNoSuppressedExceptions();
        }
    }
}
