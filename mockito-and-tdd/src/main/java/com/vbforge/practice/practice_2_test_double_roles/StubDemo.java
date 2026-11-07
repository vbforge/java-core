package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.*;
import com.vbforge.practice.practice_2_test_double_roles.RecordingEmailSender;
import com.vbforge.practice.practice_2_test_double_roles.StubUserRepository;

import java.util.Arrays;

/** Stub: canned answers. It does not remember what was saved. */
public class StubDemo {

    public static void main(String[] args) {
        StubUserRepository stub = new StubUserRepository(false, Arrays.asList(
                new User("Ann", "ann@example.com"), new User("Bob", "bob@example.com")));
        RegistrationService service = new RegistrationService(stub, new RecordingEmailSender());

        System.out.println("countUsers (canned)    = " + service.countUsers());          // 2
        System.out.println("register new user      = " + service.register("Cy", "cy@example.com"));
        System.out.println("countUsers after save  = " + service.countUsers() + "  <- still 2: a stub keeps no state");
    }
}
