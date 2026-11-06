package com.vbforge.practice.practice_10_get_versus_idiomatic_style;

import com.vbforge.practice.practice_5_user_sample_data.User;
import com.vbforge.practice.practice_5_user_sample_data.UserSampleData;

import java.util.Optional;

/** isPresent() + get() works but is verbose and fragile; map + ifPresent says the same thing safely. */
public class IsPresentGetVersusIdiomaticDemo {

    public static void main(String[] args) {
        Optional<User> user = UserSampleData.findUserById("U001");

        // Anti-pattern (fragile, verbose):
        if (user.isPresent()) {
            System.out.println("Anti-pattern: " + user.get().name());
        }

        // Idiomatic (safe, expressive):
        user.map(User::name).ifPresent(n -> System.out.println("Idiomatic:    " + n));
    }
}
