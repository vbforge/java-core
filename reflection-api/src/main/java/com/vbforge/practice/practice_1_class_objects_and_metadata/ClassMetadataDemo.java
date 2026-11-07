package com.vbforge.practice.practice_1_class_objects_and_metadata;

import java.lang.reflect.Modifier;
import java.util.Arrays;

/** Class metadata: names, package, modifiers (int flags decoded via Modifier), superclass, interfaces. */
public class ClassMetadataDemo {

    public static void main(String[] args) {
        Class<Person> clazz = Person.class;

        System.out.println("Simple name     : " + clazz.getSimpleName());
        System.out.println("Canonical name  : " + clazz.getCanonicalName());
        System.out.println("Package         : " + clazz.getPackageName());

        int modifiers = clazz.getModifiers();
        System.out.println("Is public       : " + Modifier.isPublic(modifiers));
        System.out.println("Is abstract     : " + Modifier.isAbstract(modifiers));
        System.out.println("Is final        : " + Modifier.isFinal(modifiers));

        System.out.println("Superclass      : " + clazz.getSuperclass());
        System.out.println("Interfaces      : " + Arrays.toString(clazz.getInterfaces()));
    }
}
