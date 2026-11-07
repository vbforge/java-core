package com.vbforge.practice.practice_6_strict_and_unused_stubs;

import com.vbforge.practice.practice_4_billing_sample_domain.*;
import com.vbforge.practice.practice_5_stubbing_answers.ScriptedBillingGateway;
import java.util.List;

/**
 * Strict stubs: a stubbing the test never used is reported (Mockito's
 * UnnecessaryStubbingException). A lenient stub opts out of that check.
 * An unused stub usually means the test and the code have drifted apart.
 */
public class UnusedStubDetectionDemo {

    public static void main(String[] args) {
        ScriptedBillingGateway gateway = new ScriptedBillingGateway()
                .thenReturn("acc-1", ChargeStatus.APPROVED)
                .thenReturn("acc-2", ChargeStatus.APPROVED)
                .thenReturn("acc-3", ChargeStatus.DECLINED);        // never used below
        RenewalService service = new RenewalService(gateway, (id, message) -> { });

        service.renew("acc-1", 500);
        service.renew("acc-2", 500);

        List<String> unused = gateway.unusedStubs();
        System.out.println("strict : " + (unused.isEmpty() ? "ok" : "UnnecessaryStubbing for " + unused));
        System.out.println("lenient: ignored " + unused + " (the stub was marked lenient on purpose)");
    }
}
