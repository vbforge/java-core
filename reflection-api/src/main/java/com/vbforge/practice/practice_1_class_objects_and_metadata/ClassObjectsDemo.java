package com.vbforge.practice.practice_1_class_objects_and_metadata;

/**
 * Three ways to obtain a Class object. Every loaded class has exactly one Class object,
 * so all three ways return the very same instance.
 *   1. ClassName.class           - compile-time literal, no instance needed
 *   2. instance.getClass()       - runtime type of an existing object
 *   3. Class.forName("pkg.Name") - dynamic lookup by fully-qualified name (name may come from config/DB)
 */
public class ClassObjectsDemo {

    public static void main(String[] args) throws ClassNotFoundException {
        Class<Person> classLiteral = Person.class;
        System.out.println("Via .class literal  : " + classLiteral.getName());

        Person person = new Person("Alice", 30);
        Class<?> classFromInstance = person.getClass();
        System.out.println("Via getClass()      : " + classFromInstance.getName());

        Class<?> classFromName = Class.forName(
                "com.vbforge.reflection_api.practice.practice_1_class_objects_and_metadata.Person");
        System.out.println("Via Class.forName() : " + classFromName.getName());

        // addition: shows that the three ways give the same Class object
        System.out.println("literal == getClass() : " + (classLiteral == classFromInstance));
        System.out.println("literal == forName()  : " + (classLiteral == classFromName));
    }
}
