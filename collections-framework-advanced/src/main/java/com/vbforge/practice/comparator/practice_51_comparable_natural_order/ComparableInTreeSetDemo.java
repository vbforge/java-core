package com.vbforge.practice.comparator.practice_51_comparable_natural_order;

import java.util.*;

public class ComparableInTreeSetDemo {
    public static void main(String[] args) {

        List<Person> personList = new ArrayList<>();
        Set<Person> personSet = new TreeSet<>();

        addElements(personList);
        addElements(personSet);

        Collections.sort(personList);

        System.out.println("List sorted: " + personList);
        System.out.println("Set as TreeSet" + personSet);
    }

    //helper method
    private static void addElements(Collection<Person> collection){
        collection.add(new Person(1, "Bobby"));
        collection.add(new Person(2, "Bob"));
        collection.add(new Person(3, "Katy"));
        collection.add(new Person(4, "George"));
    }

    //model class
    static class Person implements Comparable<Person>{
        private final int id;
        private final String name;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Person person = (Person) object;
            return id == person.id && Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return id +":" + name;
        }

        //if compare by id:
        /*@Override
        public int compareTo(Person o) {
            if(this.getId() > o.getId()) return 1;
            else if(this.getId() < o.getId()) return -1;
            else return 0;
        }*/


        //if compare by name length:
        @Override
        public int compareTo(Person o) {
            if(this.getName().length() > o.getName().length()) return 1;
            else if(this.getName().length() < o.getName().length()) return -1;
            else return 0;
        }
    }

}
