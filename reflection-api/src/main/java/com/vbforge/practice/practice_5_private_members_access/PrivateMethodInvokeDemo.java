package com.vbforge.practice.practice_5_private_members_access;

import java.lang.reflect.Method;

/** Calling a private instance method (printData) through reflection. */
public class PrivateMethodInvokeDemo {

    public static void main(String[] args) throws Exception {
        MyClass myClass = new MyClass();
        myClass.setName("new value");

        System.out.println("access to the private method printData():");
        Method method = myClass.getClass().getDeclaredMethod("printData");
        method.setAccessible(true);
        method.invoke(myClass);
    }
}
