package com.vbforge.practice.practice_7_verify_calls_and_order;

import com.vbforge.practice.practice_4_billing_sample_domain.*;
import com.vbforge.practice.practice_7_verify_calls_and_order.RecordingBillingGateway;

import java.util.Arrays;

/** InOrder: relative order across TWO collaborators, via one shared timeline. */
public class InOrderVerificationDemo {

    public static void main(String[] args) {
        EventLog log = new EventLog();
        RenewalService service = new RenewalService(
                new RecordingBillingGateway(log), new RecordingNotifier(log));

        service.renewAll(Arrays.asList("a", "b"), 100);

        System.out.println("events = " + log.events());
        System.out.println("charge a -> notify a -> charge b : " + log.inOrder("charge:a", "notify:a:Renewed", "charge:b"));
        System.out.println("charge a -> charge b (gaps ok)   : " + log.inOrder("charge:a", "charge:b"));
        System.out.println("charge b -> charge a (wrong)     : " + log.inOrder("charge:b", "charge:a"));
    }
}
