package com.vbforge.practice.practice_10_when_not_to_mock;

import java.util.Objects;

/** Value object: cheap to build, equal by value. Build the real one instead of doubling it. */
public class Money {

    private final long cents;

    public Money(long cents) { this.cents = cents; }

    public long cents() { return cents; }

    public Money plus(Money other) { return new Money(cents() + other.cents()); }

    @Override
    public boolean equals(Object o) {
        return o instanceof Money && ((Money) o).cents == cents;
    }

    @Override
    public int hashCode() { return Objects.hash(cents); }

    @Override
    public String toString() { return cents + "c"; }
}
