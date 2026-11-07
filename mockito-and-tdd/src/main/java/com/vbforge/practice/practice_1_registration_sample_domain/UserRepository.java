package com.vbforge.practice.practice_1_registration_sample_domain;

import java.util.List;

/** Collaborator of RegistrationService: where users are stored. */
public interface UserRepository {
    void save(User user);
    boolean existsByEmail(String email);
    List<User> findAll();
}
