package com.vbforge.practice.practice_1_registration_sample_domain;

/** Collaborator of RegistrationService: how e-mails leave the system. */
public interface EmailSender {
    void send(String to, String subject, String body);
}
