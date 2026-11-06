package com.vbforge.practice.practice_12_builtin_collector_composition;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Collectors.reducing(identity, mapper, op) as a downstream: sum of salaries per department. */
public class ReducingCollectorDemo {

    public static void main(String[] args) {
        Map<String, Integer> total = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.reducing(0, Employee::getSalary, Integer::sum)));

        System.out.println(total);
        // {Eng=15000, HR=2800, Sales=6500}
    }
}
