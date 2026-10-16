package com.vbforge.best_practices_and_solid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <b>S — Single Responsibility Principle (SRP).</b>
 *
 * <p>"A class should have only one reason to change." A <i>reason to change</i> is a
 * stakeholder or a rule that can force an edit: the validation rules, the storage
 * format, the e-mail wording, the report layout…
 *
 * <p>Sections:
 * <ol>
 *   <li>A "god class" with four reasons to change</li>
 *   <li>The same behaviour split by responsibility</li>
 *   <li>SRP at method level: calculate, format and print are three jobs</li>
 * </ol>
 */
public class SingleResponsibilityDemo {

    // ── 1. A class with too many reasons to change ────────────────────────────
    /**
     * What it is: one class that validates, hashes, stores and notifies.
     *
     * <p>Why it hurts:
     * <ul>
     *   <li>A new password rule, a new database and a new e-mail text all edit the
     *       <b>same</b> method — merge conflicts and accidental breakage</li>
     *   <li>You cannot test validation without also "storing" and "e-mailing"</li>
     *   <li>You cannot reuse the hashing anywhere else</li>
     * </ul>
     *
     * <p>Gotcha: it works perfectly today. SRP violations are a <b>maintenance</b>
     * problem, not a correctness problem — that is why they survive code review.
     */
    public static class UserRegistrationGodClass {

        private final List<String> log = new ArrayList<>();

        public List<String> getLog() {
            return Collections.unmodifiableList(log);
        }

        public boolean register(String username, String email, String password) {
            // reason to change #1: validation rules
            if (username == null || username.trim().isEmpty()) {
                log.add("rejected: username is blank");
                return false;
            }
            if (email == null || !email.contains("@")) {
                log.add("rejected: email is invalid");
                return false;
            }
            if (password == null || password.length() < 8) {
                log.add("rejected: password is too short");
                return false;
            }
            // reason to change #2: hashing algorithm (toy "hash" — never do this for real!)
            String hash = new StringBuilder(password).reverse().toString();
            // reason to change #3: where users are stored
            log.add("saved " + username + " [" + hash + "]");
            // reason to change #4: the notification channel and wording
            log.add("emailed " + email + ": Welcome, " + username + "!");
            return true;
        }
    }


    // ── 2. One responsibility per class ───────────────────────────────────────
    /**
     * What it is: every reason to change now lives in exactly one small class, and
     * {@link RegistrationService} only <i>coordinates</i> them.
     *
     * <p>Why it matters:
     * <ul>
     *   <li>Each class is tested alone (validator needs no store, store needs no mailer)</li>
     *   <li>Changing the e-mail wording touches one class and cannot break validation</li>
     *   <li>The validator can now report <b>all</b> problems at once — the god class
     *       stopped at the first one because validation was tangled with the flow</li>
     * </ul>
     *
     * <p>Cost: more classes. SRP does <b>not</b> mean "one method per class" — group
     * things that change <i>together</i>, split things that change <i>for different reasons</i>.
     */
    public static class RegistrationValidator {
        public List<String> validate(String username, String email, String password) {
            List<String> errors = new ArrayList<>();
            if (username == null || username.trim().isEmpty()) {
                errors.add("username is blank");
            }
            if (email == null || !email.contains("@")) {
                errors.add("email is invalid");
            }
            if (password == null || password.length() < 8) {
                errors.add("password is too short");
            }
            return errors;
        }
    }

    public static class PasswordHasher {
        public String hash(String password) {
            return new StringBuilder(password).reverse().toString();   // toy "hash"
        }
    }

    public interface UserStore {
        void save(String username, String passwordHash);
    }

    public interface WelcomeNotifier {
        void sendWelcome(String email, String username);
    }

    public static class InMemoryUserStore implements UserStore {
        private final Map<String, String> users = new LinkedHashMap<>();

        @Override
        public void save(String username, String passwordHash) {
            users.put(username, passwordHash);
        }

        public Map<String, String> asMap() {
            return Collections.unmodifiableMap(users);
        }
    }

    public static class RegistrationService {
        private final RegistrationValidator validator;
        private final PasswordHasher hasher;
        private final UserStore store;
        private final WelcomeNotifier notifier;

        public RegistrationService(RegistrationValidator validator, PasswordHasher hasher,
                                   UserStore store, WelcomeNotifier notifier) {
            this.validator = validator;
            this.hasher = hasher;
            this.store = store;
            this.notifier = notifier;
        }

        /** @return the list of validation errors — empty means the user was registered */
        public List<String> register(String username, String email, String password) {
            List<String> errors = validator.validate(username, email, password);
            if (!errors.isEmpty()) {
                return errors;
            }
            store.save(username, hasher.hash(password));
            notifier.sendWelcome(email, username);
            return Collections.emptyList();
        }
    }


    // ── 3. SRP at method level ────────────────────────────────────────────────
    /**
     * What it is: {@link #printTotalMixed} calculates, formats <i>and</i> prints.
     * The pure trio {@link #total}, {@link #formatTotal} and "print it yourself"
     * does the same job in three independently testable pieces.
     *
     * <p>Why it matters: a method that returns a value can be asserted in a test;
     * a method that writes to {@code System.out} can only be eyeballed.
     *
     * <p>Complexity: {@code total} is O(n) time, O(1) space — SRP costs nothing at runtime.
     *
     * <p>Gotcha: {@code String.format} uses the default locale — {@code Locale.ROOT}
     * keeps "12.50" from becoming "12,50" on another machine (and in CI).
     */
    public static void printTotalMixed(List<Double> prices) {
        double sum = 0;
        for (double price : prices) {
            sum += price;
        }
        System.out.println("Total: " + String.format(Locale.ROOT, "%.2f", sum));
    }

    public static double total(List<Double> prices) {
        double sum = 0;
        for (double price : prices) {
            sum += price;
        }
        return sum;
    }

    public static String formatTotal(double total) {
        return String.format(Locale.ROOT, "Total: %.2f", total);
    }


    // ── Demo runners ──────────────────────────────────────────────────────────
    static void godClassDemo() {
        UserRegistrationGodClass service = new UserRegistrationGodClass();
        service.register("alice", "alice@example.com", "password1");
        service.register("", "bob@example.com", "short");
        service.getLog().forEach(System.out::println);
    }

    static void splitResponsibilitiesDemo() {
        InMemoryUserStore store = new InMemoryUserStore();
        RegistrationService service = new RegistrationService(
                new RegistrationValidator(), new PasswordHasher(), store,
                (email, username) -> System.out.println("emailed " + email + ": Welcome, " + username + "!"));

        System.out.println("errors for bad input : " + service.register("", "nope", "short"));
        System.out.println("errors for good input: " + service.register("alice", "alice@example.com", "password1"));
        System.out.println("stored users         : " + store.asMap());
    }

    static void pureFunctionsDemo() {
        List<Double> prices = java.util.Arrays.asList(2.5, 4.0);
        printTotalMixed(prices);                          // one blob: cannot be asserted
        System.out.println(formatTotal(total(prices)));   // two pure steps + the caller decides to print
    }

    public static void main(String[] args) {
        godClassDemo();
        System.out.println("---");
        splitResponsibilitiesDemo();
        System.out.println("---");
        pureFunctionsDemo();
    }
}
