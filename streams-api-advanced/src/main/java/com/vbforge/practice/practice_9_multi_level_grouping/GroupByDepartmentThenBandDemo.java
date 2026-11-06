package com.vbforge.practice.practice_9_multi_level_grouping;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** A downstream groupingBy gives Map&lt;K1, Map&lt;K2, V&gt;&gt;: department, then salary band. */
public class GroupByDepartmentThenBandDemo {

    public static void main(String[] args) {
        Map<String, Map<String, List<String>>> result = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.groupingBy(e -> e.getSalary() >= 4000 ? "high" : "low", TreeMap::new,
                                Collectors.mapping(Employee::getName, Collectors.toList()))));

        System.out.println(result);
        // {Eng={high=[Alice, Bob, Carol]}, HR={low=[Frank]}, Sales={low=[Dave, Eve]}}
    }
}
