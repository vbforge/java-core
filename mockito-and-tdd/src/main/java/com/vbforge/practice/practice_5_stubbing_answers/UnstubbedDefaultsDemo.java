package com.vbforge.practice.practice_5_stubbing_answers;

import com.vbforge.practice.practice_4_billing_sample_domain.*;
import com.vbforge.practice.practice_5_stubbing_answers.ScriptedBillingGateway;

/**
 * An unstubbed double answers with "zero values": null for objects and enums, 0, false,
 * empty collections. A test can then pass for the wrong reason.
 */
public class UnstubbedDefaultsDemo {

    public static void main(String[] args) {
        ScriptedBillingGateway unstubbed = new ScriptedBillingGateway();

        System.out.println("charge         = " + unstubbed.charge("acc-1", 500));
        System.out.println("failedAttempts = " + unstubbed.failedAttempts("acc-1"));
        System.out.println("isBlocked      = " + unstubbed.isBlocked("acc-1"));
        System.out.println("history        = " + unstubbed.history("acc-1"));

        RenewalService service = new RenewalService(unstubbed, (id, message) -> { });
        System.out.println("renew -> " + service.renew("acc-1", 500)
                + "  <- DECLINED only because the status is null, not because the card was declined");

        ScriptedBillingGateway stubbed = new ScriptedBillingGateway().thenReturn("acc-1", ChargeStatus.DECLINED);
        System.out.println("renew with an explicit DECLINED stub -> "
                + new RenewalService(stubbed, (id, message) -> { }).renew("acc-1", 500));
    }
}
