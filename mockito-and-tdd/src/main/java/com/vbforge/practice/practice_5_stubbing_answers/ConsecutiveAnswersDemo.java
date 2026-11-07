package com.vbforge.practice.practice_5_stubbing_answers;

import com.vbforge.practice.practice_4_billing_sample_domain.*;
import com.vbforge.practice.practice_5_stubbing_answers.ScriptedBillingGateway;

/**
 * Consecutive stubbing: thenThrow(...).thenReturn(...).
 * Answers are used one per call; once only one is left, it repeats forever.
 */
public class ConsecutiveAnswersDemo {

    public static void main(String[] args) {
        ScriptedBillingGateway gateway = new ScriptedBillingGateway()
                .thenThrow("acc-1", new IllegalStateException("timeout"))
                .thenReturn("acc-1", ChargeStatus.APPROVED);
        RenewalService service = new RenewalService(gateway, (id, message) -> { });

        for (int call = 1; call <= 3; call++) {
            try {
                System.out.println("call " + call + " -> " + service.renew("acc-1", 500));
            } catch (IllegalStateException e) {
                System.out.println("call " + call + " -> threw " + e.getMessage());
            }
        }
    }
}
