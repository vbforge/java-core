package com.vbforge.practice.practice_6_constructors;

/** getConstructor(paramTypes).newInstance(args): create an object through a chosen constructor. */
public class GetConstructorDemo {

    public static void main(String[] args) throws Exception {
        Class<?> cl = Class.forName("com.vbforge.reflection_api.practice.practice_6_constructors.MyClass");
        Class<?>[] params = {int.class, String.class};
        MyClass myClass = (MyClass) cl.getConstructor(params).newInstance(100, "new name AAA");
        System.out.println(myClass);
    }
}
