package com.vbforge.oop_interfaces_and_abstraction.basics;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Counts down from {@code from} to 1. Implementing {@link Iterable} is all it takes to be usable
 * in an enhanced {@code for} loop.
 */
public class Countdown implements Iterable<Integer> {

    private final int from;

    public Countdown(int from) {
        this.from = from;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int next = from;

            @Override
            public boolean hasNext() {
                return next >= 1;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return next--;
            }
        };
    }
}
