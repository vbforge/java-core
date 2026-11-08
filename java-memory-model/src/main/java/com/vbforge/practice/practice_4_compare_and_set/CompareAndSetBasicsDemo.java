package com.vbforge.practice.practice_4_compare_and_set;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * compareAndSet(expected, new): "set it to new only if it still holds expected". It returns
 * false when someone changed the value in between. A retry loop turns it into a general
 * atomic update. Single-threaded, the "someone else" is simulated, so output is fixed.
 */
public class CompareAndSetBasicsDemo {

    public static void main(String[] args) {
        AtomicInteger value = new AtomicInteger(5);

        System.out.println("CAS(5 -> 6): " + value.compareAndSet(5, 6) + ", value = " + value.get());
        System.out.println("CAS(5 -> 7): " + value.compareAndSet(5, 7) + ", value = " + value.get()); // stale expected

        // Another "thread" sneaks in between our read and our CAS:
        int seen = value.get();            // we read 6
        value.set(100);                    // somebody else writes 100
        boolean swapped = value.compareAndSet(seen, seen + 1);
        System.out.println("CAS after interference: " + swapped + ", value = " + value.get());

        // The retry loop: read, compute, CAS, and start again if the CAS failed.
        int next = incrementWithRetryLoop(value);
        System.out.println("retry-loop increment -> " + next);
    }

    static int incrementWithRetryLoop(AtomicInteger value) {
        int current;
        int next;
        do {
            current = value.get();
            next = current + 1;
        } while (!value.compareAndSet(current, next));
        return next;
    }
}
