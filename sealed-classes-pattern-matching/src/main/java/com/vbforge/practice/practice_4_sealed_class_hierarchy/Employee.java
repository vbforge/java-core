package com.vbforge.practice.practice_4_sealed_class_hierarchy;

/**
 * A two-level sealed class hierarchy:
 *
 *   Employee (abstract sealed)
 *     |- Manager (abstract sealed, "sub-sealed")
 *     |    |- Director (final)
 *     |    `- TeamLead (final)
 *     `- Engineer (final)
 */
public abstract sealed class Employee permits Employee.Manager, Employee.Engineer {

    public abstract static sealed class Manager extends Employee permits Director, TeamLead {}

    public static final class Director extends Manager {}
    public static final class TeamLead extends Manager {}
    public static final class Engineer extends Employee {}
}
