package com.vbforge.practice.set.practice_25_treeset_custom_objects;

import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;

/**
 * TreeSet with custom objects — Comparable vs Comparator, and the
 * heterogeneous ClassCastException trap when neither is provided.
 */
public class TreeSetCustomObjectsDemo {

    public static void main(String[] args) {
        comparableScene();
        comparatorByNameScene();
        comparatorByAgeThenNameScene();
        heterogeneousClassCastScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — natural ordering via Comparable (by age)
    // ─────────────────────────────────────────────────────────────
    private static void comparableScene() {
        System.out.println("=== Scene 1: Comparable (natural order by age) ===");

        TreeSet<Person> peopleByAge = new TreeSet<>();
        peopleByAge.add(new Person("Alice", 30));
        peopleByAge.add(new Person("Bob", 25));
        peopleByAge.add(new Person("Charlie", 35));
        peopleByAge.add(new Person("Bob", 25)); // Duplicate by age — will be ignored

        for (Person p : peopleByAge) {
            System.out.println(p);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 — Comparator (by name)
    // ─────────────────────────────────────────────────────────────
    private static void comparatorByNameScene() {
        System.out.println("\n=== Scene 2: Comparator (by name) ===");

        TreeSet<Person> peopleByName = new TreeSet<>(Comparator.comparing(Person::getName));
        peopleByName.add(new Person("Alice", 30));
        peopleByName.add(new Person("Bob", 25));
        peopleByName.add(new Person("Charlie", 35));
        peopleByName.add(new Person("Bob", 40)); // "Bob" already exists by name → treated as duplicate

        for (Person p : peopleByName) {
            System.out.println(p);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 3 — Comparator (by age, then by name — full tiebreak)
    // ─────────────────────────────────────────────────────────────
    private static void comparatorByAgeThenNameScene() {
        System.out.println("\n=== Scene 3: Comparator (age, then name) ===");

        TreeSet<Person2> peopleByAge = new TreeSet<>(
                Comparator.comparing(Person2::getAge).thenComparing(Person2::getName));
        peopleByAge.add(new Person2("Alice", 30));
        peopleByAge.add(new Person2("Bob", 25));
        peopleByAge.add(new Person2("Charlie", 35));
        peopleByAge.add(new Person2("Bob", 25)); // Duplicate by age+name — will be ignored

        for (Person2 p : peopleByAge) {
            System.out.println(p);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 4 — heterogeneous TreeSet → ClassCastException
    // ─────────────────────────────────────────────────────────────
    private static void heterogeneousClassCastScene() {
        System.out.println("\n=== Scene 4: heterogeneous TreeSet → ClassCastException ===");

        TreeSet treeSet = new TreeSet();
        treeSet.add("3");
        try {
            treeSet.add(1);   // ClassCastException: Integer is not Comparable to String
            treeSet.add(2);
            System.out.println(treeSet);
        } catch (ClassCastException cce) {
            System.out.println("ClassCastException (expected) — TreeSet needs a single Comparable domain");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Support types
    // ─────────────────────────────────────────────────────────────

    /** Has Comparable — natural order by age. */
    static class Person implements Comparable<Person>{
        String name;
        int age;

        public Person(String name, int age){
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age && Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        @Override
        public String toString() {
            return String.format("(%s, age: %d)", name, age);
        }

        // Used by TreeSet if no Comparator is provided
        @Override
        public int compareTo(Person o) {
            return Integer.compare(age, o.age); //sort by age
        }
    }

    /** No Comparable — must be used with a Comparator (Scene 3). */
    static class Person2 {
        String name;
        int age;

        public Person2(String name, int age){
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Person2 person = (Person2) o;
            return age == person.age && Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        @Override
        public String toString() {
            return String.format("(%s, age: %d)", name, age);
        }
    }
}
