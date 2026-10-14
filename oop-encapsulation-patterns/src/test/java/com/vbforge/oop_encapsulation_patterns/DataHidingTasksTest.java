package com.vbforge.oop_encapsulation_patterns;

import com.vbforge.oop_encapsulation_patterns.DataHidingTasks.Account;
import com.vbforge.oop_encapsulation_patterns.DataHidingTasks.Playlist;
import com.vbforge.oop_encapsulation_patterns.DataHidingTasks.Roster;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * DataHidingTasksTest — tests for every method in {@link DataHidingTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code DataHidingTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=DataHidingTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("DataHidingTasks — your implementations")
class DataHidingTasksTest {

    // ── Task 1: Playlist.tracks ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement tracks()")
    @DisplayName("Task 1 — Playlist.tracks()")
    class TracksTest {

        private Playlist playlistOf(String... names) {
            Playlist playlist = new Playlist();
            for (String name : names) {
                playlist.add(name);
            }
            return playlist;
        }

        @Test
        @DisplayName("returns the tracks in insertion order")
        void insertionOrder() {
            assertThat(playlistOf("a", "b").tracks()).containsExactly("a", "b");
        }

        @Test
        @DisplayName("an empty playlist → []")
        void empty() {
            assertThat(playlistOf().tracks()).isEmpty();
        }

        @Test
        @DisplayName("the result cannot be modified")
        void readOnly() {
            Playlist playlist = playlistOf("a", "b");
            assertThatThrownBy(() -> playlist.tracks().add("x")).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> playlist.tracks().clear()).isInstanceOf(UnsupportedOperationException.class);
            assertThat(playlist.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("a list returned earlier does not follow later additions")
        void snapshot() {
            Playlist playlist = playlistOf("a", "b");
            List<String> before = playlist.tracks();
            playlist.add("c");
            assertThat(before).containsExactly("a", "b");
            assertThat(playlist.tracks()).containsExactly("a", "b", "c");
        }
    }

    // ── Task 2: Playlist.addAll ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement addAll()")
    @DisplayName("Task 2 — Playlist.addAll()")
    class AddAllTest {

        private Playlist playlistOf(String... names) {
            Playlist playlist = new Playlist();
            for (String name : names) {
                playlist.add(name);
            }
            return playlist;
        }

        @Test
        @DisplayName("appends every valid track in order")
        void appendsAll() {
            Playlist playlist = playlistOf("a");
            playlist.addAll(Arrays.asList("b", "c"));
            assertThat(playlist.size()).isEqualTo(3);
            assertThat(playlist.tracks()).containsExactly("a", "b", "c");   // also needs Task 1
        }

        @Test
        @DisplayName("a blank track rejects the whole batch")
        void blankRejectsBatch() {
            Playlist playlist = playlistOf("a");
            assertThatThrownBy(() -> playlist.addAll(Arrays.asList("d", " ")))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(playlist.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("a null track rejects the whole batch")
        void nullRejectsBatch() {
            Playlist playlist = playlistOf("a");
            assertThatThrownBy(() -> playlist.addAll(Arrays.asList("d", null)))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(playlist.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("a null list is rejected")
        void nullList() {
            assertThatThrownBy(() -> new Playlist().addAll(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an empty list changes nothing")
        void emptyList() {
            Playlist playlist = playlistOf("a");
            playlist.addAll(Collections.<String>emptyList());
            assertThat(playlist.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("later changes to the caller's list do not affect the playlist")
        void independentOfCallerList() {
            Playlist playlist = new Playlist();
            List<String> input = new ArrayList<>(Arrays.asList("a", "b"));
            playlist.addAll(input);
            input.clear();
            assertThat(playlist.size()).isEqualTo(2);
        }
    }

    // ── Task 3: Account.withdraw ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement withdraw()")
    @DisplayName("Task 3 — Account.withdraw()")
    class WithdrawTest {

        @ParameterizedTest(name = "balance 100, withdraw {0} → balance {1}")
        @CsvSource({
                "30.0,  70.0",
                "100.0, 0.0",
                "0.5,   99.5",
        })
        @DisplayName("parametrized: valid withdrawals")
        void valid(double amount, double expectedBalance) {
            Account account = new Account(100);
            account.withdraw(amount);
            assertThat(account.balance()).isEqualTo(expectedBalance);
        }

        @ParameterizedTest(name = "withdraw {0} → IllegalArgumentException")
        @CsvSource({"0.0", "-5.0"})
        @DisplayName("a non-positive amount is an IllegalArgumentException")
        void nonPositive(double amount) {
            Account account = new Account(100);
            assertThatThrownBy(() -> account.withdraw(amount)).isInstanceOf(IllegalArgumentException.class);
            assertThat(account.balance()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("more than the balance is an IllegalStateException and changes nothing")
        void overdraw() {
            Account account = new Account(100);
            assertThatThrownBy(() -> account.withdraw(100.5)).isInstanceOf(IllegalStateException.class);
            assertThat(account.balance()).isEqualTo(100.0);
        }
    }

    // ── Task 4: Account.transferTo ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement transferTo()")
    @DisplayName("Task 4 — Account.transferTo()")
    class TransferToTest {

        @Test
        @DisplayName("moves the amount from one account to the other (also needs Task 3)")
        void transfers() {
            Account a = new Account(100);
            Account b = new Account(10);
            a.transferTo(b, 40);
            assertThat(a.balance()).isEqualTo(60.0);
            assertThat(b.balance()).isEqualTo(50.0);
        }

        @Test
        @DisplayName("insufficient funds: IllegalStateException and nothing changes (also needs Task 3)")
        void insufficientFunds() {
            Account a = new Account(100);
            Account b = new Account(10);
            assertThatThrownBy(() -> a.transferTo(b, 500)).isInstanceOf(IllegalStateException.class);
            assertThat(a.balance()).isEqualTo(100.0);
            assertThat(b.balance()).isEqualTo(10.0);
        }

        @Test
        @DisplayName("a non-positive amount: IllegalArgumentException and nothing changes (also needs Task 3)")
        void nonPositive() {
            Account a = new Account(100);
            Account b = new Account(10);
            assertThatThrownBy(() -> a.transferTo(b, 0)).isInstanceOf(IllegalArgumentException.class);
            assertThat(a.balance()).isEqualTo(100.0);
            assertThat(b.balance()).isEqualTo(10.0);
        }

        @Test
        @DisplayName("a null target is an IllegalArgumentException and nothing changes")
        void nullTarget() {
            Account a = new Account(100);
            assertThatThrownBy(() -> a.transferTo(null, 10)).isInstanceOf(IllegalArgumentException.class);
            assertThat(a.balance()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("transferring to the same account is an IllegalArgumentException")
        void sameAccount() {
            Account a = new Account(100);
            assertThatThrownBy(() -> a.transferTo(a, 10)).isInstanceOf(IllegalArgumentException.class);
            assertThat(a.balance()).isEqualTo(100.0);
        }
    }

    // ── Task 5: Roster.of ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement of()")
    @DisplayName("Task 5 — Roster.of()")
    class RosterOfTest {

        @Test
        @DisplayName("stores the names in order")
        void storesNames() {
            Roster roster = Roster.of(new String[] {"Ann", "Bob"});
            assertThat(roster.size()).isEqualTo(2);
            assertThat(roster.names()).containsExactly("Ann", "Bob");
        }

        @Test
        @DisplayName("later changes to the caller's array do not change the roster")
        void independentOfCallerArray() {
            String[] array = {"Ann", "Bob"};
            Roster roster = Roster.of(array);
            array[0] = "Zed";
            assertThat(roster.names()).containsExactly("Ann", "Bob");
        }

        @Test
        @DisplayName("a null name is rejected")
        void nullName() {
            assertThatThrownBy(() -> Roster.of(new String[] {"Ann", null}))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a blank name is rejected")
        void blankName() {
            assertThatThrownBy(() -> Roster.of(new String[] {"Ann", "  "}))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a null array is rejected")
        void nullArray() {
            assertThatThrownBy(() -> Roster.of(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an empty array gives an empty roster")
        void emptyArray() {
            assertThat(Roster.of(new String[0]).size()).isZero();
        }
    }
}
