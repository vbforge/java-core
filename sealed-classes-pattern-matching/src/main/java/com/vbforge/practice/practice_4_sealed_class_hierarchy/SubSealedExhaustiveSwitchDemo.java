package com.vbforge.practice.practice_4_sealed_class_hierarchy;

import com.vbforge.practice.practice_4_sealed_class_hierarchy.Employee.Director;
import com.vbforge.practice.practice_4_sealed_class_hierarchy.Employee.Engineer;
import com.vbforge.practice.practice_4_sealed_class_hierarchy.Employee.Manager;
import com.vbforge.practice.practice_4_sealed_class_hierarchy.Employee.TeamLead;

/**
 * Exhaustiveness across two levels. Because Manager is abstract and sealed, a switch is complete
 * either with its two leaves (Director, TeamLead) or with one case for Manager itself.
 */
public class SubSealedExhaustiveSwitchDemo {

    static String role(Employee e) {
        return switch (e) {
            case Director d -> "director";
            case TeamLead t -> "team lead";
            case Engineer g -> "engineer";
        };
    }

    static String group(Employee e) {
        return switch (e) {
            case Manager m  -> "management";   // covers Director and TeamLead
            case Engineer g -> "engineering";
        };
    }

    public static void main(String[] args) {
        Employee[] staff = {new Director(), new TeamLead(), new Engineer()};
        for (Employee e : staff) {
            System.out.println(e.getClass().getSimpleName() + " -> " + role(e) + " / " + group(e));
        }
    }
}
