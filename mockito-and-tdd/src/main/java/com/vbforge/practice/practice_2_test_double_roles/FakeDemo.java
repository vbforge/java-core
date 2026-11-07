package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.*;
import com.vbforge.practice.practice_2_test_double_roles.InMemoryUserRepository;
import com.vbforge.practice.practice_2_test_double_roles.RecordingEmailSender;

/** Fake: a working lightweight implementation, so state behaves like the real thing. */
public class FakeDemo {

    public static void main(String[] args) {
        InMemoryUserRepository fake = new InMemoryUserRepository();
        RegistrationService service = new RegistrationService(fake, new RecordingEmailSender());

        System.out.println("register Ann       = " + service.register("Ann", "ann@example.com"));
        System.out.println("register Bob       = " + service.register("Bob", "bob@example.com"));
        System.out.println("register Ann again = " + service.register("Ann", "ann@example.com"));
        System.out.println("countUsers         = " + service.countUsers());              // 2
        System.out.println("stored             = " + fake.findAll());
    }
}
