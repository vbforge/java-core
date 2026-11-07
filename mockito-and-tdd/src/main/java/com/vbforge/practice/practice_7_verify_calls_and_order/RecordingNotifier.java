package com.vbforge.practice.practice_7_verify_calls_and_order;

import com.vbforge.practice.practice_4_billing_sample_domain.Notifier;
import java.util.ArrayList;
import java.util.List;

/** Spy for Notifier with verify-style helpers: verifyTimes(n) and verifyNever(). */
public class RecordingNotifier implements Notifier {

    private final EventLog log;
    private final List<String> calls = new ArrayList<String>();

    public RecordingNotifier() { this(new EventLog()); }
    public RecordingNotifier(EventLog log) { this.log = log; }

    @Override
    public void send(String accountId, String message) {
        calls.add(accountId + ":" + message);
        log.record("notify:" + accountId + ":" + message);
    }

    public void verifyTimes(String accountId, String message, int wanted) {
        int actual = count(accountId, message);
        if (actual != wanted) {
            throw new AssertionError("Wanted " + wanted + " time(s) send(" + accountId + ", " + message
                    + ") but was " + actual);
        }
    }

    public void verifyNever(String accountId, String message) {
        verifyTimes(accountId, message, 0);
    }

    private int count(String accountId, String message) {
        int n = 0;
        for (String call : calls) {
            if (call.equals(accountId + ":" + message)) n++;
        }
        return n;
    }
}
