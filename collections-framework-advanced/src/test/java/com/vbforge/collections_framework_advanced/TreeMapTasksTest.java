package com.vbforge.collections_framework_advanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TreeMapTasks — your implementations")
class TreeMapTasksTest {

    private TreeMapTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TreeMapTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement shippingCost()")
    @DisplayName("1. shippingCost(tiers, weight)")
    class ShippingCostTest {

        private final TreeMap<Integer, Integer> tiers = new TreeMap<>();

        {
            tiers.put(0, 5);
            tiers.put(10, 8);
            tiers.put(25, 12);
        }

        @ParameterizedTest(name = "weight {0} costs {1}")
        @CsvSource({
                "3,   5",
                "0,   5",
                "9,   5",
                "10,  8",
                "24,  8",
                "25,  12",
                "100, 12"
        })
        @DisplayName("examples from Javadoc and more (a boundary belongs to the higher tier)")
        void costs(int weight, int expected) {
            assertThat(tasks.shippingCost(tiers, weight)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example from Javadoc: a weight below the lowest tier is rejected")
        void belowLowestTier() {
            assertThatThrownBy(() -> tasks.shippingCost(tiers, -1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an empty tier map rejects every weight")
        void noTiers() {
            assertThatThrownBy(() -> tasks.shippingCost(new TreeMap<Integer, Integer>(), 5))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the tier map is not modified")
        void tiersUntouched() {
            tasks.shippingCost(tiers, 12);

            assertThat(tiers).hasSize(3);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countWords()")
    @DisplayName("2. countWords(words)")
    class CountWordsTest {

        @Test
        @DisplayName("example from Javadoc: counts ignore case, the first spelling is kept")
        void javadocExample() {
            TreeMap<String, Integer> result = tasks.countWords(Arrays.asList("Pear", "apple", "pear", "APPLE", "fig"));

            assertThat(result).hasSize(3);
            assertThat(result.firstKey()).isEqualTo("apple");
            assertThat(result.get("apple")).isEqualTo(2);
            assertThat(result.get("fig")).isEqualTo(1);
            assertThat(result.get("Pear")).isEqualTo(2);
            assertThat(result.lastKey()).isEqualTo("Pear");
        }

        @Test
        @DisplayName("example from Javadoc: keys are sorted alphabetically ignoring case")
        void sortedIgnoringCase() {
            TreeMap<String, Integer> result = tasks.countWords(Arrays.asList("b", "B", "a"));

            assertThat(result.firstKey()).isEqualTo("a");
            assertThat(result.get("a")).isEqualTo(1);
            assertThat(result.get("b")).isEqualTo(2);
        }

        @Test
        @DisplayName("example from Javadoc: no words give an empty map")
        void noWords() {
            assertThat(tasks.countWords(Collections.<String>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("lookups on the result ignore case as well")
        void lookupIgnoresCase() {
            TreeMap<String, Integer> result = tasks.countWords(Arrays.asList("Java", "java", "JAVA"));

            assertThat(result).hasSize(1);
            assertThat(result.get("jAvA")).isEqualTo(3);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement eventsBetween()")
    @DisplayName("3. eventsBetween(timeline, fromInclusive, toInclusive)")
    class EventsBetweenTest {

        private final TreeMap<Integer, String> timeline = new TreeMap<>();

        {
            timeline.put(9, "standup");
            timeline.put(11, "review");
            timeline.put(14, "lunch");
            timeline.put(17, "deploy");
        }

        @Test
        @DisplayName("example from Javadoc: both ends are included")
        void inclusiveEnds() {
            assertThat(tasks.eventsBetween(timeline, 10, 14)).containsExactly("review", "lunch");
        }

        @Test
        @DisplayName("example from Javadoc: a single point in time")
        void singlePoint() {
            assertThat(tasks.eventsBetween(timeline, 9, 9)).containsExactly("standup");
        }

        @Test
        @DisplayName("example from Javadoc: a gap without events")
        void gap() {
            assertThat(tasks.eventsBetween(timeline, 12, 13)).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: a reversed range gives an empty list and must not throw")
        void reversedRange() {
            assertThat(tasks.eventsBetween(timeline, 14, 10)).isEmpty();
        }

        @Test
        @DisplayName("a range covering everything returns all events in time order")
        void everything() {
            List<String> all = tasks.eventsBetween(timeline, 0, 100);

            assertThat(all).containsExactly("standup", "review", "lunch", "deploy");
        }

        @Test
        @DisplayName("the timeline is not modified")
        void timelineUntouched() {
            tasks.eventsBetween(timeline, 10, 14);

            assertThat(timeline).hasSize(4);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement book()")
    @DisplayName("4. book(bookings, start, end)")
    class BookTest {

        private TreeMap<Integer, Integer> bookings;

        @BeforeEach
        void setUpBookings() {
            bookings = new TreeMap<>();
            bookings.put(10, 20);
            bookings.put(30, 40);
        }

        @Test
        @DisplayName("example from Javadoc: a slot touching both neighbours is accepted and stored")
        void touchingIsFine() {
            assertThat(tasks.book(bookings, 20, 30)).isTrue();
            assertThat(bookings).containsOnlyKeys(10, 20, 30);
            assertThat(bookings.get(20)).isEqualTo(30);
        }

        @Test
        @DisplayName("example from Javadoc: overlapping the end of an earlier booking is rejected")
        void overlapsEarlierBooking() {
            assertThat(tasks.book(bookings, 15, 25)).isFalse();
            assertThat(bookings).hasSize(2);
        }

        @Test
        @DisplayName("example from Javadoc: overlapping the start of a later booking is rejected")
        void overlapsLaterBooking() {
            assertThat(tasks.book(bookings, 5, 12)).isFalse();
            assertThat(bookings).hasSize(2);
        }

        @Test
        @DisplayName("example from Javadoc: a slot before everything else is accepted")
        void beforeEverything() {
            assertThat(tasks.book(bookings, 0, 10)).isTrue();
            assertThat(bookings).containsOnlyKeys(0, 10, 30);
        }

        @Test
        @DisplayName("example from Javadoc: a slot inside an existing booking is rejected")
        void insideExisting() {
            assertThat(tasks.book(bookings, 35, 38)).isFalse();
        }

        @Test
        @DisplayName("a slot that swallows an existing booking is rejected")
        void swallowsExisting() {
            assertThat(tasks.book(bookings, 5, 25)).isFalse();
            assertThat(bookings).hasSize(2);
        }

        @Test
        @DisplayName("a booking starting exactly at an existing start is rejected")
        void sameStart() {
            assertThat(tasks.book(bookings, 10, 12)).isFalse();
        }

        @Test
        @DisplayName("a slot after everything else is accepted; booking the same slot twice fails")
        void afterEverythingThenTwice() {
            assertThat(tasks.book(bookings, 50, 60)).isTrue();
            assertThat(tasks.book(bookings, 50, 60)).isFalse();
        }

        @Test
        @DisplayName("an empty or reversed slot is rejected")
        void invalidSlot() {
            assertThatThrownBy(() -> tasks.book(bookings, 5, 5)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.book(bookings, 9, 3)).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
