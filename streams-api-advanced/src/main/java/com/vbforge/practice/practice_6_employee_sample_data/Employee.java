package com.vbforge.practice.practice_6_employee_sample_data;

/** Small immutable domain class used by the grouping / collector practice (name, department, salary). */
public class Employee {

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
        return name + "(" + salary + ")";
    }
}
