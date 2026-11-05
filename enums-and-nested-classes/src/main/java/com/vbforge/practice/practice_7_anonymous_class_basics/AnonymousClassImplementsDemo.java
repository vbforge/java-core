package com.vbforge.practice.practice_7_anonymous_class_basics;

/** Named implementation of an interface vs an anonymous implementation declared inline. */
public class AnonymousClassImplementsDemo {

    public static void main(String[] args) {
        Eatable eatable = new AnimalEatable();
        eatable.eat();

        System.out.println("other way:");
        Eatable eatable1 = new Eatable() {
            @Override
            public void eat() {
                System.out.println("Eatable eatable1 = new Eatable()");
            }
        };
        eatable1.eat();
    }
}

interface Eatable {
    void eat();
}

class AnimalEatable implements Eatable {
    @Override
    public void eat() {
        System.out.println("Animal implements Eatable-->eat()");
    }
}
