package com.vbforge.practice.practice_3_assertj_assertions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Returns strings, numbers, lists, maps and Optionals: a good target for the whole AssertJ toolbox. */
public class EmployeeDirectory {

    private final List<Employee> employees = new ArrayList<>();

    public void add(Employee employee) {
        boolean duplicate = employees.stream()
                .anyMatch(e -> e.email().equalsIgnoreCase(employee.email()));
        if (duplicate) {
            throw new IllegalStateException("duplicate email: " + employee.email());
        }
        employees.add(employee);
    }

    public Optional<Employee> findByEmail(String email) {
        return employees.stream().filter(e -> e.email().equalsIgnoreCase(email)).findFirst();
    }

    public List<Employee> inDepartment(String department) {
        return employees.stream()
                .filter(e -> e.department().equals(department))
                .sorted(Comparator.comparing(Employee::name))
                .collect(Collectors.toList());
    }

    public List<Employee> topEarners(int n) {
        return employees.stream()
                .sorted(Comparator.comparingInt(Employee::salary).reversed().thenComparing(Employee::name))
                .limit(n)
                .collect(Collectors.toList());
    }

    public Map<String, Long> headcountByDepartment() {
        return employees.stream().collect(
                Collectors.groupingBy(Employee::department, TreeMap::new, Collectors.counting()));
    }

    public OptionalDouble averageSalary(String department) {
        return employees.stream()
                .filter(e -> e.department().equals(department))
                .mapToInt(Employee::salary)
                .average();
    }

    public int size() {
        return employees.size();
    }
}
