package com.vbforge.practice.practice_7_grouping_by_basics;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** The three-argument groupingBy picks the Map implementation: TreeMap (sorted keys) or LinkedHashMap (encounter order). */
public class GroupingMapFactoryDemo {

    public static void main(String[] args) {
        Map<String, List<Employee>> sorted = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new, Collectors.toList()));
        System.out.println("TreeMap keys:          " + sorted.keySet());
        // [Eng, HR, Sales]

        Map<String, List<Employee>> encounter = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, LinkedHashMap::new, Collectors.toList()));
        System.out.println("LinkedHashMap keys:    " + encounter.keySet());
        // [Eng, Sales, HR]
    }
}
