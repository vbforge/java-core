package com.vbforge.practice.practice_6_employee_sample_data;

import java.util.Arrays;
import java.util.List;

/** Six employees in three departments (Eng 3, Sales 2, HR 1). One place for the setup the employee demos share. */
public final class EmployeeSampleData {

    private EmployeeSampleData() {
    }

    public static List<Employee> employees() {
        return Arrays.asList(
                new Employee("Alice", "Eng", 5000),
                new Employee("Bob", "Eng", 4000),
                new Employee("Carol", "Eng", 6000),
                new Employee("Dave", "Sales", 3000),
                new Employee("Eve", "Sales", 3500),
                new Employee("Frank", "HR", 2800));
    }
}
