package com.vbforge.practice.practice_7_verify_calls_and_order;

import com.vbforge.practice.practice_4_billing_sample_domain.*;

/** verify(times(n)) and verify(never()): checking that a call did or did not happen. */
public class VerifyTimesAndNeverDemo {

    public static void main(String[] args) {
        RecordingNotifier notifier = new RecordingNotifier();
        RecordingBillingGateway gateway = new RecordingBillingGateway(new EventLog());
        gateway.block("acc-2");
        RenewalService service = new RenewalService(gateway, notifier);

        service.renew("acc-1", 500);
        service.renew("acc-1", 500);
        service.renew("acc-2", 500);

        attempt("times(2): acc-1 Renewed", () -> notifier.verifyTimes("acc-1", "Renewed", 2));
        attempt("times(1): acc-1 Renewed", () -> notifier.verifyTimes("acc-1", "Renewed", 1));
        attempt("never():  acc-1 Declined", () -> notifier.verifyNever("acc-1", "Declined"));
        attempt("never():  acc-2 charged", () -> {
            if (gateway.chargeCount("acc-2") != 0) {
                throw new AssertionError("acc-2 was charged " + gateway.chargeCount("acc-2") + " time(s)");
            }
        });
    }

    private static void attempt(String label, Runnable verification) {
        try {
            verification.run();
            System.out.println("  PASS  " + label);
        } catch (AssertionError e) {
            System.out.println("  FAIL  " + label + " -> " + e.getMessage());
        }
    }
}
