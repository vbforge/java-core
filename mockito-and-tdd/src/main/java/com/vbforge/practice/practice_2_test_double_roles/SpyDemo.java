package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.*;
import com.vbforge.practice.practice_2_test_double_roles.InMemoryUserRepository;
import com.vbforge.practice.practice_2_test_double_roles.RecordingEmailSender;

/** Spy: records calls; the test inspects them afterwards (check-before-act is visible). */
public class SpyDemo {

    public static void main(String[] args) {
        RecordingEmailSender spy = new RecordingEmailSender();
        RegistrationService service = new RegistrationService(new InMemoryUserRepository(), spy);

        service.register("Ann", "ann@example.com");
        service.register("Bob", "bob@example.com");
        service.register("Ann", "ann@example.com");     // duplicate: nothing may be sent

        System.out.println("e-mails sent = " + spy.callCount());                        // 2
        for (String call : spy.calls()) {
            System.out.println("  " + call);
        }
    }
}
