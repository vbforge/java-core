package com.vbforge.practice.practice_9_argument_captors;

import java.util.List;

/** Collaborator with a void method: the only way to see its input is to capture it. */
public interface AuditLog {
    void record(List<String> lines);
}
