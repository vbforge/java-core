package com.vbforge.practice.practice_12_final_fields;

/**
 * 'final' field with a getter and no setter: the common idiom for a value that
 * must never change after construction.
 */
public class Account {

    private final String lastName;

    public Account(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    // public void setLastName(String v) { this.lastName = v; }   // compile error: final field
}
