package com.vbforge.practice.practice_9_argument_captors;

import java.util.ArrayList;
import java.util.List;

/** Same job, but it reuses ONE buffer and clears it after each call: a trap for captured references. */
public class ReusedBufferBatchExporter {

    private final int batchSize;

    public ReusedBufferBatchExporter(int batchSize) { this.batchSize = batchSize; }

    public void export(List<String> lines, AuditLog log) {
        List<String> buffer = new ArrayList<String>();
        for (String line : lines) {
            buffer.add(line);
            if (buffer.size() == batchSize) {
                log.record(buffer);
                buffer.clear();
            }
        }
        if (!buffer.isEmpty()) {
            log.record(buffer);
            buffer.clear();
        }
    }
}
