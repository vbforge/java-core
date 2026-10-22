package com.vbforge.collections_framework_advanced.model;

/**
 * Tiny immutable model used by the PriorityQueue demo and tasks.
 * A SMALLER priority number means MORE urgent ({@code 1} is served before {@code 5}).
 */
public final class Ticket {

    private final String id;
    private final int priority;

    public Ticket(String id, int priority) {
        this.id = id;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return id + "(p" + priority + ")";
    }
}
