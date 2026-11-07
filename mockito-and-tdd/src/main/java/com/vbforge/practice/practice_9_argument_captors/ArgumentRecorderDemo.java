package com.vbforge.practice.practice_9_argument_captors;

import java.util.Arrays;

/** getValue() is the LAST argument only; getAllValues() has every call, in order. */
public class ArgumentRecorderDemo {

    public static void main(String[] args) {
        RecordingAuditLog log = new RecordingAuditLog();

        new BatchExporter(2).export(Arrays.asList("a", "b", "c", "d", "e"), log);

        System.out.println("getValue()     = " + log.captor().getValue());
        System.out.println("getAllValues() = " + log.captor().getAllValues());
        System.out.println("calls          = " + log.captor().getAllValues().size());
    }
}
