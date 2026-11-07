package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.User;
import com.vbforge.practice.practice_1_registration_sample_domain.UserRepository;
import java.util.ArrayList;
import java.util.List;

/** STUB: returns canned answers, ignores writes, remembers nothing. */
public class StubUserRepository implements UserRepository {

    private final boolean emailTaken;
    private final List<User> canned;

    public StubUserRepository(boolean emailTaken, List<User> canned) {
        this.emailTaken = emailTaken;
        this.canned = new ArrayList<User>(canned);
    }

    @Override public void save(User user) { /* ignored */ }
    @Override public boolean existsByEmail(String email) { return emailTaken; }
    @Override public List<User> findAll() { return canned; }
}
