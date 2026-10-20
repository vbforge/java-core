package com.vbforge.optional_and_null_handling;

import com.vbforge.optional_and_null_handling.model.Address;
import com.vbforge.optional_and_null_handling.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OptionalChainingDemo — API contracts")
class OptionalChainingDemoTest {

    private static final User WITH_ALL = new User("Ann", new Address("Kyiv", "01001"));
    private static final User NO_ADDRESS = new User("Bob", null);
    private static final User NO_CITY = new User("Cid", new Address(null, "12"));
    private static final User NO_ZIP = new User("Dan", new Address("Lviv", null));

    @Nested
    @DisplayName("1. map")
    class MapTest {

        @Test
        @DisplayName("transforms the value inside a present Optional")
        void transformsPresent() {
            assertThat(OptionalChainingDemo.upperDemo(Optional.of("java"))).contains("JAVA");
        }

        @Test
        @DisplayName("an empty Optional stays empty")
        void emptyStaysEmpty() {
            assertThat(OptionalChainingDemo.upperDemo(Optional.<String>empty())).isEmpty();
        }

        @Test
        @DisplayName("a mapping function returning null produces an empty Optional")
        void nullResultBecomesEmpty() {
            assertThat(OptionalChainingDemo.cityOfDemo(new Address(null, "01001"))).isEmpty();
        }

        @Test
        @DisplayName("a mapping function returning a value produces a present Optional")
        void valueResultIsPresent() {
            assertThat(OptionalChainingDemo.cityOfDemo(new Address("Kyiv", null))).contains("Kyiv");
        }

        @Test
        @DisplayName("a null source object gives an empty Optional")
        void nullSource() {
            assertThat(OptionalChainingDemo.cityOfDemo(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("2. map vs flatMap")
    class MapVsFlatMapTest {

        @Test
        @DisplayName("map with an Optional-returning function nests the Optionals")
        void mapNests() {
            Optional<Optional<Address>> nested = OptionalChainingDemo.nestedDemo(WITH_ALL);

            assertThat(nested).isPresent();
            assertThat(nested.get()).isPresent();
        }

        @Test
        @DisplayName("nested trap: outer layer is present even though there is no address")
        void outerPresentInnerEmpty() {
            Optional<Optional<Address>> nested = OptionalChainingDemo.nestedDemo(NO_ADDRESS);

            assertThat(nested).isPresent();
            assertThat(nested.get()).isEmpty();
        }

        @Test
        @DisplayName("nested with a null user is completely empty")
        void nestedNullUser() {
            assertThat(OptionalChainingDemo.nestedDemo(null)).isEmpty();
        }

        @Test
        @DisplayName("flatMap flattens to a single Optional that holds the address")
        void flatMapFlattens() {
            assertThat(OptionalChainingDemo.flattenedDemo(WITH_ALL)).isPresent();
        }

        @Test
        @DisplayName("flatMap gives one empty Optional when the address is missing")
        void flatMapEmptyWhenNoAddress() {
            assertThat(OptionalChainingDemo.flattenedDemo(NO_ADDRESS)).isEmpty();
        }
    }

    @Nested
    @DisplayName("3. Chain instead of null-check pyramid")
    class ChainTest {

        @Test
        @DisplayName("full path yields the upper-cased city")
        void fullPath() {
            assertThat(OptionalChainingDemo.cityChainDemo(WITH_ALL)).isEqualTo("KYIV");
        }

        @Test
        @DisplayName("any missing link yields UNKNOWN")
        void missingLinks() {
            assertThat(OptionalChainingDemo.cityChainDemo(NO_ADDRESS)).isEqualTo("UNKNOWN");
            assertThat(OptionalChainingDemo.cityChainDemo(NO_CITY)).isEqualTo("UNKNOWN");
            assertThat(OptionalChainingDemo.cityChainDemo(null)).isEqualTo("UNKNOWN");
        }

        @Test
        @DisplayName("chain and legacy null-check pyramid behave identically")
        void chainMatchesLegacy() {
            User[] users = {WITH_ALL, NO_ADDRESS, NO_CITY, NO_ZIP, null};

            for (User user : users) {
                assertThat(OptionalChainingDemo.cityChainDemo(user))
                        .isEqualTo(OptionalChainingDemo.cityLegacyDemo(user));
            }
        }
    }

    @Nested
    @DisplayName("4. filter")
    class FilterTest {

        @Test
        @DisplayName("keeps a zip that has exactly 5 characters")
        void keepsValid() {
            assertThat(OptionalChainingDemo.validZipDemo(WITH_ALL)).contains("01001");
        }

        @Test
        @DisplayName("a present zip of the wrong length becomes empty")
        void dropsInvalid() {
            assertThat(OptionalChainingDemo.validZipDemo(NO_CITY)).isEmpty();
        }

        @Test
        @DisplayName("missing zip, missing address and null user all give empty")
        void missingLinks() {
            assertThat(OptionalChainingDemo.validZipDemo(NO_ZIP)).isEmpty();
            assertThat(OptionalChainingDemo.validZipDemo(NO_ADDRESS)).isEmpty();
            assertThat(OptionalChainingDemo.validZipDemo(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("5. Chains are lazy")
    class LazyTest {

        @Test
        @DisplayName("mapping function is never called for an empty Optional")
        void notCalledWhenEmpty() {
            AtomicInteger calls = new AtomicInteger();

            Optional<Integer> result = OptionalChainingDemo.lazyMapDemo(Optional.<String>empty(), calls);

            assertThat(result).isEmpty();
            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("mapping function is called exactly once for a present Optional")
        void calledOnceWhenPresent() {
            AtomicInteger calls = new AtomicInteger();

            Optional<Integer> result = OptionalChainingDemo.lazyMapDemo(Optional.of("abc"), calls);

            assertThat(result).contains(3);
            assertThat(calls.get()).isEqualTo(1);
        }
    }
}
