package com.vbforge.optional_and_null_handling;

import com.vbforge.optional_and_null_handling.model.Address;
import com.vbforge.optional_and_null_handling.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OptionalChainingTasks — your implementations")
class OptionalChainingTasksTest {

    private static final User ANN_KYIV = new User("Ann", new Address("Kyiv", "01001"));
    private static final User BOB_NO_ADDRESS = new User("Bob", null);
    private static final User CID_NO_CITY = new User("Cid", new Address(null, "12345"));
    private static final User DAN_NO_ZIP = new User("Dan", new Address("Lviv", null));

    private OptionalChainingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new OptionalChainingTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement trimmedLength()")
    @DisplayName("1. trimmedLength(text)")
    class TrimmedLengthTest {

        @Test
        @DisplayName("example from Javadoc: length is measured after trimming")
        void trimsFirst() {
            assertThat(tasks.trimmedLength(Optional.of("  hello "))).contains(5);
        }

        @Test
        @DisplayName("example from Javadoc: blank text gives Optional[0]")
        void blankGivesZero() {
            assertThat(tasks.trimmedLength(Optional.of("   "))).contains(0);
        }

        @Test
        @DisplayName("example from Javadoc: empty stays empty")
        void emptyStaysEmpty() {
            assertThat(tasks.trimmedLength(Optional.<String>empty())).isEmpty();
        }

