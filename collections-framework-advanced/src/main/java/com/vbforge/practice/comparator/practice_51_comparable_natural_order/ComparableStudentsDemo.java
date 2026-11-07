package com.vbforge.practice.comparator.practice_51_comparable_natural_order;

import java.util.ArrayList;
import java.util.List;

public class ComparableStudentsDemo {
    public static void main(String[] args) {

        List<Student> students = new ArrayList<>();
        students.add(new Student("Adel", 21));
        students.add(new Student("Garry", 20));
        students.add(new Student("Shelly", 25));
        students.add(new Student("Shelly", 19));

        students.sort(null);
        students.forEach(System.out::println);

    }

    static class Student implements Comparable<Student>{

        private String name;
        private int age;

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

        @Override
        public int compareTo(Student o) {
            int k = name.compareTo(o.name); //compare by name
            if (k != 0) return k; //not equal return
            return Integer.compare(age, o.age); //in other cases compare by age
        }
    }
}
