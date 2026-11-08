package com.vbforge.practice.practice_4_exception_testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Technique: testing exceptions three ways + failure atomicity.
 * 1) assertThatThrownBy      : most flexible, chains type / message / cause
 * 2) assertThatExceptionOfType / assertThatIllegalArgumentException : typed, reads like a sentence
 * 3) JUnit assertThrows      : returns the exception so you can inspect it by hand
 */
class BankAccountExceptionTest {

    @Test
    void withdraw_moreThanBalance_throwsWithExactMessage() {
        BankAccount account = new BankAccount(1_000);

        assertThatThrownBy(() -> account.withdraw(5_000))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("requested 5000 but only 1000 available");
    }

    @Test
    void withdraw_moreThanBalance_exceptionCarriesData() {
        BankAccount account = new BankAccount(1_000);

        assertThatExceptionOfType(InsufficientFundsException.class)
                .isThrownBy(() -> account.withdraw(5_000))
                .satisfies(e -> {
                    assertThat(e.getRequested()).isEqualTo(5_000);
                    assertThat(e.getAvailable()).isEqualTo(1_000);
                });
    }

    @Test
    void withdraw_withJUnitAssertThrows_returnsTheException() {
        BankAccount account = new BankAccount(100);

        InsufficientFundsException e = assertThrows(InsufficientFundsException.class, () -> account.withdraw(101));

        assertThat(e.getAvailable()).isEqualTo(100);
    }

    @Test
    @DisplayName("a failed withdraw leaves the balance unchanged (failure atomicity)")
    void withdraw_failure_doesNotChangeState() {
        BankAccount account = new BankAccount(100);

        assertThatThrownBy(() -> account.withdraw(500)).isInstanceOf(InsufficientFundsException.class);

        assertThat(account.balance()).isEqualTo(100);
    }

    @Test
    void transfer_whenSourceHasNoMoney_targetStaysUntouched() {
        BankAccount source = new BankAccount(50);
        BankAccount target = new BankAccount(10);

        assertThatThrownBy(() -> source.transferTo(target, 80))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(source.balance()).isEqualTo(50);
        assertThat(target.balance()).isEqualTo(10);
    }

    @ParameterizedTest(name = "withdraw({0}) is rejected as non-positive")
    @ValueSource(longs = {0, -1, -1_000})
    void withdraw_nonPositiveAmount_throwsIllegalArgument(long amount) {
        BankAccount account = new BankAccount(100);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> account.withdraw(amount))
                .withMessageContaining(String.valueOf(amount));
    }

    @Test
    void closedAccount_rejectsEveryOperation() {
        BankAccount account = new BankAccount(100);
        account.close();

        assertThatThrownBy(() -> account.deposit(1)).isInstanceOf(IllegalStateException.class).hasMessage("account is closed");
        assertThatThrownBy(() -> account.withdraw(1)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void constructor_negativeOpeningBalance_throws() {
        assertThatIllegalArgumentException().isThrownBy(() -> new BankAccount(-1));
    }

    @Test
    void transfer_nullTarget_throwsNpeWithParameterName() {
        BankAccount account = new BankAccount(10);

        assertThatThrownBy(() -> account.transferTo(null, 1))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("target");
    }

    // ----- causes -----

    @Test
    void parseToCents_garbage_wrapsOriginalExceptionAsCause() {
        assertThatThrownBy(() -> BankAccount.parseToCents("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("not a valid amount: 'abc'")
                .hasCauseInstanceOf(NumberFormatException.class);
    }

    @Test
    void parseToCents_tooManyDecimals_failsWithArithmeticCause() {
        assertThatThrownBy(() -> BankAccount.parseToCents("1.005"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasRootCauseInstanceOf(ArithmeticException.class);
    }

    // ----- the happy path: "no exception" is also a behaviour -----

    @Test
    void withdraw_exactBalance_doesNotThrow() {
        BankAccount account = new BankAccount(100);

        assertThatCode(() -> account.withdraw(100)).doesNotThrowAnyException();
        assertDoesNotThrow(() -> account.deposit(5));   // JUnit flavour of the same idea
        assertThat(account.balance()).isEqualTo(5);
    }

    @Test
    void parseToCents_validInput_returnsCents() {
        assertThat(BankAccount.parseToCents(" 12.34 ")).isEqualTo(1234);
        assertThat(BankAccount.parseToCents("7")).isEqualTo(700);
    }

    @Test
    void deposit_positiveAmount_increasesBalance() {
        BankAccount account = new BankAccount(100);

        account.deposit(50);

        assertThat(account.balance()).isEqualTo(150);
    }

    @Test
    void transfer_movesMoneyBetweenAccounts() {
        BankAccount source = new BankAccount(100);
        BankAccount target = new BankAccount(0);

        source.transferTo(target, 40);

        assertThat(source.balance()).isEqualTo(60);
        assertThat(target.balance()).isEqualTo(40);
    }
}
