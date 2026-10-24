package com.vbforge.mockito_and_tdd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * TestDoublesDemo — annotated reference for the five kinds of test double,
 * written <em>by hand</em> with no mocking framework.
 *
 * <p>Read top-to-bottom. Every double below is a tiny class you could write in
 * two minutes. Mockito automates exactly these patterns — once you can see them
 * by hand, {@code @Mock}, {@code when} and {@code verify} stop being magic.
 *
 * <p>The vocabulary (after Gerard Meszaros, <em>xUnit Test Patterns</em>):
 * <ol>
 *   <li>Dummy — fills a parameter slot; must never be used</li>
 *   <li>Stub — returns canned answers, ignores how it is called</li>
 *   <li>Fake — a real, working, but simplified implementation (in-memory)</li>
 *   <li>Spy — records how it was called (and may delegate to something real)</li>
 *   <li>Mock — pre-programmed with expectations; fails if they are not met</li>
 * </ol>
 *
 * <p>The system under test (SUT) is {@link AccountService}; its collaborators are
 * {@link UserRepository} and {@link EmailSender}. The collaborator interfaces and
 * the hand-written doubles are {@code public static} so that the {@code *Tasks}
 * and test classes can reuse them.
 */
public class TestDoublesDemo {

    public static void main(String[] args) {
        dummyDemo();
        System.out.println("---");
        stubDemo();
        System.out.println("---");
        fakeDemo();
        System.out.println("---");
        spyDemo();
        System.out.println("---");
        handRolledMockDemo();
    }

    // ── Domain: collaborators and the system under test ───────────────────────

    /** Immutable user. Equality is by value so tests can compare users directly. */
    public static final class User {
        private final String name;
        private final String email;
        private final boolean active;

        public User(String name, String email, boolean active) {
            this.name = name;
            this.email = email;
            this.active = active;
        }

        public String getName()  { return name; }
        public String getEmail() { return email; }
        public boolean isActive() { return active; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;
            User other = (User) o;
            return active == other.active && name.equals(other.name) && email.equals(other.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, email, active);
        }

        @Override
        public String toString() {
            return "User[" + name + ", " + email + (active ? ", active]" : ", inactive]");
        }
    }

    /** One recorded email — a value object, so spies can be asserted with plain equality. */
    public static final class SentEmail {
        private final String to;
        private final String subject;
        private final String body;

        public SentEmail(String to, String subject, String body) {
            this.to = to;
            this.subject = subject;
            this.body = body;
        }

