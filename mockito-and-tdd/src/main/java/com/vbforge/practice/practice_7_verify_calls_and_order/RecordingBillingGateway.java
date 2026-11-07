package com.vbforge.practice.practice_7_verify_calls_and_order;

import com.vbforge.practice.practice_4_billing_sample_domain.BillingGateway;
import com.vbforge.practice.practice_4_billing_sample_domain.ChargeStatus;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Spy for BillingGateway: always approves, records every call on the shared log. */
public class RecordingBillingGateway implements BillingGateway {

    private final EventLog log;
    private final Set<String> blocked = new HashSet<String>();
    private final Map<String, Integer> charges = new HashMap<String, Integer>();

    public RecordingBillingGateway(EventLog log) { this.log = log; }

    public void block(String accountId) { blocked.add(accountId); }

    public int chargeCount(String accountId) {
        Integer n = charges.get(accountId);
        return n == null ? 0 : n;
    }

    @Override
    public ChargeStatus charge(String accountId, long cents) {
        log.record("charge:" + accountId);
        charges.put(accountId, chargeCount(accountId) + 1);
        return ChargeStatus.APPROVED;
    }

    @Override public void refund(String accountId, long cents) { log.record("refund:" + accountId); }
    @Override public int failedAttempts(String accountId) { return 0; }
    @Override public boolean isBlocked(String accountId) { return blocked.contains(accountId); }
    @Override public List<String> history(String accountId) { return Collections.emptyList(); }
}
