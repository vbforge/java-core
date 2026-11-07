package com.vbforge.practice.practice_5_private_members_access;

import java.lang.reflect.Field;

/** A private field: getDeclaredField + setAccessible(true), then read and write it. */
public class PrivateFieldAccessDemo {

    public static void main(String[] args) throws Exception {
        MyClass myClass = new MyClass();

        Field field = myClass.getClass().getDeclaredField("name");
        field.setAccessible(true);

        String name = (String) field.get(myClass);
        System.out.println("read : number: " + myClass.getNumber() + "; name: " + name);

        field.set(myClass, "new value");
        System.out.println("write: " + field.get(myClass));
        System.out.println("object: " + myClass);
    }
}