        public String getTo()      { return to; }
        public String getSubject() { return subject; }
        public String getBody()    { return body; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SentEmail)) return false;
            SentEmail other = (SentEmail) o;
            return to.equals(other.to) && subject.equals(other.subject) && body.equals(other.body);
        }

        @Override
        public int hashCode() {
            return Objects.hash(to, subject, body);
        }

        @Override
        public String toString() {
            return "SentEmail[" + to + " | " + subject + " | " + body + "]";
        }
    }

    /** Collaborator #1 — persistence. */
    public interface UserRepository {
        void save(User user);
        Optional<User> findByEmail(String email);
        List<User> findAll();
    }

    /** Collaborator #2 — outbound email. */
    public interface EmailSender {
        void send(String to, String subject, String body);
    }

    /** The system under test. It only talks to its two collaborators. */
    public static class AccountService {
        private final UserRepository repository;
        private final EmailSender emailSender;

        public AccountService(UserRepository repository, EmailSender emailSender) {
            this.repository = repository;
            this.emailSender = emailSender;
        }

        /** Looks up the display name; never sends email. */
        public String displayName(String email) {
            Optional<User> user = repository.findByEmail(email);
            return user.isPresent() ? user.get().getName() : "<unknown>";
        }

        /** Marks the account inactive and says goodbye by email. */
        public void close(String email) {
            User user = repository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("unknown user: " + email));
            repository.save(new User(user.getName(), user.getEmail(), false));
            emailSender.send(email, "Goodbye", "Your account was closed.");
        }
    }

    // ── Hand-written doubles ──────────────────────────────────────────────────

    /** DUMMY: satisfies the constructor, explodes if anyone actually uses it. */
    public static class DummyEmailSender implements EmailSender {
        @Override
        public void send(String to, String subject, String body) {
            throw new AssertionError("DummyEmailSender must never be called");
        }
    }

    /**
     * STUB: answers with the same canned user whatever it is asked.
     * Pass {@code null} to model "nobody exists".
     */
    public static class StubUserRepository implements UserRepository {
        private final User canned;

        public StubUserRepository(User canned) {
            this.canned = canned;
        }

        @Override
        public void save(User user) {
            // a stub does not remember anything
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.ofNullable(canned);
        }

        @Override
        public List<User> findAll() {
            return canned == null
                    ? Collections.<User>emptyList()
                    : Collections.singletonList(canned);
        }
    }

    /** FAKE: a genuinely working repository — it just keeps everything in a map. */
    public static class FakeUserRepository implements UserRepository {
        private final Map<String, User> byEmail = new LinkedHashMap<>();

        @Override
        public void save(User user) {
            byEmail.put(user.getEmail(), user);
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.ofNullable(byEmail.get(email));
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>(byEmail.values());
        }
    }

    /** SPY: records every call, then forwards it to a delegate (a no-op by default). */
    public static class SpyEmailSender implements EmailSender {
        private final EmailSender delegate;
        private final List<SentEmail> sent = new ArrayList<>();

        public SpyEmailSender() {
            this((to, subject, body) -> { });
        }

        public SpyEmailSender(EmailSender delegate) {
            this.delegate = delegate;
        }

        @Override
        public void send(String to, String subject, String body) {
            sent.add(new SentEmail(to, subject, body));
            delegate.send(to, subject, body);
        }

        public List<SentEmail> sent() {
            return Collections.unmodifiableList(sent);
        }

        public int callCount() {
            return sent.size();
        }
    }

    /** MOCK (hand-rolled): you declare expectations up front, then call {@link #verifyAll()}. */
    public static class MockEmailSender implements EmailSender {
        private final List<String> expected = new ArrayList<>();
        private final List<String> actual = new ArrayList<>();

        public MockEmailSender expect(String to, String subject) {
            expected.add(to + " <- " + subject);
            return this;
        }

        @Override
        public void send(String to, String subject, String body) {
            actual.add(to + " <- " + subject);
        }

        public void verifyAll() {
            if (!expected.equals(actual)) {
                throw new AssertionError("expected " + expected + " but was " + actual);
            }
        }
    }

    // ── 1. Dummy ──────────────────────────────────────────────────────────────

    /**
     * A dummy is an object whose only job is to make the code compile.
     *
     * <p>WHY this matters: constructors demand all collaborators even when the
     * method under test only needs one. A dummy documents "this path must not touch
     * the email sender" — and turns a violation into an immediate failure.
     *
     * <p>Common gotcha: a dummy that quietly returns {@code null} hides bugs. Make
     * it throw so that accidental use is loud.
     */
    static void dummyDemo() {
        System.out.println("=== Dummy ===");

        FakeUserRepository repo = new FakeUserRepository();
        repo.save(new User("Ann", "ann@example.com", true));
        AccountService service = new AccountService(repo, new DummyEmailSender());

        // displayName() never emails anyone, so the dummy is never touched
        System.out.println("displayName   : " + service.displayName("ann@example.com"));  // Ann

        // close() DOES email — the dummy trips, proving the code path uses the sender
        try {
            service.close("ann@example.com");
        } catch (AssertionError e) {
            System.out.println("dummy tripped : " + e.getMessage());
        }
    }

    // ── 2. Stub ───────────────────────────────────────────────────────────────

    /**
     * A stub returns pre-baked answers so the SUT can run without real infrastructure.
     *
     * <p>WHY this matters: you want to test "what does the service do when the
     * repository says X?" without a database. A stub lets you dictate X.
     *
     * <p>Common gotcha: a stub ignores its arguments. If your test passes only because
     * the stub answers the same thing for <em>every</em> email, the test is weaker than
     * it looks — reach for a fake when the lookup key matters.
     */
    static void stubDemo() {
        System.out.println("=== Stub ===");

        AccountService knowsAnn = new AccountService(
                new StubUserRepository(new User("Ann", "ann@example.com", true)),
                new DummyEmailSender());
        System.out.println("any email     : " + knowsAnn.displayName("nobody@else.org"));  // Ann (!)

        AccountService knowsNobody = new AccountService(
                new StubUserRepository(null), new DummyEmailSender());
        System.out.println("empty stub    : " + knowsNobody.displayName("ann@example.com")); // <unknown>
    }

    // ── 3. Fake ───────────────────────────────────────────────────────────────

    /**
     * A fake has real behaviour — it just takes shortcuts (a map instead of SQL).
     *
     * <p>WHY this matters: a fake lets you assert on <em>state</em> ("the user is now
     * inactive") rather than on which methods were called. State-based tests survive
     * refactoring far better than interaction-based ones.
     *
     * <p>Time complexity of {@link FakeUserRepository}: save / findByEmail O(1),
     * findAll O(n) (it copies the values).
     *
     * <p>Common gotcha: a fake is production-grade code in your test tree — it needs
     * its own tests if it grows clever.
     */
    static void fakeDemo() {
        System.out.println("=== Fake ===");

        FakeUserRepository repo = new FakeUserRepository();
        repo.save(new User("Ann", "ann@example.com", true));
        AccountService service = new AccountService(repo, (to, subject, body) -> { });

        service.close("ann@example.com");

        // Assert on resulting STATE, not on "save() was called once"
        System.out.println("after close   : " + repo.findByEmail("ann@example.com").get());
        System.out.println("repo size     : " + repo.findAll().size());   // 1 — updated, not duplicated
    }

    // ── 4. Spy ────────────────────────────────────────────────────────────────

    /**
     * A spy records the calls made to it so you can assert on side effects that
     * leave no trace in state (an email going out, a metric being emitted).
     *
     * <p>WHY this matters: "an email was sent" is an <em>outgoing</em> effect — nothing
     * in your own objects changes. A spy turns it into data you can assert on.
     *
     * <p>Common gotcha: in Mockito the word "spy" means something narrower — a real
     * object wrapped so you can stub parts of it. See {@code MockitoBasicsDemo}.
     */
    static void spyDemo() {
        System.out.println("=== Spy ===");

        FakeUserRepository repo = new FakeUserRepository();
        repo.save(new User("Ann", "ann@example.com", true));
        SpyEmailSender spy = new SpyEmailSender();
        AccountService service = new AccountService(repo, spy);

        service.close("ann@example.com");

        System.out.println("calls         : " + spy.callCount());   // 1
        System.out.println("recorded      : " + spy.sent().get(0));
    }

    // ── 5. Mock (hand-rolled) ─────────────────────────────────────────────────

    /**
     * A mock verifies <em>behaviour</em>: the test declares which calls must happen,
     * and the mock fails the test if reality differs.
     *
     * <p>WHY this matters: this is what {@code verify(...)} does in Mockito. The
     * difference from a spy is who asserts — a spy hands data back for <em>you</em> to
     * assert on, a mock carries the expectations itself.
     *
     * <p>Common gotcha: people say "mock" for every double. In conversation that is
     * fine; when choosing a tool, remember the five roles do different jobs.
     */
    static void handRolledMockDemo() {
        System.out.println("=== Mock (hand-rolled) ===");

        // Scenario 1: expectation matches reality
        FakeUserRepository repo = new FakeUserRepository();
        repo.save(new User("Ann", "ann@example.com", true));
        MockEmailSender mock = new MockEmailSender().expect("ann@example.com", "Goodbye");
        new AccountService(repo, mock).close("ann@example.com");
        mock.verifyAll();
        System.out.println("verified      : expectation met");

        // Scenario 2: expectation does not match reality
        MockEmailSender wrong = new MockEmailSender().expect("bob@example.com", "Goodbye");
        repo.save(new User("Ann", "ann@example.com", true));
        new AccountService(repo, wrong).close("ann@example.com");
        try {
            wrong.verifyAll();
        } catch (AssertionError e) {
            System.out.println("mismatch      : " + e.getMessage());
        }
    }
}
