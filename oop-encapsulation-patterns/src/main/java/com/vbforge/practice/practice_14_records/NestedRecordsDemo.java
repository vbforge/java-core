package com.vbforge.practice.practice_14_records;

/** A record component can itself be a record. */
public class NestedRecordsDemo {

    public static void main(String[] args) {
        Person person = new Person("Alice", 30,
                new Address("123 Main St", "Springfield", "US"));
        System.out.println(person);
        System.out.println("City: " + person.address().city());
    }
}
