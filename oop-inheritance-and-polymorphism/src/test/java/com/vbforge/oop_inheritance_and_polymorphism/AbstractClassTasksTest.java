package com.vbforge.oop_inheritance_and_polymorphism;

import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassTasks.BankAccount;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassTasks.CheckingAccount;
import com.vbforge.oop_inheritance_and_polymorphism.AbstractClassTasks.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AbstractClassTasksTest — tests for every method in {@link AbstractClassTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code AbstractClassTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=AbstractClassTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("AbstractClassTasks — your implementations")
class AbstractClassTasksTest {

    private AbstractClassTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new AbstractClassTasks();
    }

    // ── Task 1: SavingsAccount.fee ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement SavingsAccount.fee()")
    @DisplayName("Task 1 — SavingsAccount.fee()")
    class SavingsFeeTest {

        @Test
        @DisplayName("fee(50.0) → 0.0")
        void smallAmount() {
            assertThat(new SavingsAccount(100).fee(50.0)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("fee(1000.0) → 0.0 (amount is irrelevant)")
        void largeAmount() {
            assertThat(new SavingsAccount(100).fee(1000.0)).isEqualTo(0.0);
        }
    }

    // ── Task 2: SavingsAccount.canDebit ───────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement SavingsAccount.canDebit()")
    @DisplayName("Task 2 — SavingsAccount.canDebit()")
    class SavingsCanDebitTest {

        @ParameterizedTest(name = "balance 100, debit {0} → {1}")
        @CsvSource({
                "0.0,    true",
                "50.0,   true",
                "100.0,  true",
                "100.01, false",
                "500.0,  false",
        })
        @DisplayName("parametrized: balance 100")
        void boundaries(double totalDebit, boolean expected) {
            assertThat(new SavingsAccount(100).canDebit(totalDebit)).isEqualTo(expected);
        }

        @Test
        @DisplayName("withdraw() refuses to overdraw and leaves the balance unchanged (also needs Task 1)")
        void withdrawRefused() {
            SavingsAccount account = new SavingsAccount(100);
            assertThat(account.withdraw(100.5)).isFalse();
            assertThat(account.getBalance()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("withdraw() may drain the account to exactly zero (also needs Task 1)")
        void withdrawToZero() {
            SavingsAccount account = new SavingsAccount(100);
            assertThat(account.withdraw(100)).isTrue();
            assertThat(account.getBalance()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("canDebit follows the CURRENT balance, including deposits")
        void followsBalance() {
            SavingsAccount account = new SavingsAccount(100);
            account.deposit(50);
            assertThat(account.canDebit(150.0)).isTrue();
        }
    }

    // ── Task 3: CheckingAccount.fee ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement CheckingAccount.fee()")
    @DisplayName("Task 3 — CheckingAccount.fee()")
    class CheckingFeeTest {

        @Test
        @DisplayName("fee(50.0) → 1.5")
        void smallAmount() {
            assertThat(new CheckingAccount(100, 50).fee(50.0)).isEqualTo(1.5);
        }

        @Test
        @DisplayName("fee(1000.0) → 1.5 (flat, not proportional)")
        void largeAmount() {
            assertThat(new CheckingAccount(100, 50).fee(1000.0)).isEqualTo(1.5);
        }
    }

    // ── Task 4: CheckingAccount.canDebit ──────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement CheckingAccount.canDebit()")
    @DisplayName("Task 4 — CheckingAccount.canDebit()")
    class CheckingCanDebitTest {

        @ParameterizedTest(name = "balance 100, overdraft {0}, debit {1} → {2}")
        @CsvSource({
                "50.0, 150.0, true",
                "50.0, 150.5, false",
                "0.0,  100.0, true",
                "0.0,  100.5, false",
                "50.0, 10.0,  true",
        })
        @DisplayName("parametrized: balance 100")
        void boundaries(double overdraft, double totalDebit, boolean expected) {
            assertThat(new CheckingAccount(100, overdraft).canDebit(totalDebit)).isEqualTo(expected);
        }

        @Test
        @DisplayName("withdraw() may dip into the overdraft (also needs Task 3)")
        void withdrawIntoOverdraft() {
            CheckingAccount account = new CheckingAccount(100, 50);
            assertThat(account.withdraw(100)).isTrue();          // 100 + 1.5 fee → balance -1.5
            assertThat(account.getBalance()).isEqualTo(-1.5);
        }

        @Test
        @DisplayName("withdraw() refuses to exceed the overdraft limit and changes nothing (also needs Task 3)")
        void withdrawBeyondLimit() {
            CheckingAccount account = new CheckingAccount(100, 50);
            assertThat(account.withdraw(150)).isFalse();         // 150 + 1.5 fee → -51.5 < -50
            assertThat(account.getBalance()).isEqualTo(100.0);
        }
    }

    // ── Task 5: isAbstract ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isAbstract()")
    @DisplayName("Task 5 — isAbstract()")
    class IsAbstractTest {

        @Test
        @DisplayName("BankAccount (abstract class) → true")
        void abstractClass() {
            assertThat(tasks.isAbstract(BankAccount.class)).isTrue();
        }

        @Test
        @DisplayName("SavingsAccount (concrete subclass) → false")
        void concreteClass() {
            assertThat(tasks.isAbstract(SavingsAccount.class)).isFalse();
        }

        @Test
        @DisplayName("JDK: Number → true, String → false")
        void jdkTypes() {
            assertThat(tasks.isAbstract(Number.class)).isTrue();
            assertThat(tasks.isAbstract(String.class)).isFalse();
        }

        @Test
        @DisplayName("an interface is implicitly abstract → true")
        void interfaceType() {
            assertThat(tasks.isAbstract(Runnable.class)).isTrue();
        }
    }

    // ── Task 6: abstractMethodNames ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement abstractMethodNames()")
    @DisplayName("Task 6 — abstractMethodNames()")
    class AbstractMethodNamesTest {

        @Test
        @DisplayName("BankAccount → [canDebit, fee]")
        void bankAccount() {
            assertThat(tasks.abstractMethodNames(BankAccount.class)).containsExactly("canDebit", "fee");
        }

        @Test
        @DisplayName("a concrete subclass declares no abstract methods → []")
        void concreteClass() {
            assertThat(tasks.abstractMethodNames(SavingsAccount.class)).isEmpty();
        }

        @Test
        @DisplayName("JDK: Number → [doubleValue, floatValue, intValue, longValue]")
        void number() {
            assertThat(tasks.abstractMethodNames(Number.class))
                    .containsExactly("doubleValue", "floatValue", "intValue", "longValue");
        }

        @Test
        @DisplayName("String → []")
        void string() {
            assertThat(tasks.abstractMethodNames(String.class)).isEmpty();
        }

        @Test
        @DisplayName("only DECLARED methods count: inherited abstract methods are ignored")
        void inheritedNotCounted() {
            assertThat(tasks.abstractMethodNames(CheckingAccount.class)).isEmpty();
        }
    }
}
