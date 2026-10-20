package com.vbforge.optional_and_null_handling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OptionalUnwrappingTasks — your implementations")
class OptionalUnwrappingTasksTest {

    private OptionalUnwrappingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new OptionalUnwrappingTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement valueOrComputed()")
    @DisplayName("1. valueOrComputed(value, fallback)")
    class ValueOrComputedTest {

        @Test
        @DisplayName("example from Javadoc: present value wins and supplier is never called")
        void presentNeverCallsSupplier() {
            AtomicInteger calls = new AtomicInteger();

            String result = tasks.valueOrComputed(Optional.of("Ann"), () -> {
                calls.incrementAndGet();
                return "guest";
            });

            assertThat(result).isEqualTo("Ann");
            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("example from Javadoc: empty calls the supplier exactly once")
        void emptyCallsSupplierOnce() {
            AtomicInteger calls = new AtomicInteger();

            String result = tasks.valueOrComputed(Optional.<String>empty(), () -> {
                calls.incrementAndGet();
                return "guest";
            });

            assertThat(result).isEqualTo("guest");
            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("an empty-string value is still a value — fallback is not used")
        void emptyStringIsAValue() {
            assertThat(tasks.valueOrComputed(Optional.of(""), () -> "guest")).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement requireValue()")
    @DisplayName("2. requireValue(value, fieldName)")
    class RequireValueTest {

        @Test
        @DisplayName("example from Javadoc: present value is returned")
        void presentValue() {
            assertThat(tasks.requireValue(Optional.of("ann"), "username")).isEqualTo("ann");
        }

        @ParameterizedTest(name = "missing {0} -> \"{1}\"")
        @CsvSource({
                "username, username is required",
                "email,    email is required",
                "age,      age is required"
        })
        @DisplayName("empty throws IllegalArgumentException with the field name in the message")
        void emptyThrows(String fieldName, String expectedMessage) {
            assertThatThrownBy(() -> tasks.requireValue(Optional.<String>empty(), fieldName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedMessage);
        }

        @Test
        @DisplayName("the field name is only used when the value is missing")
        void fieldNameIgnoredWhenPresent() {
            assertThat(tasks.requireValue(Optional.of("x"), "anything")).isEqualTo("x");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement collectIfPresent()")
    @DisplayName("3. collectIfPresent(value, sink)")
    class CollectIfPresentTest {

        @Test
        @DisplayName("example from Javadoc: present value is appended, empty leaves the list alone")
        void javadocExample() {
            List<String> sink = new ArrayList<String>();
            sink.add("a");

            tasks.collectIfPresent(Optional.of("x"), sink);
            tasks.collectIfPresent(Optional.<String>empty(), sink);

            assertThat(sink).containsExactly("a", "x");
        }

        @Test
        @DisplayName("empty Optional leaves an empty list empty")
        void emptyOnEmptySink() {
            List<String> sink = new ArrayList<String>();

            tasks.collectIfPresent(Optional.<String>empty(), sink);

            assertThat(sink).isEmpty();
        }

        @Test
        @DisplayName("repeated calls append in call order")
        void appendsInOrder() {
            List<String> sink = new ArrayList<String>();

            tasks.collectIfPresent(Optional.of("1"), sink);
            tasks.collectIfPresent(Optional.of("2"), sink);
            tasks.collectIfPresent(Optional.of("1"), sink);

            assertThat(sink).containsExactly("1", "2", "1");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstPresent()")
    @DisplayName("4. firstPresent(first, second, third)")
    class FirstPresentTest {

        @Test
        @DisplayName("example from Javadoc: first present wins even if later ones are present too")
        void firstWins() {
            assertThat(tasks.firstPresent(Optional.of("a"), Optional.of("b"), Optional.<String>empty()))
                    .contains("a");
        }

        @Test
        @DisplayName("example from Javadoc: second is used when first is empty")
        void secondUsed() {
            assertThat(tasks.firstPresent(Optional.<String>empty(), Optional.of("b"), Optional.of("c")))
                    .contains("b");
        }

        @Test
        @DisplayName("example from Javadoc: third is used when the first two are empty")
        void thirdUsed() {
            assertThat(tasks.firstPresent(Optional.<String>empty(), Optional.<String>empty(), Optional.of("c")))
                    .contains("c");
        }

        @Test
        @DisplayName("example from Javadoc: all empty gives empty")
        void allEmpty() {
            assertThat(tasks.firstPresent(Optional.<String>empty(), Optional.<String>empty(), Optional.<String>empty()))
                    .isEmpty();
        }

        @Test
        @DisplayName("works with other value types")
        void otherTypes() {
            assertThat(tasks.firstPresent(Optional.<Integer>empty(), Optional.of(7), Optional.of(9))).contains(7);
        }
    }
}
