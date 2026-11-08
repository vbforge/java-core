package com.vbforge.practice.practice_3_assertj_assertions;

public record Employee(int id, String name, String email, String department, int salary) {
    public Employee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("salary must not be negative");
        }
    }
}
