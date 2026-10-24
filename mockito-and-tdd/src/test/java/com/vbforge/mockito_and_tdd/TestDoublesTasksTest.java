package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.TestDoublesDemo.DummyEmailSender;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.FakeUserRepository;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.SentEmail;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.SpyEmailSender;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.StubUserRepository;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TestDoublesTasksTest — tests for every method in {@link TestDoublesTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code TestDoublesTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=TestDoublesTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 3 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("TestDoublesTasks — your implementations")
class TestDoublesTasksTest {

    private TestDoublesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TestDoublesTasks();
    }

    private static User active(String name, String email) {
        return new User(name, email, true);
    }

    private static User inactive(String name, String email) {
        return new User(name, email, false);
    }

    // ── Task 1: registerUser ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement registerUser()")
    @DisplayName("Task 1 — registerUser()")
    class RegisterUserTest {

        @Test
        @DisplayName("saves an active user and sends the welcome email")
        void happyPath() {
            FakeUserRepository repo = new FakeUserRepository();
            SpyEmailSender spy = new SpyEmailSender();

            User result = tasks.registerUser(repo, spy, "Ann", "ann@x.com");

            assertThat(result).isEqualTo(active("Ann", "ann@x.com"));
            assertThat(repo.findByEmail("ann@x.com")).contains(result);
            assertThat(spy.sent()).containsExactly(
                    new SentEmail("ann@x.com", "Welcome", "Hello, Ann!"));
        }

        @Test
        @DisplayName("duplicate email → IllegalStateException with the email in the message")
        void duplicateEmail() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(active("Ann", "ann@x.com"));

            assertThatThrownBy(() -> tasks.registerUser(repo, new SpyEmailSender(), "Ann2", "ann@x.com"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("already registered: ann@x.com");
        }

        @Test
        @DisplayName("duplicate email → nothing saved, nothing sent")
        void duplicateChangesNothing() {
            FakeUserRepository repo = new FakeUserRepository();
            User original = active("Ann", "ann@x.com");
            repo.save(original);
            SpyEmailSender spy = new SpyEmailSender();

            assertThatThrownBy(() -> tasks.registerUser(repo, spy, "Impostor", "ann@x.com"))
                    .isInstanceOf(IllegalStateException.class);

            assertThat(repo.findAll()).containsExactly(original);
            assertThat(spy.callCount()).isZero();
        }

        @Test
        @DisplayName("duplicate check happens before any email (a dummy sender is never touched)")
        void checkBeforeSending() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(active("Ann", "ann@x.com"));

            assertThatThrownBy(() -> tasks.registerUser(repo, new DummyEmailSender(), "Ann", "ann@x.com"))
                    .isInstanceOf(IllegalStateException.class);   // not AssertionError from the dummy
        }

        @Test
        @DisplayName("different emails register independently")
        void twoUsers() {
            FakeUserRepository repo = new FakeUserRepository();
            SpyEmailSender spy = new SpyEmailSender();

            tasks.registerUser(repo, spy, "Ann", "ann@x.com");
            tasks.registerUser(repo, spy, "Bob", "bob@x.com");

            assertThat(repo.findAll()).hasSize(2);
            assertThat(spy.callCount()).isEqualTo(2);
        }
    }

    // ── Task 2: countActiveUsers ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countActiveUsers()")
    @DisplayName("Task 2 — countActiveUsers()")
    class CountActiveUsersTest {

        @Test
        @DisplayName("[Ann(active), Bob(inactive), Cid(active)] → 2")
        void mixed() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(active("Ann", "ann@x.com"));
            repo.save(inactive("Bob", "bob@x.com"));
            repo.save(active("Cid", "cid@x.com"));

            assertThat(tasks.countActiveUsers(repo)).isEqualTo(2);
        }

        @Test
        @DisplayName("only inactive users → 0")
        void allInactive() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(inactive("Bob", "bob@x.com"));

            assertThat(tasks.countActiveUsers(repo)).isZero();
        }

        @Test
        @DisplayName("empty repository → 0")
        void empty() {
            assertThat(tasks.countActiveUsers(new FakeUserRepository())).isZero();
        }

        @Test
        @DisplayName("works with a stub that always answers with one active user → 1")
        void withStub() {
            assertThat(tasks.countActiveUsers(new StubUserRepository(active("Ann", "ann@x.com"))))
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("works with a stub holding one inactive user → 0")
        void withInactiveStub() {
            assertThat(tasks.countActiveUsers(new StubUserRepository(inactive("Bob", "bob@x.com"))))
                    .isZero();
        }
    }

    // ── Task 3: sendNewsletter ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sendNewsletter()")
    @DisplayName("Task 3 — sendNewsletter()")
    class SendNewsletterTest {

        @Test
        @DisplayName("emails active users only, in repository order, and returns the count")
        void activeOnly() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(active("Ann", "ann@x.com"));
            repo.save(inactive("Bob", "bob@x.com"));
            repo.save(active("Cid", "cid@x.com"));
            SpyEmailSender spy = new SpyEmailSender();

            int sent = tasks.sendNewsletter(repo, spy, "News", "Hi all");

            assertThat(sent).isEqualTo(2);
            assertThat(spy.sent()).containsExactly(
                    new SentEmail("ann@x.com", "News", "Hi all"),
                    new SentEmail("cid@x.com", "News", "Hi all"));
        }

        @Test
        @DisplayName("subject and body are passed through unchanged")
        void passesThrough() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(active("Ann", "ann@x.com"));
            SpyEmailSender spy = new SpyEmailSender();

            tasks.sendNewsletter(repo, spy, "Weekly #42", "Line1\nLine2");

            assertThat(spy.sent().get(0).getSubject()).isEqualTo("Weekly #42");
            assertThat(spy.sent().get(0).getBody()).isEqualTo("Line1\nLine2");
        }

        @Test
        @DisplayName("no users → 0 and nothing sent")
        void noUsers() {
            SpyEmailSender spy = new SpyEmailSender();

            assertThat(tasks.sendNewsletter(new FakeUserRepository(), spy, "News", "Hi")).isZero();
            assertThat(spy.callCount()).isZero();
        }

        @Test
        @DisplayName("only inactive users → 0 and the (dummy) sender is never touched")
        void onlyInactive() {
            FakeUserRepository repo = new FakeUserRepository();
            repo.save(inactive("Bob", "bob@x.com"));

            assertThat(tasks.sendNewsletter(repo, new DummyEmailSender(), "News", "Hi")).isZero();
        }
    }
}
