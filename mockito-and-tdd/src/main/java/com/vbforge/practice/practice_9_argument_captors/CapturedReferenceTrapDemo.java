package com.vbforge.practice.practice_9_argument_captors;

import com.vbforge.practice.practice_9_argument_captors.ReusedBufferBatchExporter;

import java.util.Arrays;
import java.util.List;

/** A captor keeps references: if the code clears the list it passed, the captured value is empty too. */
public class CapturedReferenceTrapDemo {

    public static void main(String[] args) {
        List<String> lines = Arrays.asList("a", "b", "c", "d", "e");

        RecordingAuditLog safe = new RecordingAuditLog();
        new BatchExporter(2).export(lines, safe);
        System.out.println("copy per batch  : " + safe.captor().getAllValues());

        RecordingAuditLog leaky = new RecordingAuditLog();
        new ReusedBufferBatchExporter(2).export(lines, leaky);
        System.out.println("reused + cleared: " + leaky.captor().getAllValues()
                + "  <- every captured list is the same, now empty, buffer");
    }
}
