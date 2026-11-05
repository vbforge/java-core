package com.vbforge.practice.practice_7_retention;

import java.lang.annotation.Annotation;

public class RetentionDemo {
    public static void main(String[] args) {
        AnnotatedClass anAnnotatedClass = new AnnotatedClass();
        Annotation[] annotations = anAnnotatedClass.getClass().getAnnotations();

        System.out.println("(RunTime): " + annotations.length);
        for (int i = 0; i < annotations.length; i++) {
            System.out.println((i + 1) + ": " + annotations[i]);
        }
    }
}

/*В этом примере мы создали свою собственную аннотацию RetentionAnnotation, а также использовали
аннотацию @Deprecated, которая также имеет политику RetentionPolicy.RUNTIME.
Если мы исправим политику аннотации RetentionAnnotation с RetentionPolicy.RUNTIME на
RetentionPolicy.SOURCE, то программа отобразит только одну аннотацию deprecated,
поскольку аннотация с RetentionPolicy.SOURCE во время компиляции будет удалена.*/
