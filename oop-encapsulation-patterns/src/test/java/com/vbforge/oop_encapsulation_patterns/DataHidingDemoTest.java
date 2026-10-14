package com.vbforge.oop_encapsulation_patterns;

import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.Account;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.AnemicAccount;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.LeakyBox;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.LeakySchedule;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.ListStock;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.MapStock;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.OpenAccount;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.SafeBox;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.SafeSchedule;
import com.vbforge.oop_encapsulation_patterns.DataHidingDemo.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * DataHidingDemoTest — verifies every concept shown in {@link DataHidingDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how data hiding behaves.
 */
@DisplayName("DataHidingDemo — API contracts")
class DataHidingDemoTest {

    // ── 1. Guarded state ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Guarded state")
    class GuardedStateTest {

        @Test
        @DisplayName("a public field accepts any value, even an invalid one")
        void publicFieldHasNoGuard() {
            OpenAccount open = new OpenAccount(100);
            open.balance = -999;
            assertThat(open.balance).isEqualTo(-999.0);
        }

        @Test
        @DisplayName("the constructor rejects a negative opening balance")
        void constructorGuard() {
            assertThatThrownBy(() -> new Account(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("initial balance must not be negative");
        }

        @Test
        @DisplayName("withdraw refuses to overdraw and leaves the balance unchanged")
        void withdrawGuard() {
            Account account = new Account(100);
            assertThatThrownBy(() -> account.withdraw(500))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("insufficient funds");
            assertThat(account.balance()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("deposit and withdraw reject a non-positive amount")
        void nonPositiveAmounts() {
            Account account = new Account(100);
            assertThatThrownBy(() -> account.deposit(0)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> account.withdraw(-1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("valid operations change the balance")
        void validOperations() {
            Account account = new Account(100);
            account.deposit(50);
            account.withdraw(30);
            assertThat(account.balance()).isEqualTo(120.0);
        }
    }

    // ── 2. Leaking internals OUT ──────────────────────────────────────────────

    @Nested
    @DisplayName("2. Leaking internals OUT")
    class LeakOutTest {

        @Test
        @DisplayName("a getter that returns the real list lets the caller empty the object")
        void leakyGetter() {
            LeakySchedule leaky = new LeakySchedule();
            leaky.add("9:00");
            leaky.slots().clear();
            assertThat(leaky.size()).isZero();
        }

        @Test
        @DisplayName("a read-only copy cannot be modified")
        void safeGetterIsReadOnly() {
            SafeSchedule safe = new SafeSchedule();
            safe.add("9:00");
            assertThatThrownBy(() -> safe.slots().clear()).isInstanceOf(UnsupportedOperationException.class);
            assertThat(safe.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("a returned copy does not follow later changes")
        void safeGetterIsSnapshot() {
            SafeSchedule safe = new SafeSchedule();
            safe.add("9:00");
            List<String> before = safe.slots();
            safe.add("10:00");
            assertThat(before).containsExactly("9:00");
            assertThat(safe.slots()).containsExactly("9:00", "10:00");
        }

        @Test
        @DisplayName("each call returns a different object")
        void differentObjects() {
            SafeSchedule safe = new SafeSchedule();
            safe.add("9:00");
            assertThat(safe.slots()).isNotSameAs(safe.slots());
        }
    }

    // ── 3. Leaking internals IN ───────────────────────────────────────────────

    @Nested
    @DisplayName("3. Leaking internals IN")
    class LeakInTest {

        @Test
        @DisplayName("a constructor that keeps the caller's array can be broken afterwards")
        void leakyConstructor() {
            int[] dims = {2, 3, 4};
            LeakyBox leaky = new LeakyBox(dims);
            dims[0] = -1;
            assertThat(leaky.volume()).isEqualTo(-12);
        }

        @Test
        @DisplayName("a defensive copy ignores later changes to the caller's array")
        void safeConstructor() {
            int[] dims = {2, 3, 4};
            SafeBox safe = new SafeBox(dims);
            dims[0] = -1;
            assertThat(safe.volume()).isEqualTo(24);
        }

        @Test
        @DisplayName("the copy is validated: a non-positive side is rejected")
        void validatesCopy() {
            assertThatThrownBy(() -> new SafeBox(new int[] {2, 0, 4}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("every side must be positive");
        }

        @Test
        @DisplayName("copy OUT: changing the returned array does not change the box")
        void copyOut() {
            SafeBox safe = new SafeBox(new int[] {2, 3, 4});
            int[] sides = safe.sides();
            sides[0] = 100;
            assertThat(safe.sides()).containsExactly(2, 3, 4);
            assertThat(safe.sides()).isNotSameAs(safe.sides());
        }
    }

    // ── 4. Views vs snapshots ─────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Views vs snapshots")
    class ViewVsSnapshotTest {

        @Test
        @DisplayName("a view sees later changes of the source")
        void viewFollowsSource() {
            List<String> source = new ArrayList<>(Arrays.asList("a"));
            List<String> view = DataHidingDemo.viewOf(source);
            source.add("b");
            assertThat(view).containsExactly("a", "b");
        }

        @Test
        @DisplayName("a snapshot does not see later changes of the source")
        void snapshotIsIndependent() {
            List<String> source = new ArrayList<>(Arrays.asList("a"));
            List<String> snapshot = DataHidingDemo.snapshotOf(source);
            source.add("b");
            assertThat(snapshot).containsExactly("a");
        }

        @Test
        @DisplayName("both refuse modification")
        void bothReadOnly() {
            List<String> source = new ArrayList<>(Arrays.asList("a"));
            assertThatThrownBy(() -> DataHidingDemo.viewOf(source).add("x"))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> DataHidingDemo.snapshotOf(source).add("x"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("List.copyOf rejects a null element")
        void copyOfRejectsNull() {
            List<String> source = new ArrayList<>(Arrays.asList("a", null));
            assertThatThrownBy(() -> DataHidingDemo.snapshotOf(source)).isInstanceOf(NullPointerException.class);
        }
    }

    // ── 5. Behaviour over getters/setters ─────────────────────────────────────

    @Nested
    @DisplayName("5. Behaviour over getters/setters")
    class BehaviourOverSettersTest {

        @Test
        @DisplayName("a private field with a public setter still accepts an invalid value")
        void setterBreaksInvariant() {
            AnemicAccount anemic = new AnemicAccount();
            anemic.setBalance(-50);
            assertThat(anemic.getBalance()).isEqualTo(-50.0);
        }

        @Test
        @DisplayName("the rule written in a caller protects only that caller")
        void ruleInCaller() {
            AnemicAccount anemic = new AnemicAccount();
            anemic.setBalance(100);
            DataHidingDemo.anemicWithdraw(anemic, 500);
            assertThat(anemic.getBalance()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("a behaviour method keeps the rule in one place")
        void behaviourKeepsRule() {
            Account account = new Account(100);
            account.withdraw(30);
            assertThat(account.balance()).isEqualTo(70.0);
            assertThatThrownBy(() -> account.withdraw(1000)).isInstanceOf(IllegalStateException.class);
            assertThat(account.balance()).isEqualTo(70.0);
        }
    }

    // ── 6. Hiding the representation ──────────────────────────────────────────

    @Nested
    @DisplayName("6. Hiding the representation")
    class HidingRepresentationTest {

        private void exercise(Stock stock) {
            stock.add("apple", 3);
            stock.add("apple", 2);
            stock.add("pear", 1);
            assertThat(stock.quantity("apple")).isEqualTo(5);
            assertThat(stock.quantity("pear")).isEqualTo(1);
            assertThat(stock.quantity("plum")).isZero();
        }

        @Test
        @DisplayName("the map-based stock honours the contract")
        void mapStock() {
            exercise(new MapStock());
        }

        @Test
        @DisplayName("the list-based stock honours the same contract")
        void listStock() {
            exercise(new ListStock());
        }

        @Test
        @DisplayName("callers cannot tell the implementations apart")
        void sameBehaviour() {
            Stock first = new MapStock();
            Stock second = new ListStock();
            for (Stock stock : new Stock[] {first, second}) {
                stock.add("x", 7);
            }
            assertThat(first.quantity("x")).isEqualTo(second.quantity("x"));
        }
    }
}
