package com.vbforge.practice.practice_9_equals_hashcode_contract;

import java.util.Objects;

/**
 * The equals() rules on a correct class (equals and hashCode use the SAME field):
 *   - Reflexive:  x.equals(x) == true
 *   - Symmetric:  x.equals(y) == y.equals(x)
 *   - Transitive: x.equals(y) && y.equals(z) -> x.equals(z)
 *   - Consistent: repeated calls return the same result (state unchanged)
 *   - Null-safe:  x.equals(null) == false (never throws NPE)
 */
public class EqualsContractRulesDemo {

    static class Employee {
        private final String id;
        private final String name;
        private final String dept;

        Employee(String id, String name, String dept) {
            this.id = id;
            this.name = name;
            this.dept = dept;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Employee e)) return false;
            return Objects.equals(id, e.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);   // must include exactly the fields equals uses
        }

        @Override
        public String toString() {
            return "Employee(" + id + "," + name + ")";
        }
    }

    public static void main(String[] args) {
        Employee e1 = new Employee("E001", "Alice", "Engineering");
        Employee e2 = new Employee("E001", "Alice", "Engineering");
        Employee e3 = new Employee("E002", "Bob",   "Marketing");

        System.out.println("Reflexive  e1.equals(e1):   " + e1.equals(e1));
        System.out.println("Symmetric  e1.equals(e2):   " + e1.equals(e2)
                + "  e2.equals(e1): " + e2.equals(e1));
        System.out.println("Null-safe  e1.equals(null): " + e1.equals(null));
        System.out.println("Not equal  e1.equals(e3):   " + e1.equals(e3));

        System.out.println("\nhashCode contract: equal objects -> same hash: "
                + (e1.hashCode() == e2.hashCode()));
    }
}
