package com.vbforge.practice.practice_2_test_double_roles;

import com.vbforge.practice.practice_1_registration_sample_domain.EmailSender;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** SPY: records every call, in order, so the test can inspect them afterwards. */
public class RecordingEmailSender implements EmailSender {

    private final List<String> calls = new ArrayList<String>();

    @Override
    public void send(String to, String subject, String body) {
        calls.add(to + " | " + subject + " | " + body);
    }

    public List<String> calls() { return Collections.unmodifiableList(calls); }
    public int callCount() { return calls.size(); }
}
