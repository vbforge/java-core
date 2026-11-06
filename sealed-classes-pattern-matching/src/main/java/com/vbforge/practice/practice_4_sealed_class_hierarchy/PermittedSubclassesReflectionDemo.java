package com.vbforge.practice.practice_4_sealed_class_hierarchy;

/**
 * Class#isSealed and Class#getPermittedSubclasses expose the hierarchy at runtime.
 * A recursive walk counts the leaf types (types that are not sealed).
 */
public class PermittedSubclassesReflectionDemo {

    static int countLeafTypes(Class<?> type) {
        if (!type.isSealed()) return 1;
        int leaves = 0;
        for (Class<?> permitted : type.getPermittedSubclasses()) {
            leaves += countLeafTypes(permitted);
        }
        return leaves;
    }

    static void print(Class<?> type, String indent) {
        System.out.println(indent + type.getSimpleName() + (type.isSealed() ? " (sealed)" : ""));
        if (type.isSealed()) {
            for (Class<?> permitted : type.getPermittedSubclasses()) {
                print(permitted, indent + "  ");
            }
        }
    }

    public static void main(String[] args) {
        print(Employee.class, "");
        System.out.println("leaf types under Employee: " + countLeafTypes(Employee.class));
        System.out.println("leaf types under Manager:  " + countLeafTypes(Employee.Manager.class));
        System.out.println("leaf types of Engineer:    " + countLeafTypes(Employee.Engineer.class));
    }
}
