package com.vbforge.practice.list.practice_13_linkedlist_crud_wrapper;

import java.util.LinkedList;
import java.util.List;

public class StudentLinkedList {

    private final LinkedList<Student> students;

    public StudentLinkedList() {
        this.students = new LinkedList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public Student getStudentByIndex(int index) {
        if(index < 0 || index >= getSize()){
            return null;
        }
        return students.get(index);
    }

    public Student getStudentById(int id) {
        for (Student student : students) {
            if(student.id() == id){
                return student;
            }
        }
        throw new IllegalArgumentException("Student with id: " + id + " not found");
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public void updateStudent(Student updatedStudent) {
        if (isEmpty()) return;

        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).id() == updatedStudent.id()) {
                students.set(i, updatedStudent);
                return;
            }
        }
    }

    public void removeStudent() {
        if(isEmpty()){
            return;
        }
        students.remove();
    }

    public void removeStudentByIndex(int index) {
        if(isEmpty()){
            return;
        }
        students.remove(index);
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    public int getSize() {
        return students.size();
    }

    public record Student(int id, String name, double gpa) {
    }

}
