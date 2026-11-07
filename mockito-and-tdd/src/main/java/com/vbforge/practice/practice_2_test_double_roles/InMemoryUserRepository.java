package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.User;
import com.vbforge.practice.practice_1_registration_sample_domain.UserRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** FAKE: a real, working, simplified implementation (a map instead of a database). */
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> byEmail = new LinkedHashMap<String, User>();

    @Override public void save(User user) { byEmail.put(user.getEmail(), user); }
    @Override public boolean existsByEmail(String email) { return byEmail.containsKey(email); }
    @Override public List<User> findAll() { return new ArrayList<User>(byEmail.values()); }
}
