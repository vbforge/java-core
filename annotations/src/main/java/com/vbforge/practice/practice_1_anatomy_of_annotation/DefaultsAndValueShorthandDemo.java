package com.vbforge.practice.practice_1_anatomy_of_annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * Reads @JavaFileInfo from a class (value shorthand + default name) and from a method
 * (both elements set explicitly), using the common AnnotatedElement API.
 */
public class DefaultsAndValueShorthandDemo {

    public static void main(String[] args) throws NoSuchMethodException {

        Class<DemoClass> demoClassObjects = DemoClass.class;
        readAnnotationOn(demoClassObjects);
        Method method = demoClassObjects.getMethod("printString");
        readAnnotationOn(method);

    }

    static void readAnnotationOn(AnnotatedElement element) {
        System.out.println("\nSearching for annotations in: " + element);
        Annotation[] annotations = element.getAnnotations();
        for (Annotation item : annotations) {
            if (item instanceof JavaFileInfo fileInfo) {
                System.out.println("Author: " + fileInfo.name());
                System.out.println("Version: " + fileInfo.value());
            }
        }
    }
}
