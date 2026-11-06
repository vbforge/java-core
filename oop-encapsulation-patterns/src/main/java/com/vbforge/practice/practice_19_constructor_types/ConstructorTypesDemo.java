package com.vbforge.practice.practice_19_constructor_types;

public class ConstructorTypesDemo {

    public static void main(String[] args) {
        Book b1 = new Book();
        Book b2 = new Book("Clean Code", "Robert Martin");
        Book b3 = new Book("Effective Java", "Joshua Bloch", 412, "Programming");
        Book b4 = new Book(b3);

        System.out.println("no-arg: " + b1);
        System.out.println("2-arg:  " + b2);
        System.out.println("full:   " + b3);
        System.out.println("copy:   " + b4);
        System.out.println("b3 == b4: " + (b3 == b4) + "   <- different objects");
    }
}
