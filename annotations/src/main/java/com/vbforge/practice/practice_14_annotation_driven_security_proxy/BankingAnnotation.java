package com.vbforge.practice.practice_14_annotation_driven_security_proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BankingAnnotation {

    SecurityLevelType securityLevel() default SecurityLevelType.MEDIUM;

}
