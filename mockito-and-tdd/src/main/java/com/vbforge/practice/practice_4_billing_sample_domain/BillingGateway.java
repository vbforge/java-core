package com.vbforge.practice.practice_4_billing_sample_domain;

import java.util.List;

/** Collaborator of RenewalService: the payment provider. */
public interface BillingGateway {
    ChargeStatus charge(String accountId, long cents);
    void refund(String accountId, long cents);          // void: needs the doThrow style of stubbing
    int failedAttempts(String accountId);
    boolean isBlocked(String accountId);
    List<String> history(String accountId);
}
