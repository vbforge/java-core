package com.vbforge.optional_and_null_handling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OptionalCreationDemo — API contracts")
class OptionalCreationDemoTest {

    @Nested
    @DisplayName("1. Optional.empty()")
    class EmptyTest {

        @Test
        @DisplayName("empty() holds no value")
        void holdsNothing() {
            Optional<String> opt = OptionalCreationDemo.emptyDemo();

            assertThat(opt).isEmpty();
            assertThat(opt.isPresent()).isFalse();
        }
    }

    @Nested
    @DisplayName("2. Optional.of(value)")
    class OfTest {

        @Test
        @DisplayName("of() wraps a non-null value")
        void wrapsValue() {
            assertThat(OptionalCreationDemo.ofDemo("hello")).contains("hello");
        }

        @Test
        @DisplayName("of(null) throws NullPointerException")
        void nullThrows() {
            assertThatThrownBy(() -> OptionalCreationDemo.ofDemo(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("of() accepts an empty string — empty text is still a value")
        void emptyStringIsAValue() {
            assertThat(OptionalCreationDemo.ofDemo("")).contains("");
        }
    }

    @Nested
    @DisplayName("3. Optional.ofNullable(value)")
    class OfNullableTest {

        @Test
        @DisplayName("ofNullable(value) is present")
        void valueIsPresent() {
            assertThat(OptionalCreationDemo.ofNullableDemo("x")).contains("x");
        }

        @Test
        @DisplayName("ofNullable(null) is empty instead of throwing")
        void nullIsEmpty() {
            assertThat(OptionalCreationDemo.ofNullableDemo(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("4. Wrapping legacy null-returning APIs")
    class LookupTest {

        @Test
        @DisplayName("existing key gives a present Optional")
        void existingKey() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", "42");

            assertThat(OptionalCreationDemo.lookupDemo(map, "id")).contains("42");
        }

        @Test
        @DisplayName("missing key gives an empty Optional")
        void missingKey() {
            assertThat(OptionalCreationDemo.lookupDemo(new HashMap<String, String>(), "id")).isEmpty();
        }

        @Test
        @DisplayName("key mapped to null looks exactly like a missing key")
        void nullValueLooksMissing() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", null);

            assertThat(map.containsKey("id")).isTrue();
            assertThat(OptionalCreationDemo.lookupDemo(map, "id")).isEmpty();
        }
    }

    @Nested
    @DisplayName("5. isPresent() / get()")
    class IsPresentGetTest {

        @Test
        @DisplayName("describe reports present value")
        void describePresent() {
            assertThat(OptionalCreationDemo.describeDemo(Optional.of("x"))).isEqualTo("present:x");
        }

        @Test
        @DisplayName("describe reports absence")
        void describeAbsent() {
            assertThat(OptionalCreationDemo.describeDemo(Optional.<String>empty())).isEqualTo("absent");
        }

        @Test
        @DisplayName("get() on a present Optional returns the value")
        void getPresent() {
            assertThat(OptionalCreationDemo.unsafeGetDemo(Optional.of("x"))).isEqualTo("x");
        }

        @Test
        @DisplayName("get() on an empty Optional throws NoSuchElementException")
        void getEmptyThrows() {
            assertThatThrownBy(() -> OptionalCreationDemo.unsafeGetDemo(Optional.<String>empty()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("6. toString() and equality")
    class ToStringAndEqualityTest {

        @Test
        @DisplayName("toString shows the value when present")
        void toStringPresent() {
            assertThat(OptionalCreationDemo.toStringDemo(Optional.of("abc"))).isEqualTo("Optional[abc]");
        }

        @Test
        @DisplayName("toString shows Optional.empty when absent")
        void toStringEmpty() {
            assertThat(OptionalCreationDemo.toStringDemo(Optional.empty())).isEqualTo("Optional.empty");
        }

        @Test
        @DisplayName("two empty Optionals are equal")
        void emptiesEqual() {
            assertThat(OptionalCreationDemo.sameValueDemo(Optional.empty(), Optional.empty())).isTrue();
        }

        @Test
        @DisplayName("present Optionals with equal values are equal")
        void sameValuesEqual() {
            assertThat(OptionalCreationDemo.sameValueDemo(Optional.of("a"), Optional.of("a"))).isTrue();
        }

        @Test
        @DisplayName("present and empty are never equal")
        void presentVsEmpty() {
            assertThat(OptionalCreationDemo.sameValueDemo(Optional.of("a"), Optional.empty())).isFalse();
        }

        @Test
        @DisplayName("Optional.of(1) is not equal to Optional.of(1L) — Integer vs Long")
        void integerVersusLong() {
            assertThat(OptionalCreationDemo.sameValueDemo(Optional.of(1), Optional.of(1L))).isFalse();
        }
    }

    @Nested
    @DisplayName("7. Primitive specialisations")
    class PrimitiveOptionalTest {

        @Test
        @DisplayName("firstEven finds the first even number")
        void findsFirstEven() {
            assertThat(OptionalCreationDemo.firstEvenDemo(new int[]{3, 5, 8, 10})).hasValue(8);
        }

        @Test
        @DisplayName("firstEven is empty when there is no even number")
        void noEven() {
            assertThat(OptionalCreationDemo.firstEvenDemo(new int[]{3, 5})).isEmpty();
        }

        @Test
        @DisplayName("firstEven is empty for an empty array")
        void emptyArray() {
            assertThat(OptionalCreationDemo.firstEvenDemo(new int[0])).isEmpty();
        }

        @Test
        @DisplayName("getAsInt() on an empty OptionalInt throws NoSuchElementException")
        void getAsIntEmptyThrows() {
            OptionalInt empty = OptionalCreationDemo.firstEvenDemo(new int[0]);

            assertThatThrownBy(empty::getAsInt).isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("8. When NOT to return an Optional")
    class CollectionsTest {

        @Test
        @DisplayName("missing key gives an empty list, never null")
        void missingKeyEmptyList() {
            assertThat(OptionalCreationDemo.tagsDemo(new HashMap<String, List<String>>(), "java")).isEmpty();
        }

        @Test
        @DisplayName("existing key returns the very same list")
        void existingKeySameList() {
            List<String> tags = new ArrayList<String>(Arrays.asList("a", "b"));
            Map<String, List<String>> store = new HashMap<String, List<String>>();
            store.put("java", tags);

            assertThat(OptionalCreationDemo.tagsDemo(store, "java")).isSameAs(tags);
        }

        @Test
        @DisplayName("the returned empty list is immutable")
        void emptyListIsImmutable() {
            List<String> result = OptionalCreationDemo.tagsDemo(new HashMap<String, List<String>>(), "x");

            assertThatThrownBy(() -> result.add("new")).isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
