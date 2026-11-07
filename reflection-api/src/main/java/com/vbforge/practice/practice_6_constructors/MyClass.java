package com.vbforge.practice.practice_6_constructors;

/** Target with two public constructors: no-arg and (int, String). */
public class MyClass {
    private int number;
    private String name = "default_name";

    public MyClass(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public MyClass() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void printData() {
        System.out.println(number + name);
    }

    @Override
    public String toString() {
        return "MyClass{number=" + number + ", name='" + name + "'}";
    }
}
