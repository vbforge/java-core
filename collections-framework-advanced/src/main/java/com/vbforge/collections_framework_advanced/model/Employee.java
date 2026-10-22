package com.vbforge.collections_framework_advanced.model;

/**
 * Tiny immutable model used by the comparator demo and tasks.
 *
 * <p>It deliberately has NO {@code equals}/{@code hashCode}/{@code Comparable}:
 * ordering is always supplied from outside through a {@link java.util.Comparator},
 * which is exactly the situation custom comparators are made for.
 */
public final class Employee {

    private final String name;
    private final String department;
    private final int salary;

    public Employee(String name, String department, int salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return name + "(" + department + ", " + salary + ")";
    }
}
