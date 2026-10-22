package com.vbforge.exception_handling;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TryCatchFinallyTasks — your implementations")
class TryCatchFinallyTasksTest {

    private TryCatchFinallyTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TryCatchFinallyTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement safeDivide()")
    @DisplayName("1. safeDivide(a, b, fallback)")
    class SafeDivideTest {

        @ParameterizedTest(name = "{0} / {1} = {2}")
        @CsvSource({
                "10, 2, 5",
                "7,  2, 3",
                "0,  5, 0",
                "-9, 3, -3"
        })
        @DisplayName("example from Javadoc: normal division")
        void normalDivision(int a, int b, int expected) {
            assertThat(tasks.safeDivide(a, b, -1)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example from Javadoc: division by zero returns the fallback")
        void divisionByZero() {
            assertThat(tasks.safeDivide(10, 0, -1)).isEqualTo(-1);
            assertThat(tasks.safeDivide(0, 0, 99)).isEqualTo(99);
        }

        @Test
        @DisplayName("fallback is not used when the division works")
        void fallbackNotUsed() {
            assertThat(tasks.safeDivide(8, 2, 1234)).isEqualTo(4);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumValidNumbers()")
    @DisplayName("2. sumValidNumbers(csv)")
    class SumValidNumbersTest {

        @Test
        @DisplayName("example from Javadoc: all valid")
        void allValid() {
            assertThat(tasks.sumValidNumbers("1,2,3")).isEqualTo(6);
        }

        @Test
        @DisplayName("example from Javadoc: invalid tokens are skipped, processing continues")
        void invalidSkipped() {
            assertThat(tasks.sumValidNumbers("1, x, 3")).isEqualTo(4);
        }

        @Test
        @DisplayName("example from Javadoc: nothing valid gives 0")
        void nothingValid() {
            assertThat(tasks.sumValidNumbers("a,b")).isEqualTo(0);
        }

        @Test
        @DisplayName("example from Javadoc: empty text gives 0")
        void emptyText() {
            assertThat(tasks.sumValidNumbers("")).isEqualTo(0);
        }

        @Test
        @DisplayName("example from Javadoc: null gives 0")
        void nullText() {
            assertThat(tasks.sumValidNumbers(null)).isEqualTo(0);
        }

        @Test
        @DisplayName("negative numbers and surrounding whitespace are handled")
        void negativesAndWhitespace() {
            assertThat(tasks.sumValidNumbers(" -5 , 10 ,oops, 2")).isEqualTo(7);
        }

        @Test
        @DisplayName("a bad token at the start does not stop the later ones")
        void badTokenFirst() {
            assertThat(tasks.sumValidNumbers("bad,4,5")).isEqualTo(9);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstOrNone()")
    @DisplayName("3. firstOrNone(numbers)")
    class FirstOrNoneTest {

        @Test
        @DisplayName("example from Javadoc: returns the first element as text")
        void firstElement() {
            assertThat(tasks.firstOrNone(new int[]{7, 8})).isEqualTo("7");
        }

        @Test
        @DisplayName("example from Javadoc: empty array gives \"none\"")
        void emptyArray() {
            assertThat(tasks.firstOrNone(new int[]{})).isEqualTo("none");
        }

        @Test
        @DisplayName("example from Javadoc: null array gives \"none\"")
        void nullArray() {
            assertThat(tasks.firstOrNone(null)).isEqualTo("none");
        }

        @Test
        @DisplayName("zero and negative first elements are real values")
        void zeroAndNegative() {
            assertThat(tasks.firstOrNone(new int[]{0})).isEqualTo("0");
            assertThat(tasks.firstOrNone(new int[]{-4, 1})).isEqualTo("-4");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement retry()")
    @DisplayName("4. retry(action, maxAttempts)")
    class RetryTest {

        @Test
        @DisplayName("example from Javadoc: first attempt succeeds, action called once")
        void firstAttemptSucceeds() {
            AtomicInteger calls = new AtomicInteger();

            int result = tasks.retry(() -> {
                calls.incrementAndGet();
                return 5;
            }, 3);

            assertThat(result).isEqualTo(5);
            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("example from Javadoc: succeeds on the third attempt")
        void thirdAttemptSucceeds() {
            AtomicInteger calls = new AtomicInteger();

            int result = tasks.retry(() -> {
                if (calls.incrementAndGet() < 3) {
                    throw new IllegalStateException("fail " + calls.get());
                }
                return 9;
            }, 3);

            assertThat(result).isEqualTo(9);
            assertThat(calls.get()).isEqualTo(3);
        }

        @Test
        @DisplayName("example from Javadoc: all attempts fail, the LAST exception is rethrown")
        void allFail() {
            AtomicInteger calls = new AtomicInteger();

            assertThatThrownBy(() -> tasks.retry(() -> {
                throw new IllegalStateException("fail " + calls.incrementAndGet());
            }, 3))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("fail 3");
            assertThat(calls.get()).isEqualTo(3);
        }

        @Test
        @DisplayName("maxAttempts of 1 means exactly one try")
        void singleAttempt() {
            AtomicInteger calls = new AtomicInteger();

            assertThatThrownBy(() -> tasks.retry(() -> {
                calls.incrementAndGet();
                throw new IllegalStateException("once");
            }, 1)).hasMessage("once");
            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("example from Javadoc: maxAttempts below 1 is rejected before any call")
        void invalidMaxAttempts() {
            AtomicInteger calls = new AtomicInteger();

            assertThatThrownBy(() -> tasks.retry(() -> {
                calls.incrementAndGet();
                return 1;
            }, 0)).isInstanceOf(IllegalArgumentException.class);
            assertThat(calls.get()).isZero();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runWithCleanup()")
    @DisplayName("5. runWithCleanup(action, log)")
    class RunWithCleanupTest {

        @Test
        @DisplayName("example from Javadoc: success returns the result and logs cleanup once")
        void success() {
            List<String> log = new ArrayList<String>();

            String result = tasks.runWithCleanup(() -> "ok", log);

            assertThat(result).isEqualTo("ok");
            assertThat(log).containsExactly("cleanup");
        }

        @Test
        @DisplayName("example from Javadoc: failure is turned into an error text, cleanup still runs")
        void failure() {
            List<String> log = new ArrayList<String>();

            String result = tasks.runWithCleanup(() -> {
                throw new IllegalStateException("bad");
            }, log);

            assertThat(result).isEqualTo("error:bad");
            assertThat(log).containsExactly("cleanup");
        }

        @Test
        @DisplayName("cleanup happens AFTER the action has finished")
        void cleanupComesAfterAction() {
            List<String> log = new ArrayList<String>();

            tasks.runWithCleanup(() -> {
                log.add("action");
                return "x";
            }, log);

            assertThat(log).containsExactly("action", "cleanup");
        }
    }
}
