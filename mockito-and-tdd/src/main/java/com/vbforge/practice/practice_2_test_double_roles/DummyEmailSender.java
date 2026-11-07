package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.EmailSender;

/** DUMMY: fills a parameter, must never be used. Any call is a test failure. */
public class DummyEmailSender implements EmailSender {
    @Override
    public void send(String to, String subject, String body) {
        throw new AssertionError("dummy EmailSender must never be called");
    }
}
