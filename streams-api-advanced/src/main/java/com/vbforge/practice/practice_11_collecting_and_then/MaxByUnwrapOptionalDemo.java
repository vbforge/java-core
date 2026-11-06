package com.vbforge.practice.practice_11_collecting_and_then;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** collectingAndThen(maxBy(...), Optional::get) turns Map&lt;K, Optional&lt;E&gt;&gt; into plain values (buckets are never empty). */
public class MaxByUnwrapOptionalDemo {

    public static void main(String[] args) {
        Map<String, Employee> topEarner = EmployeeSampleData.employees().stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(Employee::getSalary)),
                                Optional::get)));

        System.out.println(topEarner);
        // {Eng=Carol(6000), HR=Frank(2800), Sales=Eve(3500)}
    }
}
