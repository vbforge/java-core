package com.vbforge.practice.practice_18_object_associations;

import java.time.LocalDate;

/** Bidirectional association: addPartner links both objects to each other. */
public class PartnerBidirectionalAssociationDemo {

    static final class Human {
        private final char gender;
        private final String firstNames, lastName;
        private Human partner;
        private int yearBirth, monthBirth, dayBirth;

        Human(String firstNames, String lastName, char gender) {
            this.firstNames = firstNames;
            this.lastName   = lastName;
            this.gender     = gender;
        }

        void addPartner(Human other) { this.partner = other; other.partner = this; }
        Human getPartner() { return partner; }
        String getPartnerName() { return partner.getFullName(); }
        String getFullName() { return firstNames + " " + lastName; }

        void setBirthDate(int y, int m, int d) { yearBirth = y; monthBirth = m; dayBirth = d; }

        /** Age as of a given date (passed in, so the result does not depend on today's date). */
        int getAge(LocalDate asOf) {
            int age = asOf.getYear() - yearBirth;
            if (asOf.getMonthValue() < monthBirth
                    || (asOf.getMonthValue() == monthBirth && asOf.getDayOfMonth() < dayBirth)) {
                age--;
            }
            return age;
        }
    }

    public static void main(String[] args) {
        LocalDate asOf = LocalDate.of(2025, 1, 1);

        Human connie = new Human("Connie", "Smith", 'F');
        connie.setBirthDate(1987, 1, 22);
        System.out.println(connie.getFullName() + " is " + connie.getAge(asOf) + " years old");

        Human john = new Human("John", "Brown", 'M');
        john.setBirthDate(1986, 12, 8);
        System.out.println(john.getFullName() + " is " + john.getAge(asOf) + " years old");

        connie.addPartner(john);
        System.out.println(connie.getFullName() + "'s partner is " + connie.getPartnerName());
        System.out.println(john.getFullName() + "'s partner is " + john.getPartnerName());
        System.out.println("links point back to each other: " + (connie.getPartner().getPartner() == connie));
    }
}
