package com.vbforge.practice.practice_3_state_vs_interaction_verification;

import com.vbforge.practice.practice_1_registration_sample_domain.*;
import com.vbforge.practice.practice_2_test_double_roles.InMemoryUserRepository;
import com.vbforge.practice.practice_2_test_double_roles.RecordingEmailSender;
import java.util.ArrayList;
import java.util.List;

/**
 * Two ways to verify the same requirement ("a repeated e-mail is stored once"):
 *   - STATE:       look at what ended up in the fake repository
 *   - INTERACTION: look at which calls the code made on a collaborator
 *
 * The code is then refactored to detect duplicates by scanning findAll() instead of
 * calling existsByEmail(). The behaviour is identical, so the state check still passes,
 * but the interaction check on existsByEmail() breaks.
 */
public class StateVsInteractionDemo {

    /** Delegating spy: real fake behaviour, plus a record of the method names called. */
    static class RecordingUserRepository implements UserRepository {
        private final InMemoryUserRepository delegate = new InMemoryUserRepository();
        private final List<String> methodCalls = new ArrayList<String>();

        @Override public void save(User user) { methodCalls.add("save"); delegate.save(user); }
        @Override public boolean existsByEmail(String email) { methodCalls.add("existsByEmail"); return delegate.existsByEmail(email); }
        @Override public List<User> findAll() { methodCalls.add("findAll"); return delegate.findAll(); }

        int timesCalled(String method) {
            int n = 0;
            for (String m : methodCalls) {
                if (m.equals(method)) n++;
            }
            return n;
        }
    }

    /** Refactored implementation: same behaviour, different calls. */
    static class ScanningRegistrationService {
        private final UserRepository repository;
        private final EmailSender sender;

        ScanningRegistrationService(UserRepository repository, EmailSender sender) {
            this.repository = repository;
            this.sender = sender;
        }

        boolean register(String name, String email) {
            for (User existing : repository.findAll()) {
                if (existing.getEmail().equals(email)) {
                    return false;
                }
            }
            repository.save(new User(name, email));
            sender.send(email, "Welcome", "Hi " + name + "!");
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("--- original implementation (uses existsByEmail) ---");
        RecordingUserRepository repo1 = new RecordingUserRepository();
        RegistrationService original = new RegistrationService(repo1, new RecordingEmailSender());
        original.register("Ann", "ann@example.com");
        original.register("Ann", "ann@example.com");
        report(repo1);

        System.out.println("--- refactored implementation (scans findAll) ---");
        RecordingUserRepository repo2 = new RecordingUserRepository();
        ScanningRegistrationService refactored = new ScanningRegistrationService(repo2, new RecordingEmailSender());
        refactored.register("Ann", "ann@example.com");
        refactored.register("Ann", "ann@example.com");
        report(repo2);
    }

    private static void report(RecordingUserRepository repo) {
        boolean state = repo.delegate.findAll().size() == 1;
        boolean interaction = repo.timesCalled("existsByEmail") == 2;
        System.out.println("  state check (one user stored):                  " + (state ? "PASS" : "FAIL"));
        System.out.println("  interaction check (existsByEmail called twice): " + (interaction ? "PASS" : "FAIL")
                + " (was " + repo.timesCalled("existsByEmail") + ")");
    }
}
