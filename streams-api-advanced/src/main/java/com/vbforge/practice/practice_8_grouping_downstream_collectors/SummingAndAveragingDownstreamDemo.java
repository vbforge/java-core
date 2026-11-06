package com.vbforge.practice.practice_8_grouping_downstream_collectors;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** summingInt gives Integer, averagingInt gives Double. */
public class SummingAndAveragingDownstreamDemo {

    public static void main(String[] args) {
        Map<String, Integer> totals = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.summingInt(Employee::getSalary)));
        System.out.println("total:   " + totals);
        // total:   {Eng=15000, HR=2800, Sales=6500}

        Map<String, Double> averages = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.averagingInt(Employee::getSalary)));
        System.out.println("average: " + averages);
        // average: {Eng=5000.0, HR=2800.0, Sales=3250.0}
    }
}
