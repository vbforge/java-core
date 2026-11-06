package com.vbforge.practice.practice_12_builtin_collector_composition;

import com.vbforge.practice.practice_6_employee_sample_data.Employee;
import com.vbforge.practice.practice_6_employee_sample_data.EmployeeSampleData;

import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

/** summarizingInt collects count, sum, min, average and max in one pass. */
public class SummarizingIntDemo {

    public static void main(String[] args) {
        IntSummaryStatistics stats = EmployeeSampleData.employees().stream()
                .collect(Collectors.summarizingInt(Employee::getSalary));

        System.out.println("count=" + stats.getCount() + " sum=" + stats.getSum()
                + " min=" + stats.getMin() + " max=" + stats.getMax() + " average=" + stats.getAverage());
        // count=6 sum=24300 min=2800 max=6000 average=4050.0
    }
}
