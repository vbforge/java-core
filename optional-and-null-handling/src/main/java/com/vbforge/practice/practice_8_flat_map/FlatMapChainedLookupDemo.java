package com.vbforge.practice.practice_8_flat_map;

import com.vbforge.practice.practice_5_user_sample_data.User;
import com.vbforge.practice.practice_5_user_sample_data.UserSampleData;

import java.util.Optional;

/** flatMap chains two Optional-returning steps: user -> email -> domain. */
public class FlatMapChainedLookupDemo {

    public static void main(String[] args) {
        Optional<User> alice = UserSampleData.findUserById("U001");
        Optional<User> bob = UserSampleData.findUserById("U002");

        String aliceEmail = alice.flatMap(UserSampleData::emailOf).orElse("no email");
        String bobEmail = bob.flatMap(UserSampleData::emailOf).orElse("no email");
        System.out.println("Alice email: " + aliceEmail);
        System.out.println("Bob email:   " + bobEmail + "  <- null wrapped by ofNullable");

        // flatMap then map: the domain part of the email
        String aliceDomain = alice
                .flatMap(UserSampleData::emailOf)
                .map(email -> email.split("@")[1])
                .orElse("unknown");
        String bobDomain = bob
                .flatMap(UserSampleData::emailOf)
                .map(email -> email.split("@")[1])
                .orElse("unknown");
        System.out.println("Alice domain: " + aliceDomain);
        System.out.println("Bob domain:   " + bobDomain);
    }
}
