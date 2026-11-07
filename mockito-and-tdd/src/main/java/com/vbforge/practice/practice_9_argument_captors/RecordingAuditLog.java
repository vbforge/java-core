package com.vbforge.practice.practice_9_argument_captors;

import java.util.List;
import java.util.function.Predicate;

/** Spy for AuditLog that captures every argument. */
public class RecordingAuditLog implements AuditLog {

    private final ArgumentRecorder<List<String>> captor = new ArgumentRecorder<List<String>>();

    @Override
    public void record(List<String> lines) { captor.capture(lines); }

    public ArgumentRecorder<List<String>> captor() { return captor; }

    /** argThat-style: how many calls had an argument accepted by the matcher */
    public int timesCalledWith(Predicate<List<String>> matcher) {
        int n = 0;
        for (List<String> call : captor.getAllValues()) {
            if (matcher.test(call)) n++;
        }
        return n;
    }
}
