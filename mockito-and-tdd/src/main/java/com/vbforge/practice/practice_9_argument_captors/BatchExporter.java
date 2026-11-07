package com.vbforge.practice.practice_9_argument_captors;

import java.util.ArrayList;
import java.util.List;

/** Sends lines to the audit log in batches. Each batch is a fresh COPY, so it is safe to capture. */
public class BatchExporter {

    private final int batchSize;

    public BatchExporter(int batchSize) { this.batchSize = batchSize; }

    public void export(List<String> lines, AuditLog log) {
        for (int from = 0; from < lines.size(); from += batchSize) {
            int to = Math.min(from + batchSize, lines.size());
            log.record(new ArrayList<String>(lines.subList(from, to)));
        }
    }
}
