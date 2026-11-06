package com.vbforge.practice.practice_21_constructor_dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Constructor injection: the object receives what it depends on from outside,
 * so every field is set and the dependency can be swapped (here: where the info line is logged).
 * The Consumer is only a stand-in for "a dependency".
 */
public class ConstructorDependencyDemo {

    static final class Human {
        private final String gender;
        private final String name;
        private final int    age;
        private final Consumer<String> logger;

        Human(String gender, String name, int age, Consumer<String> logger) {
            this.gender = gender;
            this.name   = name;
            this.age    = age;
            this.logger = logger;
        }

        String getGender() { return gender; }
        String getName()   { return name; }
        int    getAge()    { return age; }

        void printInfo() {
            String info = gender + ", " + name + ", " + age;
            System.out.println(info);
            logger.accept(info);
        }
    }

    public static void main(String[] args) {
        List<String> log = new ArrayList<>();

        Human h = new Human("Male", "John", 28, log::add);
        h.printInfo();

        System.out.println("captured by the injected dependency: " + log);
    }
}
