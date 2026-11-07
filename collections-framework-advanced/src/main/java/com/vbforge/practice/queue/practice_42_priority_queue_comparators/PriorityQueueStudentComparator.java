package com.vbforge.practice.queue.practice_42_priority_queue_comparators;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityQueueStudentComparator {

    private final PriorityQueue<Student> queue;

    public PriorityQueueStudentComparator() {
        this.queue = new PriorityQueue<>(new StudentComparator());
    }

    protected void addStudent(Student student) {
        queue.add(student);
    }

    public Student removeStudent() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getSize() {
        return queue.size();
    }

    public Student peek() {
        return queue.peek();
    }

    public record Student(int id, String name, double gpa) {
    }

    static class StudentComparator implements Comparator<Student> {

        @Override
        public int compare(Student student1, Student student2) {
            //compare the students based on gpa
            return Double.compare(student2.gpa(), student1.gpa()); //descending order
        }
    }

}
