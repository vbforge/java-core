package com.vbforge.practice.practice_11_marker_interface_vs_annotation;

/**
 * Marker interface vs marker annotation .
 *   1. An interface defines a TYPE - the compiler rejects an object that does not implement MyMark.
 *   2. An annotation does NOT define a type and is NOT inherited (no @Inherited) -
 *      the check happens only at runtime, and Child fails it although Parent passes.
 */
public class MarkerInterfaceVsAnnotationDemo {

    public static void main(String[] args) {
        scene1InterfacesDefineAType();
        scene2AnnotationIsNotInherited();
    }

    // -----------------------------------------------------------------------
    // Scene 1 - interfaces define a type
    // -----------------------------------------------------------------------
    static void scene1InterfacesDefineAType() {
        System.out.println("=== Scene 1: interfaces define a type ===");
        MarkedClass markedClass = new MarkedClass();
        NonMarkedClass nonMarkedClass = new NonMarkedClass();
        //Interfaces define a type:
        test(markedClass);
        //test(nonMarkedClass); //требуемым типом для метода test() является MyMark,
        // а мы передаем тип NonMarkedClass
    }

    static void test(MyMark markedObject){
        System.out.println("Method 'test' is completed");
    }

    // -----------------------------------------------------------------------
    // Scene 2 - the interface is inherited by Child, the annotation is not
    // (the last check fails on purpose and is caught - see the comment at the bottom)
    // -----------------------------------------------------------------------
    static void scene2AnnotationIsNotInherited() {
        System.out.println("\n=== Scene 2: interface inherited, annotation not ===");
        Parent parent = new Parent();
        Child child = new Child();

        testInterface(parent);
        testInterface(child);

        testAnnotation(parent);
        try {
            testAnnotation(child);   // fails on purpose: @MyAnnotation is not inherited
        } catch (RuntimeException e) {
            System.out.println("Runtime check failed: " + e.getMessage());
        }
    }

    public static void testInterface(MyMark markedObject){
        System.out.println("Method 'testInterface' is completed");
    }
    public static void testAnnotation(Object object){
        if(!object.getClass().isAnnotationPresent(MyAnnotation.class)){
            throw new RuntimeException("Object is not annotated by '@MyAnnotation'");
        }
        System.out.println("Method 'testAnnotation' is completed");
    }
}

/*Вызов метода testAnnotation (child) на этапе исполнения генерирует исключение, сообщая,
что объект не аннотирован аннотацией MyAnnotation, которой был аннотирован его родительский
класс Parent. Для успешного выполнения классу Child также необходимо быть аннотированным MyAnnotation (либо MyAnnotation должна быть @Inherited).*/
