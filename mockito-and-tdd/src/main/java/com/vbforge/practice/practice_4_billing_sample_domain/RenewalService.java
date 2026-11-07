package com.vbforge.practice.practice_4_billing_sample_domain;

import java.util.List;

/** System under test for the stubbing / verification / captor demos. */
public class RenewalService {

    private final BillingGateway gateway;
    private final Notifier notifier;

    public RenewalService(BillingGateway gateway, Notifier notifier) {
        this.gateway = gateway;
        this.notifier = notifier;
    }

    /** @return BLOCKED, RENEWED or DECLINED (anything that is not APPROVED counts as declined) */
    public String renew(String accountId, long cents) {
        if (gateway.isBlocked(accountId)) {
            notifier.send(accountId, "Blocked");
            return "BLOCKED";
        }
        ChargeStatus status = gateway.charge(accountId, cents);
        if (status == ChargeStatus.APPROVED) {
            notifier.send(accountId, "Renewed");
            return "RENEWED";
        }
        notifier.send(accountId, "Declined");
        return "DECLINED";
    }

    public void renewAll(List<String> accountIds, long cents) {
        for (String id : accountIds) {
            renew(id, cents);
        }
    }

    /** @return true when the refund went through, false when the gateway failed */
    public boolean cancel(String accountId, long cents) {
        try {
            gateway.refund(accountId, cents);
            notifier.send(accountId, "Refunded");
            return true;
        } catch (RuntimeException e) {
            notifier.send(accountId, "Refund failed");
            return false;
        }
    }
}
