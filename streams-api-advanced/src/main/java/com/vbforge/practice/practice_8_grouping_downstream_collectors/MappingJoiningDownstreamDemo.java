package com.vbforge.practice.practice_8_grouping_downstream_collectors;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** mapping(extractor, downstream) transforms each element before the downstream collector sees it. */
public class MappingJoiningDownstreamDemo {

    public static void main(String[] args) {
        Map<String, String> namesByDepartment = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.mapping(Employee::getName, Collectors.joining(", "))));

        System.out.println(namesByDepartment);
        // {Eng=Alice, Bob, Carol, HR=Frank, Sales=Dave, Eve}
    }
}
