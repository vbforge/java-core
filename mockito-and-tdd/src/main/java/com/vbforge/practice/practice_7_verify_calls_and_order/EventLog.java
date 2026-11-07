package com.vbforge.practice.practice_7_verify_calls_and_order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** One shared timeline of events across several doubles: what InOrder verifies against. */
public class EventLog {

    private final List<String> events = new ArrayList<String>();

    public void record(String event) { events.add(event); }

    public List<String> events() { return Collections.unmodifiableList(events); }

    /** true when the expected events appear in this relative order (other events may sit in between) */
    public boolean inOrder(String... expected) {
        int next = 0;
        for (String event : events) {
            if (next < expected.length && event.equals(expected[next])) {
                next++;
            }
        }
        return next == expected.length;
    }
}
