package com.vbforge.exception_handling;

import java.util.List;

/**
 * A fake resource that records what happens to it in a shared event log
 * ({@code open:x}, {@code use:x}, {@code close:x}). Used by the try-with-resources
 * demo and tasks so that ORDER of events can be asserted — no files, no network.
 *
 * <p>Note that {@link #close()} narrows the interface's {@code throws Exception}:
 * it declares no checked exception, so try-with-resources does not force a
 * {@code catch} block on callers.
 */
public class TrackedResource implements AutoCloseable {

    private final String name;
    private final List<String> log;
    private final boolean failOnClose;

    public TrackedResource(String name, List<String> log) {
        this(name, log, false, false);
    }

    /**
     * @param failOnOpen  if true the CONSTRUCTOR throws (nothing is logged, the object never exists)
     * @param failOnClose if true {@link #close()} logs the close and then throws
     */
    public TrackedResource(String name, List<String> log, boolean failOnOpen, boolean failOnClose) {
        this.name = name;
        this.log = log;
        this.failOnClose = failOnClose;
        if (failOnOpen) {
            throw new IllegalStateException("open failed: " + name);
        }
        log.add("open:" + name);
    }

    public void use() {
        log.add("use:" + name);
    }

    @Override
    public void close() {
        log.add("close:" + name);
        if (failOnClose) {
            throw new IllegalStateException("close failed: " + name);
        }
    }
}
