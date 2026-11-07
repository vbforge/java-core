package com.vbforge.practice.practice_3_instantiate_by_name;

/**
 * Creating objects from a class NAME: Class.forName(name) + no-arg constructor + cast.
 * Each call creates a new object; the cast is the only place the compiler can check anything.
 * (Class.newInstance() is deprecated since Java 9: getDeclaredConstructor().newInstance() is the replacement.)
 */
public class InstantiateByNameDemo {

    private static final String PERSON_NAME =
            "com.vbforge.reflection_api.practice.practice_3_instantiate_by_name.Person";

    public static void main(String[] args) throws Exception {
        Class<?> aClass = Class.forName(PERSON_NAME);

        Object obj1 = aClass.getDeclaredConstructor().newInstance();
        Person p1 = (Person) obj1;
        Person p2 = (Person) aClass.getDeclaredConstructor().newInstance();

        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        System.out.println("p1 == p2 : " + (p1 == p2));

        // after the cast it is an ordinary object
        p1.setName("John");
        p1.eat();
        System.out.println("p1 = " + p1);

        // the name does not have to be a hard-coded string: refactoring-safe variant
        Class<?> byLiteralName = Class.forName(Person.class.getName());
        Person p3 = (Person) byLiteralName.getDeclaredConstructor().newInstance();
        System.out.println("p3 = " + p3);
    }
}
