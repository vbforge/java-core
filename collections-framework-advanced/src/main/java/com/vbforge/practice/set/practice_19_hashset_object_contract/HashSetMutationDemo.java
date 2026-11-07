package com.vbforge.practice.set.practice_19_hashset_object_contract;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Mutating objects that participate in equals()/hashCode() while they are
 * inside a HashSet — the two correct patterns:
 *
 *   Scene 1: object is ALREADY in the set.
 *            → remove from set FIRST, then mutate, then re-add.
 *            (Mutating in place first corrupts the set — contains(oldKey)
 *            will return false because the hash bucket no longer matches.)
 *
 *   Scene 2: object was logically equal to an existing member (so it never
 *            really entered the set), then mutated to become unique.
 *            → mutate, then add. The add succeeds because the object is now
 *            distinct.
 */
public class HashSetMutationDemo {

    public static void main(String[] args) {
        mutateStoredObjectScene();
        mutateRejectedObjectScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — mutate a stored object: remove → mutate → re-add
    // ─────────────────────────────────────────────────────────────
    private static void mutateStoredObjectScene() {
        System.out.println("=== Scene 1: mutate a stored object (safe pattern) ===");

        Set<Person> persons = new HashSet<>();
        Person p1 = new Person("John", 20);
        Person p2 = new Person("John", 20); // logically equal to p1
        Person p3 = new Person("Jane", 21);

        persons.add(p1);
        persons.add(p2); // not added — equals p1
        persons.add(p3);

        System.out.println("--- HashCodes ---");
        System.out.println("p1: " + p1.hashCode());
        System.out.println("p2: " + p2.hashCode());
        System.out.println("p3: " + p3.hashCode());

        System.out.println("--- Initial Contains Check ---");
        System.out.println("Contains p1: " + persons.contains(p1));
        System.out.println("Contains p2: " + persons.contains(p2)); // true, because it’s equal to p1
        System.out.println("Contains p3: " + persons.contains(p3));

        System.out.println("--- Initial Set Content ---");
        System.out.println("Persons: " + persons);

        // Step 2: Mutate p1 safely
        System.out.println("--- Safe Mutation of p1 ---");
        if (persons.contains(p1)) {
            persons.remove(p1); // remove using original hash
            System.out.println("p1 removed successfully.");
        } else {
            System.out.println("WARNING: p1 not found for removal!");
        }

        // Safe to mutate after removal
        p1.setName("Johnny");
        System.out.println("p1 mutated to: " + p1);

        // Re-add mutated object
        persons.add(p1);
        System.out.println("p1 re-added to the set.");

        System.out.println("--- Final Set Content ---");
        System.out.println("Persons: " + persons);

        System.out.println("--- Final Contains Check ---");
        System.out.println("Contains p1: " + persons.contains(p1)); // true
        System.out.println("Contains p2: " + persons.contains(p2)); // false — p1 changed
        System.out.println("Contains p3: " + persons.contains(p3)); // true
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 — mutate an object that was rejected, then add
    // ─────────────────────────────────────────────────────────────
    private static void mutateRejectedObjectScene() {
        System.out.println("\n=== Scene 2: mutate a rejected object, then re-add ===");

        Set<Person> persons = new HashSet<>();

        Person p1 = new Person("John", 20);
        Person p2 = new Person("John", 20); // logically equal to p1
        Person p3 = new Person("Jane", 21);

        // Step 1: Initial add
        persons.add(p1);
        persons.add(p2); // not added (equals p1)
        persons.add(p3);

        System.out.println("--- Initial Set Content ---");
        System.out.println("Persons: " + persons);

        // Step 2: Mutate p2 to make it unique
        System.out.println("--- Mutate p2 ---");
        p2.setName("Johnny");
        System.out.println("New hashCode of p2: " + p2.hashCode());
        System.out.println("p2 equals p1? " + p2.equals(p1)); // false now

        // Step 3: Add mutated p2
        persons.add(p2); // accepted now
        System.out.println("--- Final Set Content ---");
        System.out.println("Persons: " + persons);
    }

    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public void setName(String name) { this.name = name; }
        public void setAge(int age) { this.age = age; }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + (name == null ? 0 : name.hashCode());
            result = 31 * result + age;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Person other = (Person) obj;
            return age == other.age && Objects.equals(name, other.name);
        }

        @Override
        public String toString() {
            return String.format("(%s, age: %d)", name, age);
        }
    }
}
