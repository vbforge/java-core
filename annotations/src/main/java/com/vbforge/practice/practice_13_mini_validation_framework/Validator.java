package com.vbforge.practice.practice_13_mini_validation_framework;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime validator that reads constraint annotations via reflection
 * and enforces them against field values.
 * <p>
 * Steps:
 * 1. getDeclaredFields()            — enumerate all fields
 * 2. setAccessible(true)            — bypass private access
 * 3. field.get(object)              — read the value
 * 4. isAnnotationPresent(...)       — check for constraint annotations
 * 5. getAnnotation(...)             — read constraint parameters
 * 6. evaluate & collect violations
 */
class Validator {

    /**
     * Validates all annotated fields of the given object.
     *
     * @param object the object to validate
     * @return list of error messages; empty if the object is valid
     */
    public static List<String> validate(Object object) throws IllegalAccessException {
        List<String> errors = new ArrayList<>();
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(object);
            String fieldName = field.getName();

            // ---- @NonNull constraint ----
            if (field.isAnnotationPresent(NonNull.class)) {
                if (value == null) {
                    NonNull ann = field.getAnnotation(NonNull.class);
                    errors.add(fieldName + ": " + ann.message());
                    continue; // skip further checks on null field
                }
            }

            if (value == null) continue; // non-constrained null — skip

            // ---- @MinLength constraint ----
            if (field.isAnnotationPresent(MinLength.class) && value instanceof String str) {
                MinLength ann = field.getAnnotation(MinLength.class);
                if (str.isBlank()) {
                    errors.add(fieldName + ": must not be blank");
                } else if (str.length() < ann.value()) {
                    errors.add(fieldName + ": length " + str.length()
                            + " is below minimum " + ann.value());
                }
            }

            // ---- @Range constraint ----
            if (field.isAnnotationPresent(Range.class) && value instanceof Number num) {
                Range ann = field.getAnnotation(Range.class);
                long v = num.longValue();
                if (v < ann.min() || v > ann.max()) {
                    errors.add(fieldName + ": value " + v
                            + " is outside range [" + ann.min() + ", " + ann.max() + "]");
                }
            }
        }

        return errors;
    }
}
