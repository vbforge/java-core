package com.vbforge.exception_handling;

import com.vbforge.exception_handling.bank.Account;
import com.vbforge.exception_handling.bank.AccountServiceException;
import com.vbforge.exception_handling.bank.InsufficientFundsException;
import com.vbforge.exception_handling.bank.InvalidAmountException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CustomExceptionsTasks — your implementations")
class CustomExceptionsTasksTest {

    private CustomExceptionsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CustomExceptionsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement shortfall()")
    @DisplayName("1. shortfall(account, amount)")
    class ShortfallTest {

        @Test
        @DisplayName("example from Javadoc: success returns 0 and the money is taken")
        void success() {
            Account account = new Account(100);

            assertThat(tasks.shortfall(account, 30)).isZero();
            assertThat(account.getBalance()).isEqualTo(70);
        }

        @Test
        @DisplayName("example from Javadoc: too little money returns the missing amount, balance unchanged")
        void insufficient() {
            Account account = new Account(100);

            assertThat(tasks.shortfall(account, 250)).isEqualTo(150);
            assertThat(account.getBalance()).isEqualTo(100);
        }

        @Test
        @DisplayName("withdrawing exactly the balance succeeds")
        void exactBalance() {
            Account account = new Account(50);

            assertThat(tasks.shortfall(account, 50)).isZero();
            assertThat(account.getBalance()).isZero();
        }

        @Test
        @DisplayName("an invalid amount is a caller bug and is NOT caught")
        void invalidAmountEscapes() {
            assertThatThrownBy(() -> tasks.shortfall(new Account(100), -1))
                    .isInstanceOf(InvalidAmountException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement transfer()")
    @DisplayName("2. transfer(from, to, amount)")
    class TransferTest {

        @Test
        @DisplayName("example from Javadoc: successful transfer moves the money")
        void success() {
            Account from = new Account(100);
            Account to = new Account(0);

            tasks.transfer(from, to, 40);

            assertThat(from.getBalance()).isEqualTo(60);
            assertThat(to.getBalance()).isEqualTo(40);
        }

        @Test
        @DisplayName("example from Javadoc: insufficient funds become AccountServiceException(\"Transfer failed\")")
        void insufficientFunds() {
            assertThatThrownBy(() -> tasks.transfer(new Account(10), new Account(0), 40))
                    .isInstanceOf(AccountServiceException.class)
                    .hasMessage("Transfer failed");
        }

        @Test
        @DisplayName("the original InsufficientFundsException is the cause")
        void causeIsKept() {
            assertThatThrownBy(() -> tasks.transfer(new Account(10), new Account(0), 40))
                    .hasCauseInstanceOf(InsufficientFundsException.class);
        }

        @Test
        @DisplayName("a failed transfer changes neither account")
        void noPartialEffect() {
            Account from = new Account(10);
            Account to = new Account(5);

            try {
                tasks.transfer(from, to, 40);
            } catch (AccountServiceException expected) {
                // expected
            }

            assertThat(from.getBalance()).isEqualTo(10);
            assertThat(to.getBalance()).isEqualTo(5);
        }

        @Test
        @DisplayName("example from Javadoc: an invalid amount escapes UNWRAPPED")
        void invalidAmountNotWrapped() {
            assertThatThrownBy(() -> tasks.transfer(new Account(100), new Account(0), -1))
                    .isInstanceOf(InvalidAmountException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement tryDeposit()")
    @DisplayName("3. tryDeposit(account, amount)")
    class TryDepositTest {

        @Test
        @DisplayName("example from Javadoc: a valid amount is deposited")
        void valid() {
            Account account = new Account(10);

            assertThat(tasks.tryDeposit(account, 5)).isTrue();
            assertThat(account.getBalance()).isEqualTo(15);
        }

        @ParameterizedTest(name = "amount {0} is rejected")
        @ValueSource(longs = {0, -3, Long.MIN_VALUE})
        @DisplayName("example from Javadoc: an invalid amount returns false and changes nothing")
        void invalid(long amount) {
            Account account = new Account(10);

            assertThat(tasks.tryDeposit(account, amount)).isFalse();
            assertThat(account.getBalance()).isEqualTo(10);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement rootCause()")
    @DisplayName("4. rootCause(throwable)")
    class RootCauseTest {

        @Test
        @DisplayName("example from Javadoc: follows the chain to the deepest cause")
        void deepChain() {
            IOException root = new IOException("c");
            Throwable top = new RuntimeException("a", new IllegalStateException("b", root));

            assertThat(tasks.rootCause(top)).isSameAs(root);
        }

        @Test
        @DisplayName("example from Javadoc: no cause means the exception is its own root")
        void noCause() {
            Throwable single = new IllegalArgumentException("x");

            assertThat(tasks.rootCause(single)).isSameAs(single);
        }

        @Test
        @DisplayName("a single cause level")
        void oneLevel() {
            IOException cause = new IOException("io");

            assertThat(tasks.rootCause(new RuntimeException(cause))).isSameAs(cause);
        }
    }
}
