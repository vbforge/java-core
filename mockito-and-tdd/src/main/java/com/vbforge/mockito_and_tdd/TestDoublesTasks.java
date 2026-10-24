package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.TestDoublesDemo.EmailSender;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.User;
import com.vbforge.mockito_and_tdd.TestDoublesDemo.UserRepository;

/**
 * TestDoublesTasks — implement every method below to make {@code TestDoublesTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code TestDoublesDemo.java} to understand the five kinds of test double.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=TestDoublesTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * The collaborator types ({@link UserRepository}, {@link EmailSender}, {@link User})
 * live in {@code TestDoublesDemo}. The tests drive your code with the hand-written
 * doubles from that file — no Mockito yet.
 */
public class TestDoublesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Registers a new user: stores them and sends a welcome email.
     * If the email address is already registered, nothing is stored or sent.
     *
     * <p>Examples:
     * <pre>
     *   registerUser(repo, sender, "Ann", "ann@x.com")
     *       → saves User("Ann", "ann@x.com", active=true)
     *       → sends email: to="ann@x.com", subject="Welcome", body="Hello, Ann!"
     *       → returns the saved user
     *
     *   registerUser(repo, sender, "Ann", "ann@x.com")   // a second time
     *       → throws IllegalStateException("already registered: ann@x.com")
     *         (no save, no email)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Ask the repository whether the email exists <em>before</em> doing any work.</li>
     *   <li>The duplicate check must happen before the email is sent — a dummy sender in
     *       the tests will fail loudly if you get the order wrong.</li>
     * </ul>
     *
     * @param repository  where users are stored
     * @param emailSender where the welcome email goes
     * @param name        the new user's display name
     * @param email       the new user's (unique) email address
     * @return the user that was saved
     * @throws IllegalStateException if {@code email} is already registered
     */
    public User registerUser(UserRepository repository, EmailSender emailSender,
                             String name, String email) {
        throw new UnsupportedOperationException("TODO: implement registerUser");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Counts how many stored users are active.
     *
     * <p>Examples:
     * <pre>
     *   users = [Ann(active), Bob(inactive), Cid(active)]  → 2
     *   users = [Bob(inactive)]                            → 0
     *   users = []                                         → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link UserRepository#findAll()} gives you everything; filter it yourself.</li>
     *   <li>This method never sends email — the tests may hand you a repository stub.</li>
     * </ul>
     *
     * @param repository where users are stored
     * @return the number of users for whom {@code isActive()} is true
     */
    public int countActiveUsers(UserRepository repository) {
        throw new UnsupportedOperationException("TODO: implement countActiveUsers");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Sends the same newsletter to every <em>active</em> user, in the order the
     * repository returns them, and reports how many emails were sent.
     * Inactive users are skipped.
     *
     * <p>Examples:
     * <pre>
     *   users = [Ann(active), Bob(inactive), Cid(active)], subject="News", body="Hi all"
     *       → emails to Ann then Cid, each with subject "News" and body "Hi all"
     *       → returns 2
     *
     *   users = []  → no emails, returns 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One {@link EmailSender#send} call per recipient — the subject and body are
     *       passed through unchanged.</li>
     *   <li>The tests check the calls with a spy that records them in order.</li>
     * </ul>
     *
     * @param repository  where users are stored
     * @param emailSender where the emails go
     * @param subject     the subject line, used for every email
     * @param body        the body text, used for every email
     * @return the number of emails sent
     */
    public int sendNewsletter(UserRepository repository, EmailSender emailSender,
                              String subject, String body) {
        throw new UnsupportedOperationException("TODO: implement sendNewsletter");
    }
}
