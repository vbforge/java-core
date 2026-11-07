package com.vbforge.practice.practice_11_custom_exceptions;

/**
 * A small domain example: validating a student's mark.
 *
 * A mark outside 0..100 is a LOGICAL error (not a system error).
 * A custom checked exception (`MarkException`) forces the caller to
 * handle the invalid-input case.
 */
public class MarkExceptionDemo {

    static class MarkException extends Exception {
        @Override
        public String getMessage() {
            return "Mark must be between 0 and 100";
        }
    }

    static class Student {
        private final String firstName;
        private final int group;
        private double mark;

        Student(String firstName, int group) {
            this.firstName = firstName;
            this.group = group;
        }

        void setMark(double mark) throws MarkException {
            if (mark < 0 || mark > 100) {
                throw new MarkException();
            }
            this.mark = mark;
        }

        double getMark() { return mark; }
    }

    public static void main(String[] args) {
        Student john = new Student("John", 505);

        try {
            john.setMark(101);
            System.out.println("mark = " + john.getMark());
        } catch (MarkException e) {
            System.out.println("rejected: " + e.getMessage());
        }

        try {
            john.setMark(87);
            System.out.println("accepted: mark = " + john.getMark());
        } catch (MarkException e) {
            System.out.println("rejected: " + e.getMessage());
        }
    }
}
