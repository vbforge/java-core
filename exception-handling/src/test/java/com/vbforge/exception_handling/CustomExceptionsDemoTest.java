package com.vbforge.exception_handling;

import com.vbforge.exception_handling.bank.Account;
import com.vbforge.exception_handling.bank.AccountServiceException;
import com.vbforge.exception_handling.bank.InsufficientFundsException;
import com.vbforge.exception_handling.bank.InvalidAmountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CustomExceptionsDemo — API contracts")
class CustomExceptionsDemoTest {

    @Nested
    @DisplayName("1. A checked exception carrying data")
    class CheckedWithDataTest {

        @Test
        @DisplayName("sufficient funds: money is withdrawn")
        void sufficient() {
            assertThat(CustomExceptionsDemo.withdrawDemo(new Account(100), 30)).isEqualTo("ok:70");
        }

        @Test
        @DisplayName("insufficient funds: the shortfall comes from the exception, not from text parsing")
        void insufficient() {
            assertThat(CustomExceptionsDemo.withdrawDemo(new Account(100), 250)).isEqualTo("short by 150");
        }

        @Test
        @DisplayName("exception exposes balance, requested amount and a readable message")
        void exceptionData() {
            InsufficientFundsException e = new InsufficientFundsException(100, 250);

            assertThat(e.getBalance()).isEqualTo(100);
            assertThat(e.getRequested()).isEqualTo(250);
            assertThat(e.getShortfall()).isEqualTo(150);
            assertThat(e).hasMessage("Insufficient funds: balance=100, requested=250");
        }

        @Test
        @DisplayName("InsufficientFundsException is a CHECKED exception")
        void isChecked() {
            assertThat(RuntimeException.class.isAssignableFrom(InsufficientFundsException.class)).isFalse();
            assertThat(Exception.class.isAssignableFrom(InsufficientFundsException.class)).isTrue();
        }
    }

    @Nested
    @DisplayName("2. An unchecked exception for contract violations")
    class UncheckedTest {

        @Test
        @DisplayName("a positive deposit returns the new balance")
        void validDeposit() {
            assertThat(CustomExceptionsDemo.depositDemo(new Account(100), 50)).isEqualTo(150);
        }

        @Test
        @DisplayName("a negative amount throws InvalidAmountException and exposes the amount")
        void negativeAmount() {
            assertThatThrownBy(() -> CustomExceptionsDemo.depositDemo(new Account(100), -5))
                    .isInstanceOf(InvalidAmountException.class)
                    .hasMessage("Amount must be positive: -5")
                    .extracting("amount").isEqualTo(-5L);
        }

        @Test
        @DisplayName("zero is rejected and the balance is unchanged")
        void zeroRejected() {
            Account account = new Account(100);

            assertThatThrownBy(() -> CustomExceptionsDemo.depositDemo(account, 0))
                    .isInstanceOf(InvalidAmountException.class);
            assertThat(account.getBalance()).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("3. Custom exceptions fit the standard hierarchy")
    class HierarchyTest {

        @Test
        @DisplayName("InvalidAmountException is caught by a handler for IllegalArgumentException")
        void caughtAsParent() {
            assertThat(CustomExceptionsDemo.catchAsParentDemo(-1))
                    .isEqualTo("InvalidAmountException: Amount must be positive: -1");
        }

        @Test
        @DisplayName("a valid amount is accepted")
        void accepted() {
            assertThat(CustomExceptionsDemo.catchAsParentDemo(5)).isEqualTo("accepted");
        }

        @Test
        @DisplayName("InvalidAmountException is unchecked and extends IllegalArgumentException")
        void isUnchecked() {
            assertThat(IllegalArgumentException.class.isAssignableFrom(InvalidAmountException.class)).isTrue();
            assertThat(RuntimeException.class.isAssignableFrom(InvalidAmountException.class)).isTrue();
        }
    }

    @Nested
    @DisplayName("4. Wrapping with the cause preserved")
    class WrappingTest {

        @Test
        @DisplayName("sufficient funds: the payment goes through")
        void paymentOk() {
            assertThat(CustomExceptionsDemo.payDemo(new Account(100), 40)).isEqualTo(60);
        }

        @Test
        @DisplayName("insufficient funds are translated to AccountServiceException")
        void translated() {
            assertThatThrownBy(() -> CustomExceptionsDemo.payDemo(new Account(10), 99))
                    .isInstanceOf(AccountServiceException.class)
                    .hasMessage("Payment of 99 failed");
        }

        @Test
        @DisplayName("the original InsufficientFundsException is kept as the cause, data intact")
        void causeKept() {
            Throwable thrown = null;
            try {
                CustomExceptionsDemo.payDemo(new Account(10), 99);
            } catch (AccountServiceException e) {
                thrown = e;
            }

            assertThat(thrown).isNotNull();
            assertThat(thrown.getCause()).isInstanceOf(InsufficientFundsException.class);
            assertThat(((InsufficientFundsException) thrown.getCause()).getShortfall()).isEqualTo(89);
        }
    }

    @Nested
    @DisplayName("5. Walking the cause chain")
    class CauseChainTest {

        @Test
        @DisplayName("an exception without a cause is a chain of one")
        void noCause() {
            assertThat(CustomExceptionsDemo.causeChainDemo(new IllegalStateException("x")))
                    .containsExactly("IllegalStateException");
        }

        @Test
        @DisplayName("the chain runs from the outermost exception to the root cause")
        void fullChain() {
            Throwable top = new AccountServiceException("top",
                    new IllegalStateException("middle", new IOException("root")));

            List<String> chain = CustomExceptionsDemo.causeChainDemo(top);

            assertThat(chain).containsExactly("AccountServiceException", "IllegalStateException", "IOException");
        }

        @Test
        @DisplayName("works for the exception produced by a failed payment")
        void realChain() {
            Throwable thrown = null;
            try {
                CustomExceptionsDemo.payDemo(new Account(1), 2);
            } catch (AccountServiceException e) {
                thrown = e;
            }

            assertThat(CustomExceptionsDemo.causeChainDemo(thrown))
                    .containsExactly("AccountServiceException", "InsufficientFundsException");
        }
    }

    @Nested
    @DisplayName("6. Gotcha: losing the cause")
    class LostCauseTest {

        @Test
        @DisplayName("copying only the message leaves the new exception without a cause")
        void causeLost() {
            Throwable lost = CustomExceptionsDemo.lostCauseDemo();

            assertThat(lost.getCause()).isNull();
            assertThat(lost).hasMessage("disk unreachable");
        }

        @Test
        @DisplayName("passing the original as cause keeps type and message")
        void causeKept() {
            Throwable kept = CustomExceptionsDemo.keptCauseDemo();

            assertThat(kept.getCause()).isInstanceOf(IOException.class);
            assertThat(kept.getCause()).hasMessage("disk unreachable");
            assertThat(kept).hasMessage("storage failed");
        }
    }
}
