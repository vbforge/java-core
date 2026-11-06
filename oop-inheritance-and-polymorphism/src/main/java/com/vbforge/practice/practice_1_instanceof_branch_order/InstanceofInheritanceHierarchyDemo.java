package com.vbforge.practice.practice_1_instanceof_branch_order;

/**
 * `instanceof` walks the class hierarchy. A subclass instance matches
 * `instanceof` for every superclass it extends — so ORDER of the branches matters.
 *
 * null instanceof Anything → false (safe; never NPEs), so null falls into the final else.
 */
public class InstanceofInheritanceHierarchyDemo {

    // --- hierarchy 1: hospital staff ---
    static class MedicalStaff { }
    static class Nurse        extends MedicalStaff { }
    static class Doctor       extends MedicalStaff { }
    static class HeadDoctor   extends Doctor { }

    // --- hierarchy 2: courses ---
    static class Course         { }
    static class BaseCourse     extends Course { }
    static class FreeCourse     extends BaseCourse { }
    static class OptionalCourse extends Course { }

    public static void main(String[] args) {
        System.out.println("--- hierarchy 1: what does HeadDoctor match? ---");
        MedicalStaff medic = new HeadDoctor();
        System.out.println("medic instanceof Nurse       : " + (medic instanceof Nurse));
        System.out.println("medic instanceof Doctor      : " + (medic instanceof Doctor));
        System.out.println("medic instanceof HeadDoctor  : " + (medic instanceof HeadDoctor));
        System.out.println("medic instanceof MedicalStaff: " + (medic instanceof MedicalStaff));

        System.out.println("\n--- branch ordering matters ---");
        describeBadOrder(medic);   // will say "Doctor" — probably not what you wanted
        describeGoodOrder(medic);  // will say "HeadDoctor"

        System.out.println("\n--- hierarchy 2: null falls through to else ---");
        doAction(new FreeCourse());
        doAction(new OptionalCourse());
        doAction(new Course());
        doAction(null);
    }

    /** Wrong order: Doctor check catches HeadDoctor because HeadDoctor IS-A Doctor. */
    private static void describeBadOrder(MedicalStaff medic) {
        if (medic instanceof Nurse) {
            System.out.println("  describeBadOrder → Nurse");
        } else if (medic instanceof Doctor) {
            System.out.println("  describeBadOrder → Doctor");     // ← the bug: HeadDoctor matches here
        } else if (medic instanceof HeadDoctor) {
            System.out.println("  describeBadOrder → HeadDoctor"); // never reached
        }
    }

    /** Correct order: most-specific first. */
    private static void describeGoodOrder(MedicalStaff medic) {
        if (medic instanceof HeadDoctor) {
            System.out.println("  describeGoodOrder → HeadDoctor");
        } else if (medic instanceof Doctor) {
            System.out.println("  describeGoodOrder → Doctor");
        } else if (medic instanceof Nurse) {
            System.out.println("  describeGoodOrder → Nurse");
        }
    }

    private static void doAction(Course course) {
        if (course instanceof FreeCourse) {
            System.out.println("  doAction → FreeCourse (and Course, BaseCourse)");
        } else if (course instanceof OptionalCourse) {
            System.out.println("  doAction → OptionalCourse");
        } else if (course instanceof BaseCourse) {
            System.out.println("  doAction → BaseCourse");
        } else if (course instanceof Course) {
            System.out.println("  doAction → Course");
        } else {
            System.out.println("  doAction → null");
        }
    }
}
