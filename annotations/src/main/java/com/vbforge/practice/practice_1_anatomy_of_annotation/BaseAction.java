package com.vbforge.practice.practice_1_anatomy_of_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation with two REQUIRED elements (no defaults): every use must supply both. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BaseAction {

    int level();

    String sqlRequest();
}
