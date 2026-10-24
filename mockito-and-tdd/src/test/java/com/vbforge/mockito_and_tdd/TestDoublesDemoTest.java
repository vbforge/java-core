package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.TestDoublesDemo.AccountService;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.DummyEmailSender;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.FakeUserRepository;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.MockEmailSender;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.SentEmail;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.SpyEmailSender;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.StubUserRepository;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TestDoublesDemoTest — verifies every concept shown in {@link TestDoublesDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * the five kinds of test double.
 */
@DisplayName("TestDoublesDemo — API contracts")
class TestDoublesDemoTest {

    private static final User ANN = new User("Ann", "ann@example.com", true);

    // ── 1. Dummy ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Dummy")
    class DummyTest {

        @Test
        @DisplayName("an untouched dummy is harmless")
        void unusedDummyIsHarmless() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(ANN);
            AccountService service = new AccountService(repo, new DummyEmailSender());

            assertThat(service.displayName("ann@example.com")).isEqualTo("Ann");
        }

        @Test
        @DisplayName("using the dummy fails loudly with AssertionError")
        void usedDummyThrows() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(ANN);
            AccountService service = new AccountService(repo, new DummyEmailSender());

            assertThatThrownBy(() -> service.close("ann@example.com"))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("must never be called");
        }
    }

    // ── 2. Stub ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Stub")
    class StubTest {

        @Test
        @DisplayName("returns the canned user for ANY email")
        void ignoresArguments() {
            AccountService service =
                    new AccountService(new StubUserRepository(ANN), new DummyEmailSender());

            assertThat(service.displayName("ann@example.com")).isEqualTo("Ann");
            assertThat(service.displayName("someone-else@example.org")).isEqualTo("Ann");
        }

        @Test
        @DisplayName("a stub built with null models an empty repository")
        void emptyStub() {
            AccountService service =
                    new AccountService(new StubUserRepository(null), new DummyEmailSender());

            assertThat(service.displayName("ann@example.com")).isEqualTo("<unknown>");
        }

        @Test
        @DisplayName("a stub does not remember saves")
        void stubForgetsSaves() {
            StubUserRepository stub = new StubUserRepository(null);
            stub.save(ANN);

            assertThat(stub.findAll()).isEmpty();
        }

        @Test
        @DisplayName("findAll returns just the canned user")
        void findAllReturnsCanned() {
            assertThat(new StubUserRepository(ANN).findAll()).containsExactly(ANN);
        }
    }

    // ── 3. Fake ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Fake")
    class FakeTest {

        @Test
        @DisplayName("stores and retrieves users by email")
        void storesAndRetrieves() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(ANN);

            assertThat(repo.findByEmail("ann@example.com")).contains(ANN);
            assertThat(repo.findByEmail("nobody@example.com")).isEmpty();
        }

        @Test
        @DisplayName("saving the same email replaces the user")
        void saveReplaces() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(ANN);
            repo.save(new User("Ann B.", "ann@example.com", true));

            assertThat(repo.findAll()).hasSize(1);
            assertThat(repo.findByEmail("ann@example.com").get().getName()).isEqualTo("Ann B.");
        }

        @Test
        @DisplayName("findAll keeps insertion order")
        void keepsInsertionOrder() {
            FakeUserRepository repo = new FakeUserRepository();
            User bob = new User("Bob", "bob@example.com", true);
            repo.save(bob);
            repo.save(ANN);

            assertThat(repo.findAll()).containsExactly(bob, ANN);
        }

        @Test
        @DisplayName("state verification: close() leaves the user inactive")
        void stateVerification() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(ANN);

            new AccountService(repo, new SpyEmailSender()).close("ann@example.com");

            assertThat(repo.findByEmail("ann@example.com").get().isActive()).isFalse();
            assertThat(repo.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("closing an unknown user throws IllegalArgumentException")
        void closeUnknownUser() {
            AccountService service =
                    new AccountService(new FakeUserRepository(), new DummyEmailSender());

            assertThatThrownBy(() -> service.close("ghost@example.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("unknown user: ghost@example.com");
        }
    }

    // ── 4. Spy ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Spy")
    class SpyTest {

        @Test
        @DisplayName("records exactly what was sent")
        void recordsCalls() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(ANN);
            SpyEmailSender spy = new SpyEmailSender();

            new AccountService(repo, spy).close("ann@example.com");

            assertThat(spy.callCount()).isEqualTo(1);
            assertThat(spy.sent()).containsExactly(
                    new SentEmail("ann@example.com", "Goodbye", "Your account was closed."));
        }

        @Test
        @DisplayName("a spy that was never called has an empty record")
        void neverCalled() {
            SpyEmailSender spy = new SpyEmailSender();

            assertThat(spy.callCount()).isZero();
            assertThat(spy.sent()).isEmpty();
        }

        @Test
        @DisplayName("forwards every call to its delegate")
        void forwardsToDelegate() {
            List<String> delegateSaw = new ArrayList<>();
            SpyEmailSender spy = new SpyEmailSender((to, subject, body) -> delegateSaw.add(to));

            spy.send("a@x.com", "s", "b");
            spy.send("b@x.com", "s", "b");

            assertThat(delegateSaw).containsExactly("a@x.com", "b@x.com");
            assertThat(spy.callCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("the recorded list cannot be modified by the caller")
        void recordIsReadOnly() {
            SpyEmailSender spy = new SpyEmailSender();
            spy.send("a@x.com", "s", "b");

            assertThatThrownBy(() -> spy.sent().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // ── 5. Mock (hand-rolled) ─────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Mock (hand-rolled)")
    class MockTest {

        @Test
        @DisplayName("verifyAll passes when the expected call happened")
        void expectationMet() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(ANN);
            MockEmailSender mock = new MockEmailSender().expect("ann@example.com", "Goodbye");

            new AccountService(repo, mock).close("ann@example.com");

            mock.verifyAll();   // does not throw
        }

        @Test
        @DisplayName("verifyAll fails when a different call happened")
        void expectationMissed() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(ANN);
            MockEmailSender mock = new MockEmailSender().expect("bob@example.com", "Goodbye");

            new AccountService(repo, mock).close("ann@example.com");

            assertThatThrownBy(mock::verifyAll)
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("bob@example.com <- Goodbye")
                    .hasMessageContaining("ann@example.com <- Goodbye");
        }

        @Test
        @DisplayName("verifyAll fails when an expected call never happened")
        void expectationNeverCalled() {
            MockEmailSender mock = new MockEmailSender().expect("ann@example.com", "Goodbye");

            assertThatThrownBy(mock::verifyAll).isInstanceOf(AssertionError.class);
        }

        @Test
        @DisplayName("a mock with no expectations passes if nothing was called")
        void noExpectationsNoCalls() {
            new MockEmailSender().verifyAll();   // does not throw
        }
    }
}
