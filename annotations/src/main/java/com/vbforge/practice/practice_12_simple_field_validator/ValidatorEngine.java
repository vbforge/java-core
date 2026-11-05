package com.vbforge.practice.practice_12_simple_field_validator;

import java.lang.reflect.Field;

public class ValidatorEngine {
    public void validate(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyValidator.class)) {
                MyValidator myValidator = field.getDeclaredAnnotation(MyValidator.class);
                try {
                    field.setAccessible(true);
                    Object raw = field.get(obj);
                    if (!(raw instanceof Number number)) {
                        throw new IllegalStateException("@MyValidator supports numeric fields only: " + field.getName());
                    }
                    if (number.longValue() <= 0) {
                        throw new RuntimeException(myValidator.errorMessage());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
