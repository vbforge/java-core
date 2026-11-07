package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.EmailSender;

/**
 * MOCK: carries its own expectations. An unexpected call fails immediately,
 * and verifyOnce() fails when the expected call never happened.
 */
public class ExpectingEmailSender implements EmailSender {

    private final String expectedTo;
    private final String expectedSubject;
    private int received;

    public ExpectingEmailSender(String expectedTo, String expectedSubject) {
        this.expectedTo = expectedTo;
        this.expectedSubject = expectedSubject;
    }

    @Override
    public void send(String to, String subject, String body) {
        if (!expectedTo.equals(to) || !expectedSubject.equals(subject)) {
            throw new AssertionError("Unexpected call: send(" + to + ", " + subject + ")");
        }
        received++;
    }

    public void verifyOnce() {
        if (received != 1) {
            throw new AssertionError("Wanted send(" + expectedTo + ", " + expectedSubject
                    + ") once but was " + received + " time(s)");
        }
    }
}
