package com.vbforge.practice.practice_6_reference_assignment;

/**
 * A reference variable stores the address of an object, not the object itself.
 * Assigning one reference to another copies the address - both variables
 * then point to the SAME object, so mutations through either are visible.
 */
public class ReferenceAssignmentDemo {

    static class Dog {
        String name;
        int age;

        Dog(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    public static void main(String[] args) {
        Dog thisDog = new Dog("Sparky", 2);
        Dog thatDog = thisDog;                       // copies the reference

        System.out.println("thisDog: " + thisDog.name + ", " + thisDog.age);
        System.out.println("thatDog: " + thatDog.name + ", " + thatDog.age);

        thatDog.name = "Wolfy";
        thatDog.age  = 1;

        System.out.println("\nafter mutating through thatDog:");
        System.out.println("thisDog: " + thisDog.name + ", " + thisDog.age);
        System.out.println("thatDog: " + thatDog.name + ", " + thatDog.age);
        System.out.println("same object (==): " + (thisDog == thatDog));
        System.out.println("same hashCode:    " + (thisDog.hashCode() == thatDog.hashCode()));
    }
}
