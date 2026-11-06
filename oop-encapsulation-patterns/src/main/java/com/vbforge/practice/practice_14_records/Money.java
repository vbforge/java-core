package com.vbforge.practice.practice_14_records;

import com.vbforge.practice.practice_14_records.Describable;

public record Money(double amount, String currency) implements Describable {

    public Money {
        if (amount < 0)
            throw new IllegalArgumentException("Amount cannot be negative: " + amount);
        if (currency == null || currency.isBlank())
            throw new IllegalArgumentException("Currency cannot be blank");
        currency = currency.toUpperCase();   // normalise: reassign the PARAMETER, never this.currency
    }

    @Override
    public String describe() {
        return String.format("%.2f %s", amount, currency);
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException("Cannot add different currencies");
        return new Money(this.amount + other.amount, this.currency);
    }
}
