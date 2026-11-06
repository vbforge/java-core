package com.vbforge.practice.practice_7_grouping_by_basics;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** groupingBy(classifier) gives Map&lt;K, List&lt;T&gt;&gt;; only keys that occur exist (no empty buckets). */
public class GroupByDepartmentDemo {

    public static void main(String[] args) {
        Map<String, List<Employee>> byDepartment = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));

        // HashMap order is unspecified, so print through a TreeMap
        System.out.println(new TreeMap<>(byDepartment));
        // {Eng=[Alice(5000), Bob(4000), Carol(6000)], HR=[Frank(2800)], Sales=[Dave(3000), Eve(3500)]}

        System.out.println("Legal bucket: " + byDepartment.get("Legal")); // null - the key was never created
    }
}
