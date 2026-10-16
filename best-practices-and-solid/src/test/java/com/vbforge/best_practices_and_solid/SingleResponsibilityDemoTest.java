package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.SingleResponsibilityDemo.InMemoryUserStore;
import com.vbforge.best_practices_and_solid.SingleResponsibilityDemo.PasswordHasher;
import com.vbforge.best_practices_and_solid.SingleResponsibilityDemo.RegistrationService;
import com.vbforge.best_practices_and_solid.SingleResponsibilityDemo.RegistrationValidator;
import com.vbforge.best_practices_and_solid.SingleResponsibilityDemo.UserRegistrationGodClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("SingleResponsibilityDemo — API contracts")
class SingleResponsibilityDemoTest {

    @Nested
    @DisplayName("1. A class with too many reasons to change")
    class GodClassSection {

        @Test
        @DisplayName("valid input is validated, hashed, saved and e-mailed in a single call")
        void validRegistrationDoesEverything() {
            UserRegistrationGodClass service = new UserRegistrationGodClass();

            boolean registered = service.register("alice", "alice@example.com", "password1");

            assertThat(registered).isTrue();
            assertThat(service.getLog()).containsExactly(
                    "saved alice [1drowssap]",
                    "emailed alice@example.com: Welcome, alice!");
        }

        @Test
        @DisplayName("stops at the first problem because validation is tangled with the flow")
        void stopsAtFirstProblem() {
            UserRegistrationGodClass service = new UserRegistrationGodClass();

            boolean registered = service.register("", "not-an-email", "short");

            assertThat(registered).isFalse();
            assertThat(service.getLog()).containsExactly("rejected: username is blank");
        }

        @Test
        @DisplayName("the log is exposed read-only")
        void logIsReadOnly() {
            UserRegistrationGodClass service = new UserRegistrationGodClass();

            assertThatThrownBy(() -> service.getLog().add("tampering"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }


    @Nested
    @DisplayName("2. One responsibility per class")
    class SplitSection {

        @Test
        @DisplayName("the validator reports every problem at once")
        void validatorReportsAllProblems() {
            List<String> errors = new RegistrationValidator().validate("", "nope", "short");

            assertThat(errors).containsExactly(
                    "username is blank", "email is invalid", "password is too short");
        }

        @Test
        @DisplayName("the validator returns an empty list for valid input")
        void validatorAcceptsValidInput() {
            assertThat(new RegistrationValidator().validate("alice", "alice@example.com", "password1")).isEmpty();
        }

        @Test
        @DisplayName("invalid input never reaches the store or the notifier")
        void invalidInputTouchesNothing() {
            List<String> notifications = new ArrayList<>();
            InMemoryUserStore store = new InMemoryUserStore();
            RegistrationService service = new RegistrationService(
                    new RegistrationValidator(), new PasswordHasher(), store,
                    (email, username) -> notifications.add(email));

            List<String> errors = service.register("alice", "nope", "password1");

            assertThat(errors).containsExactly("email is invalid");
            assertThat(store.asMap()).isEmpty();
            assertThat(notifications).isEmpty();
        }

        @Test
        @DisplayName("valid input is stored with a hashed password and the user is notified")
        void validInputIsStoredAndNotified() {
            List<String> notifications = new ArrayList<>();
            InMemoryUserStore store = new InMemoryUserStore();
            RegistrationService service = new RegistrationService(
                    new RegistrationValidator(), new PasswordHasher(), store,
                    (email, username) -> notifications.add(email + "/" + username));

            List<String> errors = service.register("alice", "alice@example.com", "password1");

            assertThat(errors).isEmpty();
            assertThat(store.asMap()).containsEntry("alice", "1drowssap").hasSize(1);
            assertThat(notifications).containsExactly("alice@example.com/alice");
        }

        @Test
        @DisplayName("every collaborator can be replaced by a lambda")
        void collaboratorsAreReplaceable() {
            List<String> saved = new ArrayList<>();
            RegistrationService service = new RegistrationService(
                    new RegistrationValidator(), new PasswordHasher(),
                    (username, hash) -> saved.add(username + ":" + hash),
                    (email, username) -> { });

            service.register("bob", "bob@example.com", "password1");

            assertThat(saved).containsExactly("bob:1drowssap");
        }
    }


    @Nested
    @DisplayName("3. SRP at method level")
    class MethodLevelSection {

        @Test
        @DisplayName("total only calculates")
        void totalCalculates() {
            assertThat(SingleResponsibilityDemo.total(Arrays.asList(2.5, 4.0))).isEqualTo(6.5);
            assertThat(SingleResponsibilityDemo.total(Collections.<Double>emptyList())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("formatTotal only formats, with a locale-independent decimal point")
        void formatTotalFormats() {
            assertThat(SingleResponsibilityDemo.formatTotal(6.5)).isEqualTo("Total: 6.50");
            assertThat(SingleResponsibilityDemo.formatTotal(1234.5)).isEqualTo("Total: 1234.50");
        }

        @Test
        @DisplayName("the mixed method prints the same text, but returns nothing to assert on")
        void mixedMethodOnlyPrints() {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(captured, true));
                SingleResponsibilityDemo.printTotalMixed(Arrays.asList(2.5, 4.0));
            } finally {
                System.setOut(original);
            }

            assertThat(captured.toString().trim()).isEqualTo(
                    SingleResponsibilityDemo.formatTotal(SingleResponsibilityDemo.total(Arrays.asList(2.5, 4.0))));
        }
    }
}
