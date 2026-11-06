package com.vbforge.practice.practice_8_grouping_downstream_collectors;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** groupingBy + counting(): the value type is Long, not Integer. */
public class CountingDownstreamDemo {

    public static void main(String[] args) {
        Map<String, Long> countByDepartment = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new, Collectors.counting()));

        System.out.println(countByDepartment);
        // {Eng=3, HR=1, Sales=2}
    }
}