        @Test
        @DisplayName("empty string gives Optional[0]")
        void emptyStringGivesZero() {
            assertThat(tasks.trimmedLength(Optional.of(""))).contains(0);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement zipOf()")
    @DisplayName("2. zipOf(user)")
    class ZipOfTest {

        @Test
        @DisplayName("example from Javadoc: returns the zip of the address")
        void zipPresent() {
            assertThat(tasks.zipOf(ANN_KYIV)).contains("01001");
        }

        @Test
        @DisplayName("example from Javadoc: user without address gives empty")
        void noAddress() {
            assertThat(tasks.zipOf(BOB_NO_ADDRESS)).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: address without zip gives empty")
        void noZip() {
            assertThat(tasks.zipOf(DAN_NO_ZIP)).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: null user gives empty")
        void nullUser() {
            assertThat(tasks.zipOf(null)).isEmpty();
        }

        @Test
        @DisplayName("zip is returned even when the city is missing")
        void zipWithoutCity() {
            assertThat(tasks.zipOf(CID_NO_CITY)).contains("12345");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement cityStartingWith()")
    @DisplayName("3. cityStartingWith(user, prefix)")
    class CityStartingWithTest {

        @ParameterizedTest(name = "prefix \"{0}\" -> {1}")
        @CsvSource({
                "Ky,   Kyiv",
                "K,    Kyiv",
                "Kyiv, Kyiv"
        })
        @DisplayName("example from Javadoc: matching prefix returns the city")
        void matchingPrefix(String prefix, String expectedCity) {
            assertThat(tasks.cityStartingWith(ANN_KYIV, prefix)).contains(expectedCity);
        }

        @Test
        @DisplayName("example from Javadoc: non-matching prefix gives empty")
        void nonMatching() {
            assertThat(tasks.cityStartingWith(ANN_KYIV, "Lv")).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: the match is case-sensitive")
        void caseSensitive() {
            assertThat(tasks.cityStartingWith(ANN_KYIV, "ky")).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: user whose address has no city gives empty")
        void noCity() {
            assertThat(tasks.cityStartingWith(CID_NO_CITY, "K")).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: null user gives empty")
        void nullUser() {
            assertThat(tasks.cityStartingWith(null, "K")).isEmpty();
        }

        @Test
        @DisplayName("user without address gives empty")
        void noAddress() {
            assertThat(tasks.cityStartingWith(BOB_NO_ADDRESS, "K")).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement mailingLabel()")
    @DisplayName("4. mailingLabel(user)")
    class MailingLabelTest {

        @Test
        @DisplayName("example from Javadoc: name and city")
        void nameAndCity() {
            assertThat(tasks.mailingLabel(ANN_KYIV)).isEqualTo("Ann (Kyiv)");
        }

        @Test
        @DisplayName("example from Javadoc: no address gives only the name")
        void noAddress() {
            assertThat(tasks.mailingLabel(BOB_NO_ADDRESS)).isEqualTo("Bob");
        }

        @Test
        @DisplayName("example from Javadoc: address without city gives only the name")
        void noCity() {
            assertThat(tasks.mailingLabel(CID_NO_CITY)).isEqualTo("Cid");
        }

        @Test
        @DisplayName("example from Javadoc: null user gives \"nobody\"")
        void nullUser() {
            assertThat(tasks.mailingLabel(null)).isEqualTo("nobody");
        }

        @Test
        @DisplayName("a missing zip does not matter")
        void zipIsIrrelevant() {
            assertThat(tasks.mailingLabel(DAN_NO_ZIP)).isEqualTo("Dan (Lviv)");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumIfBothPresent()")
    @DisplayName("5. sumIfBothPresent(a, b)")
    class SumIfBothPresentTest {

        @Test
        @DisplayName("example from Javadoc: both present gives the sum")
        void bothPresent() {
            assertThat(tasks.sumIfBothPresent(Optional.of(2), Optional.of(3))).contains(5);
        }

        @Test
        @DisplayName("example from Javadoc: second empty gives empty")
        void secondEmpty() {
            assertThat(tasks.sumIfBothPresent(Optional.of(2), Optional.<Integer>empty())).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: first empty gives empty")
        void firstEmpty() {
            assertThat(tasks.sumIfBothPresent(Optional.<Integer>empty(), Optional.of(3))).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: both empty gives empty")
        void bothEmpty() {
            assertThat(tasks.sumIfBothPresent(Optional.<Integer>empty(), Optional.<Integer>empty())).isEmpty();
        }

        @Test
        @DisplayName("zero and negative values are valid inputs")
        void zeroAndNegative() {
            assertThat(tasks.sumIfBothPresent(Optional.of(0), Optional.of(-4))).contains(-4);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement citiesOf()")
    @DisplayName("6. citiesOf(users)")
    class CitiesOfTest {

        @Test
        @DisplayName("example from Javadoc: collects cities in order, skipping users without one")
        void collectsInOrder() {
            User cidInLviv = new User("Cid", new Address("Lviv", null));

            List<String> cities = tasks.citiesOf(Arrays.asList(ANN_KYIV, BOB_NO_ADDRESS, cidInLviv));

            assertThat(cities).containsExactly("Kyiv", "Lviv");
        }

        @Test
        @DisplayName("example from Javadoc: null entries and users without address give an empty list")
        void nothingToCollect() {
            assertThat(tasks.citiesOf(Arrays.asList(null, BOB_NO_ADDRESS))).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: empty input gives an empty list")
        void emptyInput() {
            assertThat(tasks.citiesOf(Collections.<User>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("users whose address has no city are skipped")
        void skipsMissingCity() {
            assertThat(tasks.citiesOf(Arrays.asList(CID_NO_CITY, ANN_KYIV))).containsExactly("Kyiv");
        }

        @Test
        @DisplayName("duplicates are kept")
        void keepsDuplicates() {
            User second = new User("Eve", new Address("Kyiv", null));

            assertThat(tasks.citiesOf(Arrays.asList(ANN_KYIV, second))).containsExactly("Kyiv", "Kyiv");
        }

        @Test
        @DisplayName("returns a new list on every call")
        void returnsNewList() {
            List<User> users = new ArrayList<User>(Collections.singletonList(ANN_KYIV));

            assertThat(tasks.citiesOf(users)).isNotSameAs(tasks.citiesOf(users));
        }
    }
}
