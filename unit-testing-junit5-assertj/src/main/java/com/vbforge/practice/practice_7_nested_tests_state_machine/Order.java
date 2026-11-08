package com.vbforge.practice.practice_7_nested_tests_state_machine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** State machine: NEW -> PAID -> SHIPPED -> DELIVERED, and CANCELLED from NEW or PAID. */
public class Order {

    public enum Status { NEW, PAID, SHIPPED, DELIVERED, CANCELLED }

    private Status status = Status.NEW;
    private final List<Status> history = new ArrayList<>(List.of(Status.NEW));

    public void pay() {
        transition("pay", status == Status.NEW, Status.PAID);
    }

    public void ship() {
        transition("ship", status == Status.PAID, Status.SHIPPED);
    }

    public void deliver() {
        transition("deliver", status == Status.SHIPPED, Status.DELIVERED);
    }

    public void cancel() {
        transition("cancel", status == Status.NEW || status == Status.PAID, Status.CANCELLED);
    }

    public Status status() {
        return status;
    }

    public List<Status> history() {
        return Collections.unmodifiableList(history);
    }

    private void transition(String action, boolean allowed, Status next) {
        if (!allowed) {
            throw new IllegalStateException("cannot " + action + " order in state " + status);
        }
        status = next;
        history.add(next);
    }
}
