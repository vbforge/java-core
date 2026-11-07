package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.*;
import com.vbforge.practice.practice_2_test_double_roles.ExpectingEmailSender;
import com.vbforge.practice.practice_2_test_double_roles.InMemoryUserRepository;

import java.util.Collections;

/** Mock: expectations live inside the double; it fails the test itself. */
public class HandWrittenMockDemo {

    public static void main(String[] args) {
        System.out.println("--- expected call happens ---");
        ExpectingEmailSender ok = new ExpectingEmailSender("ann@example.com", "Welcome");
        new RegistrationService(new InMemoryUserRepository(), ok).register("Ann", "ann@example.com");
        ok.verifyOnce();
        System.out.println("  verified");

        System.out.println("--- unexpected call fails immediately ---");
        ExpectingEmailSender wrongRecipient = new ExpectingEmailSender("ann@example.com", "Welcome");
        try {
            new RegistrationService(new InMemoryUserRepository(), wrongRecipient).register("Bob", "bob@example.com");
        } catch (AssertionError e) {
            System.out.println("  " + e.getMessage());
        }

        System.out.println("--- expected call never happens ---");
        ExpectingEmailSender neverCalled = new ExpectingEmailSender("ann@example.com", "Welcome");
        new RegistrationService(new StubUserRepository(true, Collections.<User>emptyList()), neverCalled)
                .register("Ann", "ann@example.com");
        try {
            neverCalled.verifyOnce();
        } catch (AssertionError e) {
            System.out.println("  " + e.getMessage());
        }
    }
}
