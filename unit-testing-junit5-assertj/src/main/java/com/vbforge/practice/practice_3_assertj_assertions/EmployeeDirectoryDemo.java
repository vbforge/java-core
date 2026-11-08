package com.vbforge.practice.practice_3_assertj_assertions;

public class EmployeeDirectoryDemo {
    public static void main(String[] args) {
        EmployeeDirectory dir = new EmployeeDirectory();
        dir.add(new Employee(1, "Alice", "alice@corp.com", "DEV", 5000));
        dir.add(new Employee(2, "Bob", "bob@corp.com", "QA", 3000));
        dir.add(new Employee(3, "Carol", "carol@corp.com", "DEV", 6000));
        System.out.println(dir.headcountByDepartment());
        System.out.println(dir.topEarners(2));
        System.out.println(dir.averageSalary("DEV"));
    }
}
