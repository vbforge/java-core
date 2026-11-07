package com.vbforge.practice.practice_6_constructors;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;

/**
 * getDeclaredConstructors() + getParameterTypes(): list every constructor signature.
 * The array order is unspecified, so the demo sorts by parameter count.
 */
public class ListConstructorsDemo {

    public static void main(String[] args) {
        Constructor<?>[] constructors = MyClass.class.getDeclaredConstructors();
        Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));

        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            System.out.println(Arrays.toString(parameterTypes));
            for (Class<?> parameterType : parameterTypes) {
                System.out.println(parameterType.getName());
            }
            System.out.println("----------");
        }
    }
}
