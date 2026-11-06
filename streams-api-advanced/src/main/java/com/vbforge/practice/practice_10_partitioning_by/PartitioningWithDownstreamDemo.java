package com.vbforge.practice.practice_10_partitioning_by;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.Map;
import java.util.stream.Collectors;

/** partitioningBy accepts a downstream collector: count the employees earning at least 4000 vs less. */
public class PartitioningWithDownstreamDemo {

    public static void main(String[] args) {
        Map<Boolean, Long> counts = EmployeeSampleData.employees().stream()
                .collect(Collectors.partitioningBy(e -> e.getSalary() >= 4000, Collectors.counting()));

        System.out.println(counts);
        // {false=3, true=3}
    }
}
