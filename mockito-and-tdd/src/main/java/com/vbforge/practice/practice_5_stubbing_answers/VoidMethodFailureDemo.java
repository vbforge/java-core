package com.vbforge.practice.practice_5_stubbing_answers;

import com.vbforge.practice.practice_4_billing_sample_domain.*;
import com.vbforge.practice.practice_5_stubbing_answers.ScriptedBillingGateway;

import java.util.ArrayList;
import java.util.List;

/**
 * A void method has no return value to stub, so when(gateway.refund(...)) cannot compile.
 * Mockito's doThrow(ex).when(gateway).refund(...) puts the failure first; here the
 * equivalent is configuring the double before the call.
 */
public class VoidMethodFailureDemo {

    public static void main(String[] args) {
        final List<String> sent = new ArrayList<String>();
        Notifier recording = (id, message) -> sent.add(id + ":" + message);

        RenewalService healthy = new RenewalService(new ScriptedBillingGateway(), recording);
        System.out.println("healthy refund -> " + healthy.cancel("acc-1", 500));

        ScriptedBillingGateway failing = new ScriptedBillingGateway()
                .doThrowOnRefund(new IllegalStateException("bank down"));
        RenewalService broken = new RenewalService(failing, recording);
        System.out.println("failing refund -> " + broken.cancel("acc-1", 500));

        System.out.println("notifications  = " + sent);
    }
}
