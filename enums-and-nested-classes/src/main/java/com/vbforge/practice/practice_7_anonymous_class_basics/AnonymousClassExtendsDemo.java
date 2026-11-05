package com.vbforge.practice.practice_7_anonymous_class_basics;

/** Three ways to specialise eat(): the regular class, a named subclass, an anonymous subclass. */
public class AnonymousClassExtendsDemo {

    public static void main(String[] args) {
        Animal animal = new Animal();
        animal.eat();
        Animal otherAnimal = new OtherAnimal();
        otherAnimal.eat();
        Animal animal1 = new Animal() {
            @Override
            public void eat() {
                System.out.println("anonymous Animal.eat();");
            }
        };
        animal1.eat();
    }
}

class Animal {

    public void eat() {
        System.out.println("Animal.eat();");
    }
}

class OtherAnimal extends Animal {
    @Override
    public void eat() {
        System.out.println("OtherAnimal.eat();");
    }
}
