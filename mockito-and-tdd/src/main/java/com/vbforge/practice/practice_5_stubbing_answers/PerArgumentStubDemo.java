package com.vbforge.practice.practice_5_stubbing_answers;

import com.vbforge.practice.practice_4_billing_sample_domain.*;
import com.vbforge.practice.practice_5_stubbing_answers.ScriptedBillingGateway;

/** A stub gives a different answer depending on the argument. */
public class PerArgumentStubDemo {

    public static void main(String[] args) {
        ScriptedBillingGateway gateway = new ScriptedBillingGateway()
                .thenReturn("acc-1", ChargeStatus.APPROVED)
                .thenReturn("acc-2", ChargeStatus.DECLINED);
        Notifier printing = (id, message) -> System.out.println("  notify " + id + ": " + message);
        RenewalService service = new RenewalService(gateway, printing);

        System.out.println("acc-1 -> " + service.renew("acc-1", 500));
        System.out.println("acc-2 -> " + service.renew("acc-2", 500));
    }
}
