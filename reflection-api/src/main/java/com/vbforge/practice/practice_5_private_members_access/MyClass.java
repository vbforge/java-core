package com.vbforge.practice.practice_5_private_members_access;

/** Target with private state and a private method; there is intentionally no getter for name. */
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
