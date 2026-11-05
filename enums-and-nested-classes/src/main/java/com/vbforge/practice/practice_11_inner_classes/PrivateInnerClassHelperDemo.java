package com.vbforge.practice.practice_11_inner_classes;

/** A private inner class: an implementation detail that only the outer class can create and see. */
public class PrivateInnerClassHelperDemo {

    public static void main(String[] args) {
        Employee employee = new Employee("John", "hr".toUpperCase());
        String jobDescription = employee.getJobDescription();
        System.out.println(jobDescription);
    }
}

class Employee {
    private final String name;
    private final String department;

    public Employee(String name, String department) {
        this.name = name;
        this.department = department;
    }

    // This method uses the private inner class
    public String getJobDescription() {
        JobDescription jobDescription = new JobDescription();
        return jobDescription.getDescription();
    }

    // Inner class: JobDescription (invisible outside Employee)
    private class JobDescription {
        private final String description;

        public JobDescription() {
            // outer members are accessed directly
            this.description = "The " + department + " department is responsible for " + getResponsibilities();
        }

        private String getResponsibilities() {   // private method of the inner class
            return "specific tasks related to " + department;
        }

        public String getDescription() {
            return description;
        }
    }
}
