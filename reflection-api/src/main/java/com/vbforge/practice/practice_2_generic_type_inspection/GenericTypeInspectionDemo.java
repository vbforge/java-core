package com.vbforge.practice.practice_2_generic_type_inspection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/** getType() gives the erased type, getGenericType() the parameterized one (ParameterizedType). */
public class GenericTypeInspectionDemo {

    public static void main(String[] args) throws NoSuchFieldException {
        Field field = Container.class.getDeclaredField("items");

        System.out.println("Erased type     : " + field.getType().getSimpleName());

        Type genericType = field.getGenericType();
        System.out.println("Generic type    : " + genericType.getTypeName());

        if (genericType instanceof ParameterizedType pt) {
            Type[] typeArgs = pt.getActualTypeArguments();
            System.out.println("Type argument   : " + typeArgs[0].getTypeName());
        }
    }
}
