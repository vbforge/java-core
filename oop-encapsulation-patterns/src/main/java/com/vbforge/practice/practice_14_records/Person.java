package com.vbforge.practice.practice_14_records;

import com.vbforge.practice.practice_14_records.Address;

public record Person(String name, int age, Address address) {

    public Person {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be blank");
        if (age < 0 || age > 150)
            throw new IllegalArgumentException("Age out of range: " + age);
    }
}
