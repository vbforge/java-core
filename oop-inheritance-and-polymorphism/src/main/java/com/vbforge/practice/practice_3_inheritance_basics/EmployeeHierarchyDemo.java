package com.vbforge.practice.practice_3_inheritance_basics;

/**
 * Inheritance with a small Employee hierarchy.
 *
 * Key concepts:
 *   1. extends        - subclass inherits fields and methods from the superclass
 *   2. super(...)     - calls the superclass constructor (must be the first statement)
 *   3. super.method() - reuses the overridden superclass implementation
 *   4. @Override      - compile-time check that the override is intentional
 *   5. protected      - baseSalary is visible to subclasses
 *
 * Java supports single inheritance only (one direct superclass).
 * All classes implicitly extend java.lang.Object.
 */
public class EmployeeHierarchyDemo {

    // --- Base class (superclass) ---
    static class Employee {

        private final String name;
        private final String department;
        protected double baseSalary;   // protected = accessible in subclasses

        public Employee(String name, String department, double baseSalary) {
            this.name       = name;
            this.department = department;
            this.baseSalary = baseSalary;
        }

        public double calculatePay() {
            return baseSalary;
        }

        public String getDetails() {
            return name + " [" + department + "] base=" + baseSalary;
        }

        public String getName()       { return name;       }
        public String getDepartment() { return department; }
    }

    // --- Subclass ---
    static class Manager extends Employee {

        private final int teamSize;
        private final double bonus;

        public Manager(String name, String department, double baseSalary, int teamSize, double bonus) {
            super(name, department, baseSalary);   // must be first - initializes the Employee part
            this.teamSize = teamSize;
            this.bonus    = bonus;
        }

        @Override
        public double calculatePay() {
            return baseSalary + bonus;    // builds on the parent's concept
        }

        @Override
        public String getDetails() {
            return super.getDetails()     // reuses the parent implementation
                   + ", teamSize=" + teamSize + ", bonus=" + bonus;
        }

        public int    getTeamSize() { return teamSize; }
        public double getBonus()    { return bonus;    }
    }

    // --- Another subclass ---
    static class Contractor extends Employee {

        private final double hourlyRate;
        private final int    hoursWorked;

        public Contractor(String name, String department, double hourlyRate, int hoursWorked) {
            super(name, department, 0);   // contractors have no base salary
            this.hourlyRate  = hourlyRate;
            this.hoursWorked = hoursWorked;
        }

        @Override
        public double calculatePay() {
            return hourlyRate * hoursWorked;
        }

        @Override
        public String getDetails() {
            return super.getDetails()
                   + ", rate=" + hourlyRate + ", hours=" + hoursWorked;
        }
    }

    public static void main(String[] args) {
        Employee   emp      = new Employee("Alice", "HR", 50_000);
        Manager    mgr      = new Manager("Bob", "Engineering", 80_000, 8, 15_000);
        Contractor contract = new Contractor("Carol", "Design", 75.0, 160);

        System.out.println("-- Details --");
        System.out.println(emp.getDetails());
        System.out.println(mgr.getDetails());
        System.out.println(contract.getDetails());

        System.out.println("\n-- Pay calculations (polymorphic) --");
        Employee[] staff = { emp, mgr, contract };
        for (Employee e : staff) {
            System.out.printf("%-8s pay: %.2f%n", e.getName(), e.calculatePay());
        }
    }
}
