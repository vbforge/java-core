package com.vbforge.practice.queue.practice_42_priority_queue_comparators;


/**
 * Driver added for {@link PriorityQueueStudentComparator}: the wrapper had no {@code main}.
 * Highest GPA is served first (comparator is descending by gpa).
 */
public class PriorityQueueStudentComparatorDemo {
    public static void main(String[] args) {
        PriorityQueueStudentComparator queue = new PriorityQueueStudentComparator();
        queue.addStudent(new PriorityQueueStudentComparator.Student(1, "Anna", 3.7));
        queue.addStudent(new PriorityQueueStudentComparator.Student(2, "Bob", 4.5));
        queue.addStudent(new PriorityQueueStudentComparator.Student(3, "Cara", 4.1));
        System.out.println("peek: " + queue.peek() + ", size=" + queue.getSize());
        while (!queue.isEmpty()) {
            System.out.println("next: " + queue.removeStudent());
        }
    }
}
