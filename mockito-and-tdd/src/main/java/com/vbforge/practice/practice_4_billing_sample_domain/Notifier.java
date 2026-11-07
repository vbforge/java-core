package com.vbforge.practice.practice_4_billing_sample_domain;

/** Collaborator of RenewalService: tells the customer what happened. */
public interface Notifier {
    void send(String accountId, String message);
}
