package com.vbforge.practice.practice_8_grouping_downstream_collectors;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** maxBy(comparator) returns an Optional (see practice_11 for unwrapping it). */
public class MaxByOptionalDownstreamDemo {

    public static void main(String[] args) {
        Map<String, Optional<Employee>> topEarner = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.maxBy(Comparator.comparingInt(Employee::getSalary))));

        System.out.println(topEarner);
        // {Eng=Optional[Carol(6000)], HR=Optional[Frank(2800)], Sales=Optional[Eve(3500)]}
    }
}
