package com.vbforge.practice.practice_5_user_sample_data;

import java.util.List;
import java.util.Optional;

/**
 * Shared fixture, not a concept (no main): imported by the user demos in practice_6, 7 and 8.
 *
 * Ids: U001 = Alice (has an email), U002 = Bob (email unknown, null), anything else = no user.
 */
public final class UserSampleData {

    private UserSampleData() {
    }

    public static final List<String> IDS = List.of("U001", "U002", "U999");

    /** Optional as a return type: a user might not exist. */
    public static Optional<User> findUserById(String id) {
        if ("U001".equals(id)) return Optional.of(new User("U001", "Alice", "alice@example.com"));
        if ("U002".equals(id)) return Optional.of(new User("U002", "Bob", null)); // email unknown
        return Optional.empty();
    }

    /** The email may be null, so it is wrapped with ofNullable. */
    public static Optional<String> emailOf(User user) {
        return Optional.ofNullable(user.email());
    }
}
