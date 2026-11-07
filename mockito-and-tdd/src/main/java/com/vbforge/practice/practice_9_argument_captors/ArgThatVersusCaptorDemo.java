package com.vbforge.practice.practice_9_argument_captors;

import java.util.Arrays;
import java.util.List;

/** argThat answers "did a matching call happen?"; a captor shows the actual values so you can assert details. */
public class ArgThatVersusCaptorDemo {

    public static void main(String[] args) {
        RecordingAuditLog log = new RecordingAuditLog();
        new BatchExporter(2).export(Arrays.asList("a", "b", "c", "d", "e"), log);

        int matching = log.timesCalledWith(batch -> batch.size() == 3);
        System.out.println("argThat (size == 3): " + matching + " call(s) matched  <- says nothing about WHY");

        List<List<String>> all = log.captor().getAllValues();
        System.out.println("captor shows       : " + all);
        System.out.println("sizes asserted     : " + all.get(0).size() + ", " + all.get(1).size() + ", " + all.get(2).size());
    }
}
