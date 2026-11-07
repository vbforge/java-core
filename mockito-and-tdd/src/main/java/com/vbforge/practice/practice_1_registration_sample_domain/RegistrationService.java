package com.vbforge.practice.practice_1_registration_sample_domain;

/**
 * System under test for the test-double demos.
 *
 * register: check first, then act (save, then welcome e-mail).
 * A duplicate e-mail saves nothing and sends nothing.
 */
public class RegistrationService {

    private final UserRepository repository;
    private final EmailSender sender;

    public RegistrationService(UserRepository repository, EmailSender sender) {
        this.repository = repository;
        this.sender = sender;
    }

    /** @return true when registered, false when the e-mail is already taken */
    public boolean register(String name, String email) {
        if (repository.existsByEmail(email)) {
            return false;
        }
        repository.save(new User(name, email));
        sender.send(email, "Welcome", "Hi " + name + "!");
        return true;
    }

    public int countUsers() {
        return repository.findAll().size();
    }
}
