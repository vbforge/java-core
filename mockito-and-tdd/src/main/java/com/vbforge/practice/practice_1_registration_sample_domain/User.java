package com.vbforge.practice.practice_1_registration_sample_domain;

import java.util.Objects;

/** Immutable user: name + e-mail. Shared fixture for the test-double demos. */
public final class User {

    private final String name;
    private final String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return Objects.equals(name, other.name) && Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() { return Objects.hash(name, email); }

    @Override
    public String toString() { return name + " <" + email + ">"; }
}
