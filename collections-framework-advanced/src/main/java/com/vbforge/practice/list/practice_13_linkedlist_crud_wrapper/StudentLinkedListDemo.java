package com.vbforge.practice.list.practice_13_linkedlist_crud_wrapper;


/**
 * Small driver added for {@link StudentLinkedList}: the wrapper had no {@code main}, so it could not be run.
 */
public class StudentLinkedListDemo {
    public static void main(String[] args) {
        StudentLinkedList list = new StudentLinkedList();
        list.addStudent(new StudentLinkedList.Student(1, "Anna", 4.5));
        list.addStudent(new StudentLinkedList.Student(2, "Bob", 3.9));
        list.addStudent(new StudentLinkedList.Student(3, "Cara", 4.1));
        System.out.println("all:      " + list.getAllStudents());
        System.out.println("by index: " + list.getStudentByIndex(1));
        System.out.println("by id:    " + list.getStudentById(3));
        list.updateStudent(new StudentLinkedList.Student(2, "Bob", 4.0));
        System.out.println("updated:  " + list.getStudentByIndex(1));
        list.removeStudent();                 // LinkedList.remove() = removeFirst()
        System.out.println("after removeStudent():        " + list.getAllStudents());
        list.removeStudentByIndex(0);
        System.out.println("after removeStudentByIndex(0): " + list.getAllStudents() + ", size=" + list.getSize());
    }
}
