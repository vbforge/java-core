package com.vbforge.practice.practice_5_stubbing_answers;

import com.vbforge.practice.practice_4_billing_sample_domain.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hand-written stand-in for a stubbed Mockito mock of BillingGateway.
 *
 *   thenReturn / thenThrow   consecutive answers per account: consumed in order,
 *                            the LAST one repeats (Mockito's consecutive stubbing)
 *   doThrowOnRefund          the void-method style (doThrow(...).when(gateway).refund(...))
 *   unstubbed calls          "zero values": null for objects/enums, 0, false, empty list
 */
public class ScriptedBillingGateway implements BillingGateway {

    private final Map<String, Deque<Object>> answers = new LinkedHashMap<String, Deque<Object>>();
    private final Set<String> used = new HashSet<String>();
    private final Set<String> blocked = new HashSet<String>();
    private RuntimeException refundFailure;

    public ScriptedBillingGateway thenReturn(String accountId, ChargeStatus status) {
        answersFor(accountId).add(status);
        return this;
    }

    public ScriptedBillingGateway thenThrow(String accountId, RuntimeException failure) {
        answersFor(accountId).add(failure);
        return this;
    }

    public ScriptedBillingGateway block(String accountId) {
        blocked.add(accountId);
        return this;
    }

    public ScriptedBillingGateway doThrowOnRefund(RuntimeException failure) {
        this.refundFailure = failure;
        return this;
    }

    /** Accounts that were stubbed but never charged: what strict stubs would complain about. */
    public List<String> unusedStubs() {
        List<String> unused = new ArrayList<String>();
        for (String account : answers.keySet()) {
            if (!used.contains(account)) {
                unused.add(account);
            }
        }
        return unused;
    }

    private Deque<Object> answersFor(String accountId) {
        Deque<Object> queue = answers.get(accountId);
        if (queue == null) {
            queue = new LinkedList<Object>();
            answers.put(accountId, queue);
        }
        return queue;
    }

    @Override
    public ChargeStatus charge(String accountId, long cents) {
        Deque<Object> queue = answers.get(accountId);
        if (queue == null) {
            return null;                                   // unstubbed: null for an enum
        }
        used.add(accountId);
        Object answer = queue.size() > 1 ? queue.poll() : queue.peek();
        if (answer instanceof RuntimeException) {
            throw (RuntimeException) answer;
        }
        return (ChargeStatus) answer;
    }

    @Override
    public void refund(String accountId, long cents) {
        if (refundFailure != null) {
            throw refundFailure;
        }
    }

    @Override public int failedAttempts(String accountId) { return 0; }
    @Override public boolean isBlocked(String accountId) { return blocked.contains(accountId); }
    @Override public List<String> history(String accountId) { return Collections.emptyList(); }
}
