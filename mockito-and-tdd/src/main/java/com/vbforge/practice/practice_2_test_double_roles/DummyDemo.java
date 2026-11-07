package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.*;
import com.vbforge.practice.practice_2_test_double_roles.DummyEmailSender;
import com.vbforge.practice.practice_2_test_double_roles.StubUserRepository;

import java.util.Collections;

/** Dummy: a parameter that must be there but is never used on this path. */
public class DummyDemo {

    public static void main(String[] args) {
        // the e-mail is already taken, so register() returns before it reaches the sender
        UserRepository taken = new StubUserRepository(true, Collections.<User>emptyList());
        RegistrationService service = new RegistrationService(taken, new DummyEmailSender());

        boolean registered = service.register("Ann", "ann@example.com");

        System.out.println("registered = " + registered);
        System.out.println("the dummy sender was never touched (it would have thrown AssertionError)");
    }
}
