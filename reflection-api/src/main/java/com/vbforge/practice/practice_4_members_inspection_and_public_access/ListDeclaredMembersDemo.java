package com.vbforge.practice.practice_4_members_inspection_and_public_access;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * getDeclaredMethods() / getDeclaredFields(): every member of THIS class (private included, inherited excluded).
 * The order of the returned arrays is unspecified, so the demo sorts them by name.
 */
public class ListDeclaredMembersDemo {

    public static void main(String[] args) {
        Class<Player> cl = Player.class;

        Method[] methods = cl.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        System.out.println("--- methods: ---");
        for (Method item : methods) {
            System.out.println(item);
        }

        Field[] fields = cl.getDeclaredFields();
        Arrays.sort(fields, Comparator.comparing(Field::getName));
        System.out.println("--- fields: ---");
        for (Field item : fields) {
            System.out.println(item);
        }
    }
}
